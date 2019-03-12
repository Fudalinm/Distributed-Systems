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

//TODO: check
void sendTCPRequest();

//TODO: Check
void registerTCPClient(Message);

//TODO: check
void reactTCPToRegisterInfo(Message, Token);

DWORD WINAPI newClientSignInThread(LPVOID lpParam);


DWORD WINAPI newClientSignInThread(LPVOID lpParam){
    //nasluchujemy na naszym odbiorczym gniezdzie aby zaakceptowac nowego klienta
    while(true){
        printf("##########waiting for a new guy##########\n");
        listen(myData.tmpListeningSocket, 1) < 0 ? printf("%s:Error while listening new Client\n", myData.userName) : printf(
                "%sListining is ok while listening new Client\n", myData.userName);
        (myData.listeningSocket = accept(myData.tmpListeningSocket, NULL,NULL)) < 0 ?
                                 printf("%s:Accept not working as expected\n", myData.userName) : printf(
                        "%s:Accept worked fine\n", myData.userName);
    }
}

void waitForPreviousHop() {
    printf("%s: I'm waiting for previous node\n", myData.userName);
    listen(myData.tmpListeningSocket, 1) < 0 ? printf("%s:Error while listening\n", myData.userName) : printf(
            "%sListining is ok\n", myData.userName);

    (myData.listeningSocket = accept(myData.tmpListeningSocket, NULL,NULL)) < 0 ?
                             printf("%s:Accept not working as expected\n", myData.userName) : printf(
                    "%s:Accept worked fine\n", myData.userName);

    printf("SOCKET: %d\n",myData.listeningSocket);

    ///Why this part of code is broken?
//    myData.listeningSocket = accept(myData.tmpListeningSocket, (struct sockaddr *) &myData.listeningAddress,
//                                    (socklen_t *) sizeof(myData.listeningAddress)) < 0 ?
//                             printf("%s:Accept not working as expected\n", myData.userName) : printf(
//                    "%s:Accept worked fine\n", myData.userName);
}

void waitForNextHop() {
    printf("%sWaiting fhor next node\n", myData.userName);
    connect(myData.nextHopSocket, (struct sockaddr *) &myData.nextHopAddress, sizeof(myData.nextHopAddress)) < 0
    ? printf("%s: Error while connecting\n", myData.userName) : printf("%s: Connecting ok!\n");
}

void startTCP() {
    createTCPSockets();
    DWORD userThreadID;

    if (myData.ifJoining) {
        waitForNextHop();
        sendTCPRequest();
        waitForPreviousHop();
    } else {
        if (myData.ifHaveToken) {
            waitForNextHop();
            waitForPreviousHop();
        } else {
            waitForPreviousHop();
            waitForNextHop();
        }
    }
    CreateThread(NULL, 0, newClientSignInThread, NULL, 0, &userThreadID);
    while (true) {
        sleep(1);
        myData.ifHaveToken == 1 ? serviceTCPSending() : serviceTCPReceiving();
    }
}

void createTCPSockets() {
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

void serviceTCPSending() {
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
    send(myData.nextHopSocket, (char *) &tokenToSend, sizeof(Token), 0) < 0 ? printf("%s: Couldn't send message\n")
                                                                            : printf("%s: Could send message\n");
    myData.ifHaveToken = false;
}

void serviceTCPResending() {
    myData.ifAdmin ? myData.tokenAutorizationKey = rand() % 65536
                   : myData.tokenAutorizationKey = myData.lastToken.tokenAutorizationKey;
    myData.lastToken.tokenAutorizationKey = myData.tokenAutorizationKey;
    send(myData.nextHopSocket, (char *) &myData.lastToken, sizeof(Token), 0) < 0 ? printf("%s: Couldn't REsend message\n")
                                                                                 : printf("%s: Could Resend message\n");
    myData.ifHaveToken = false;
    myData.lastMessageId = myData.lastToken.msgID;
}



//TODO: check
void sendTCPRequest() {
    Token tokenToSend;
    RegisterRequest rq;
    memcpy(rq.from, myData.userName, USER_NAME_SIZE);
    rq.newListeningPort = myData.listeningPort;
    tokenToSend.msgType = REGISTER_REQUEST;
    tokenToSend.tokenAutorizationKey = -10;
    tokenToSend.msgID = -10;
    memcpy((void *) &tokenToSend.message.content, (void *) &rq, sizeof(rq));
    send(myData.nextHopSocket, (char *) &tokenToSend, sizeof(Token), 0) < 0 ? printf("%s: Couldn't send join request\n")
                                                                            : printf("%s: Could send join request\n");
}


//TODO: Check
void registerTCPClient(Message m) {
    char newUserName[USER_NAME_SIZE];
    memcpy(newUserName, m.from, USER_NAME_SIZE);
    RegisterRequest *rq = (RegisterRequest *) &m.content;

    RegisterInfo ri;
    memcpy(ri.from, myData.userName, USER_NAME_SIZE);
    ri.newSendingPort = rq->newListeningPort;
    ri.oldSendingPort = myData.listeningPort;

    Token tokenToSend;
    tokenToSend.msgType = REGISTER_INFO;
    tokenToSend.msgID = -1;
    tokenToSend.tokenAutorizationKey = -10;
    memcpy(tokenToSend.message.from, myData.userName, USER_NAME_SIZE);

    memcpy((void *) &tokenToSend.message.content, (void *) &ri, sizeof(ri));

    send(myData.nextHopSocket, (char *) &tokenToSend, sizeof(Token), 0) < 0 ? printf("%s: Couldn't send message\n")
                                                                            : printf("%s: Could send message\n");
}

//TODO: Check
void reactTCPToRegisterInfo(Message m, Token tokenToSend) {
    RegisterInfo ri;
    memcpy((void *) &ri, (void *) &m.content, sizeof(ri));

    if (ri.oldSendingPort == myData.targetPort) {
        myData.targetPort = ri.newSendingPort;
        SOCKET mainSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
        sockaddr_in sender;
        memset(&sender, 0, sizeof(sender));
        sender.sin_family = AF_INET;
        sender.sin_addr.s_addr = inet_addr(myData.targetIP);
        sender.sin_port = htons(myData.targetPort);
        myData.nextHopAddress = sender;
        myData.nextHopSocket = mainSocket;
        //printf("\nSOCKET1:%d\n", mainSocket);
        connect(myData.nextHopSocket, (struct sockaddr *) &myData.nextHopAddress, sizeof(myData.nextHopAddress)) < 0
        ? printf("%s: Error while connecting to new client\n", myData.userName) : printf("%s: Connecting to new client ok!\n");

        printf("nextHopSocket: %d\n", myData.nextHopAddress);
        printf("target port: %d\n", myData.targetPort);
        printf("%s: Target socket changed\n", myData.userName);
        ///Have no idea why but sending new message solve problems
        ///probably token is lost during client add
        tokenToSend.msgType = STRING_MESSAGE;
        memcpy(tokenToSend.message.content,"Udało mi sie z toba skomunikować\0",255);
        send(myData.nextHopSocket, (char *) &tokenToSend, sizeof(Token), 0) < 0 ? printf("%s: Couldn't send message in register Info\n")
                                                                                : printf("%s: Could send message\n");
    }else {//przesyłamy dalej pakiet
        send(myData.nextHopSocket, (char *) &tokenToSend, sizeof(Token), 0) < 0 ? printf("%s: Couldn't send message in register Info\n")
                                                                                : printf("%s: Could send message\n");
    }
}

void serviceTCPReceiving() {
    sleep(1.5);
    Token recvToken;
    recv(myData.listeningSocket, (char *) &recvToken, sizeof(recvToken), 0) < 0? printf("%s: Nie udalo sie odczytac\n",myData.userName):  printf("%s: Udalo sie odczytac\n",myData.userName);
    if (recvToken.msgType == REGISTER_REQUEST) {
        printf("%s: Received registration request: %s\n", myData.userName, recvToken.message.from);
        registerTCPClient(recvToken.message);
    } else if (recvToken.msgType == REGISTER_INFO) {
        printf("%s: Received registration info: %s\n", myData.userName, recvToken.message.from);
        reactTCPToRegisterInfo(recvToken.message, recvToken);
    } else {
        sleep(2);
        myData.lastToken = recvToken;
        if (recvToken.msgType == EMPTY_TOKEN) {//we can resend empty token or send new message if we have one
            myData.ifHaveToken = true;
            printf("%s: Received empty token\n", myData.userName);
            ifNewMessageToSend ? serviceTCPSending() : serviceTCPResending();
        } else if (strcmp(recvToken.message.to, myData.userName) ==0) {//we can send new empty token or new message if we have one
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
