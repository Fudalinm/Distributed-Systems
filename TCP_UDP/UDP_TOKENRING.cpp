//
// Created by fudal on 10.03.2019.
//

#include <Winsock2.h> // before Windows.h, else Winsock 1 conflict
#include <Ws2tcpip.h> // neede for ip_mreq definition for multicast
#include <Windows.h>
#include <iostream>
#include <stdlib.h>
#include <time.h>
#include <unistd.h>
#include "Connectors.h"
#include "structs_def.h"

#include <sys/types.h>
#include <iostream>

void startUDP();
void createUDPSockets();
void serviceUDPResending();
void serviceUDPSending();
void registerUDPClient();
void serviceUDPReceiving();


Connector myData;
bool ifNewMessageToSend = false;
char userName[USER_NAME_SIZE];
char message[MSG_SIZE];



void serviceUDPSending(){
    sleep(2);
    myData.ifAdmin ? myData.tokenAutorizationKey = rand()% 65536: myData.tokenAutorizationKey = myData.lastToken.tokenAutorizationKey;
    Token tokenToSend;
    tokenToSend.tokenAutorizationKey = myData.tokenAutorizationKey;
    if(ifNewMessageToSend){
        tokenToSend.msgType = STRING_MESSAGE;
        ifNewMessageToSend = false;
    }else{
        tokenToSend.msgType = EMPTY_TOKEN;
    }
    memcpy(tokenToSend.message.from,myData.userName,USER_NAME_SIZE);
    memcpy(tokenToSend.message.to,userName,USER_NAME_SIZE);
    memcpy(tokenToSend.message.content,message,MSG_SIZE);

    int a = sendto(myData.nextHopSocket,(char *) (&tokenToSend), sizeof(tokenToSend), 0 ,(sockaddr *) &myData.nextHopAddress, sizeof(myData.nextHopAddress) );
    a == -1 ? printf("%s: Couldn't send\n",myData.userName) : printf("%s: Could send\n",myData.userName);
    myData.ifHaveToken = false;
}

void startUDP(){
    createUDPSockets();
    //printf("Dupa 2\n");
    while(true) {
        sleep(1);
        //printf("Dupa 1\n");
        myData.ifHaveToken == 1 ? serviceUDPSending() : serviceUDPReceiving();
    }
}

void createUDPSockets() {
    SOCKET mainSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);

    sockaddr_in listener;
    memset(&listener, 0, sizeof(listener));
    listener.sin_family = AF_INET;
    listener.sin_addr.s_addr = inet_addr(myData.targetIP);
    listener.sin_port = htons(myData.listeningPort);
    if (bind(mainSocket, (sockaddr *) &listener, sizeof(listener)) < 0) {
        printf("\n%d\n",WSAGetLastError());
        printf("Error while binding in UDP\n");
        exit(EXIT_FAILURE);
    }
    myData.listeningSocket = mainSocket;
    myData.listeningAddress = listener;

    mainSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);

    sockaddr_in sender;
    memset(&sender, 0, sizeof(sender));
    sender.sin_family = AF_INET;
    sender.sin_addr.s_addr = inet_addr(myData.targetIP);
    sender.sin_port = htons(myData.targetPort);



    myData.nextHopAddress = sender;
    myData.nextHopSocket = mainSocket;

}


//we are not incrementing msgID
void serviceUDPResending(){
    sleep(1);
    myData.ifAdmin ? myData.tokenAutorizationKey = rand()% 65536: myData.tokenAutorizationKey = myData.lastToken.tokenAutorizationKey;

    myData.lastToken.tokenAutorizationKey = myData.tokenAutorizationKey;
    0 == sendto(myData.nextHopSocket,(char *) (&myData.lastToken), sizeof(myData.lastToken), 0 ,(sockaddr *) &myData.nextHopAddress, sizeof(myData.nextHopAddress) ) ?
    printf("Przekazanie udało się %s\n",myData.userName): printf("Przekazanie NIE udało się %s\n",myData.userName);

}

void registerUDPClient(){
}

//TODO: Implement user sending interface in new thread
//TODO: WHEN DO I NEED TO DELETE PACKET FORM LOOP???
//TODO: Something logically wrong with this funcion it need to be checke
void serviceUDPReceiving(){
    // TODO: Refactor nextHop socket name
    Token recvToken;
    recv(myData.listeningSocket,(char*) &recvToken, sizeof(recvToken),0);
    if(recvToken.msgType == REGISTER_MESSAGE){
        registerUDPClient();
    }else if(recvToken.msgType == EMPTY_TOKEN){//we can resend empty token or send new message if we have one
        printf("%s: received empty token\n",myData.userName);
        myData.ifHaveToken = true;
        //serviceUDPSending();
    }else{
        //usuwam pakiet wtedy gdy juz wczesniej byl u mnie dokladnie taki sam
        //i nie wyslalem tego pakietu sam do siebie
        if(recvToken.msgID == myData.lastMessageId && recvToken.message.to != myData.userName){//we can send new empty token or new message if we have one
            printf("%s: I need to delete this message: it has already been here\n",myData.userName);
            //acquiring token
            myData.ifHaveToken = true;
            //updating last token received
            myData.lastToken = recvToken;
            myData.lastMessageId = recvToken.msgID;
            //serviceUDPSending();
        }else if(recvToken.message.to == myData.userName){//we can send new empty token or new message if we have one
            //proccessing message
            if(recvToken.msgType == STRING_MESSAGE){
                printf("%s: I received: '%s' now i'm going to send\n",myData.userName,recvToken.message.content);
            }
            //acquiring token
            myData.ifHaveToken = true;
            //updating last token received
            myData.lastToken = recvToken;
            myData.lastMessageId = recvToken.msgID;
            //serviceUDPSending();
        }else{//HERE we need to always resend
            serviceUDPResending();
        }
    }


}