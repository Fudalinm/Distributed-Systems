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

void createTCPSockets();
void serviceTCPSending();
void serviceTCPResending();
void serviceTCPReceiving();


//TODO: implement
void sendTCPRequest();
//TODO: Implement
void registerTCPClient(Message);
//TODO: Implement
void reactTCPToRegisterInfo(Message, Token);


///tutaj akceptujemy i listen
void waitForPreviousHop(){
    printf("%s: I'm waiting for previous node\n",myData.userName);
    listen(myData.listeningSocket,1) < 0 ? printf("%s:Error while listening\n",myData.userName):printf("%sListining is ok\n",myData.userName);
    myData.listeningSocket = accept(myData.listeningSocket, (struct sockaddr *) &myData.listeningAddress, (socklen_t*) sizeof(myData.listeningAddress)) < 0 ?
            printf("%s:Accept not working as expected\n",myData.userName): printf("%s:Accept worked fine\n",myData.userName);
}
///klient zaczynajacy od tej funkcji powinien startowac ostatni
void waitForNextHop(){
    printf("%sWaiting fhor next node\n",myData.userName);
    connect(myData.nextHopSocket, (struct sockaddr *) &myData.nextHopAddress, sizeof(myData.nextHopAddress)) < 0 ? printf("%s: Error while connecting\n",myData.userName) : printf("%s: Connecting ok!");
}

void startTCP() {
        createTCPSockets();

        if (myData.ifJoining) {
            sendTCPRequest();
        }else{
            if(myData.ifHaveToken){
                waitForNextHop();
                waitForPreviousHop();
            }else{
                waitForPreviousHop();
                waitForNextHop();
            }
        }
        while (true) {
            sleep(1);
            myData.ifHaveToken == 1 ? serviceTCPSending() : serviceTCPReceiving();
        }
}

void createTCPSockets(){
    SOCKET mainSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    sockaddr_in listener;
    memset(&listener, 0, sizeof(listener));
    listener.sin_family = AF_INET;
    listener.sin_addr.s_addr = inet_addr(myData.targetIP);
    listener.sin_port = htons(myData.listeningPort);
    if (bind(mainSocket, (sockaddr *) &listener, sizeof(listener)) < 0) {
        printf("\n%d\n", WSAGetLastError());
        printf("Error while binding in TCP\n");
        exit(EXIT_FAILURE);
    }
    myData.tmpListeningSocket = mainSocket;
    myData.listeningAddress = listener;

    mainSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

    sockaddr_in sender;
    memset(&sender, 0, sizeof(sender));
    sender.sin_family = AF_INET;
    sender.sin_addr.s_addr = inet_addr(myData.targetIP);
    sender.sin_port = htons(myData.targetPort);

    myData.nextHopAddress = sender;
    myData.nextHopSocket = mainSocket;
}

void serviceTCPSending(){
    myData.ifAdmin ? myData.tokenAutorizationKey = rand() % 65536
                   : myData.tokenAutorizationKey = myData.lastToken.tokenAutorizationKey;
    Token tokenToSend;
    tokenToSend.tokenAutorizationKey = myData.tokenAutorizationKey;
    if (ifNewMessageToSend) {
        tokenToSend.msgType = STRING_MESSAGE;
        memcpy(tokenToSend.message.from, myData.userName, USER_NAME_SIZE);
        memcpy(tokenToSend.message.to, userName, USER_NAME_SIZE);
        memcpy(tokenToSend.message.content, message, MSG_SIZE);
        ifNewMessageToSend = false;
    } else {
        tokenToSend.msgType = EMPTY_TOKEN;
    }
    tokenToSend.msgID = myData.lastToken.msgID + 1;
    myData.lastToken = tokenToSend;
    send(myData.nextHopSocket,(char*) &tokenToSend, sizeof(Token),0) < 0 ? printf("%s: Couldn't send message\n") : printf("%s: Could send message\n");
    myData.ifHaveToken = false;
}

void serviceTCPResending(){
    myData.ifAdmin ? myData.tokenAutorizationKey = rand() % 65536
                   : myData.tokenAutorizationKey = myData.lastToken.tokenAutorizationKey;
    myData.lastToken.tokenAutorizationKey = myData.tokenAutorizationKey;
    send(myData.nextHopSocket,(char*) &myData.lastToken, sizeof(Token),0) < 0 ? printf("%s: Couldn't send message\n") : printf("%s: Could send message\n");
    myData.ifHaveToken = false;
    myData.lastMessageId = myData.lastToken.msgID;
}


void sendTCPRequest(){

}

void serviceTCPReceiving(){
    sleep(1.5);
    Token recvToken;
    recv(myData.listeningSocket, (char *) &recvToken, sizeof(recvToken), 0);
    if (recvToken.msgType == REGISTER_REQUEST) {
        printf("%s: Received registration request: %s\n", myData.userName, recvToken.message.from);
        registerTCPClient(recvToken.message);
    }else if (recvToken.msgType == REGISTER_INFO) {
        printf("%s: Received registration info: %s\n", myData.userName, recvToken.message.from);
        reactTCPToRegisterInfo(recvToken.message, recvToken);
    }else {
        sleep(2);
        myData.lastToken = recvToken;
        if (recvToken.msgType == EMPTY_TOKEN) {//we can resend empty token or send new message if we have one
            myData.ifHaveToken = true;
            printf("%s: Received empty token\n", myData.userName);
            ifNewMessageToSend ? serviceTCPSending() : serviceTCPResending();
        } else if (strcmp(recvToken.message.to, myData.userName) ==
                   0) {//we can send new empty token or new message if we have one
            myData.ifHaveToken = true;
            if (recvToken.msgType == STRING_MESSAGE) {
                printf("%s: I received: '%s' now i'm going to send\n", myData.userName, recvToken.message.content);
            } else {
                printf("%s:Message to me but it is unrecognized\n", myData.userName);
            }
            serviceTCPSending();
        } else {
            if (recvToken.msgID == myData.lastMessageId) {
                printf("%s: I need to delete this message: it has already been here\n", myData.userName);
                serviceTCPSending(); //we need to always send new message we cannot resend
            } else {
                serviceTCPResending();
            }
        }
    }

}
