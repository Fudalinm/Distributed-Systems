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
    //patrzymy czy zmienic klucz autoryzacji pakietu
    myData.ifAdmin ? myData.tokenAutorizationKey = rand()% 65536: myData.tokenAutorizationKey = myData.lastToken.tokenAutorizationKey;
    Token tokenToSend;
    tokenToSend.tokenAutorizationKey = myData.tokenAutorizationKey;
    if(ifNewMessageToSend){
        tokenToSend.msgType = STRING_MESSAGE;
        memcpy(tokenToSend.message.from,myData.userName,USER_NAME_SIZE);
        memcpy(tokenToSend.message.to,userName,USER_NAME_SIZE);
        memcpy(tokenToSend.message.content,message,MSG_SIZE);
        ifNewMessageToSend = false;
    }else{
        tokenToSend.msgType = EMPTY_TOKEN;
    }
    tokenToSend.msgID = myData.lastToken.msgID + 1;
    myData.lastToken = tokenToSend;
    int a = sendto(myData.nextHopSocket,(char *) (&tokenToSend), sizeof(tokenToSend), 0 ,(sockaddr *) &myData.nextHopAddress, sizeof(myData.nextHopAddress) );
    a == -1 ? printf("%s: Couldn't send\n",myData.userName) : printf("%s: Could send\n",myData.userName);
    myData.ifHaveToken = false;
}

void startUDP(){
    createUDPSockets();
    while(true) {
        sleep(1);
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
    myData.ifAdmin ? myData.tokenAutorizationKey = rand()% 65536: myData.tokenAutorizationKey = myData.lastToken.tokenAutorizationKey;
    myData.lastToken.tokenAutorizationKey = myData.tokenAutorizationKey;

    -1 == sendto(myData.nextHopSocket,(char *) (&myData.lastToken), sizeof(myData.lastToken), 0 ,(sockaddr *) &myData.nextHopAddress, sizeof(myData.nextHopAddress) ) ?
    printf("%s:Couldn't pass the message\n",myData.userName): printf("%s:Could pass the message\n",myData.userName);
    myData.ifHaveToken = false;
    myData.lastMessageId = myData.lastToken.msgID;

}

void registerUDPClient(){
}

//last messageID is changed in sending and resending methods
void serviceUDPReceiving(){
    sleep(1.5);
    Token recvToken;
    recv(myData.listeningSocket,(char*) &recvToken, sizeof(recvToken),0);
    if(recvToken.msgType == REGISTER_MESSAGE){
        registerUDPClient();
    }else {
        sleep(2);
        myData.lastToken = recvToken;
        if(recvToken.msgType == EMPTY_TOKEN){//we can resend empty token or send new message if we have one
            myData.ifHaveToken = true;
            printf("%s: Received empty token\n",myData.userName);
            ifNewMessageToSend ? serviceUDPSending() : serviceUDPResending();
        }else if(strcmp(recvToken.message.to,myData.userName) == 0){//we can send new empty token or new message if we have one
            myData.ifHaveToken = true;
            if(recvToken.msgType == STRING_MESSAGE){
                printf("%s: I received: '%s' now i'm going to send\n",myData.userName,recvToken.message.content);
            }else{
                printf("%s:Message to me but it is unrecognized\n",myData.userName);
            }
            //jezeli odebralem swoj pakiet to teraz powinienem albo wyslac pusty token albo wyslac kolejny jesli mam jaki
            serviceUDPSending();
            //ifNewMessageToSend ? serviceUDPSending() : serviceUDPResending();
        }else{
            if(recvToken.msgID == myData.lastMessageId){
                printf("%s: I need to delete this message: it has already been here\n",myData.userName);
                ifNewMessageToSend ? serviceUDPSending() : serviceUDPResending();
            }else{
                serviceUDPResending();
            }
        }
    }
}