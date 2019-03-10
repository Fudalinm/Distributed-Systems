#include <Winsock2.h> // before Windows.h, else Winsock 1 conflict
#include <Ws2tcpip.h> // neede for ip_mreq definition for multicast
#include <Windows.h>
#include <iostream>
#include <stdlib.h>
#include <time.h>
#include <unistd.h>
#include "Connectors.h"
#include "structs_def.h"
#include <thread>

#include <sys/types.h>
#include "UDP_TOKENRING.cpp"
#include <windows.h>

#include <stdio.h>

#include <conio.h>

void startTCP();
void initializeSocket();



DWORD WINAPI userLoop(LPVOID lpParam){
    while(true){
        sleep(4);
        ifNewMessageToSend = false;
        printf("Give user name to which you want to send a message:\n");
        std::cin >> userName;
        printf("Give message that you want to pass:\n");
        std::cin >> message;
        ifNewMessageToSend = true;
        printf("Now you need to wait for your token\n");
        while(myData.ifHaveToken){
            ;
        }
        printf("Your token has arrived\n");
    }
}

void processArguments(char* argv[]){
    srand(time(NULL));
    memcpy(myData.userName,argv[1],USER_NAME_SIZE);
    myData.listeningPort = atoi(argv[2]);
    myData.targetPort = atoi(argv[3]);
    myData.targetIP = argv[4];
    myData.ifTCP = argv[5] == "TCP";
    myData.ifHaveToken = 0 == strcmp(argv[6],"HAVE_TOKEN\0");
    myData.ifAdmin =  0 == strcmp(argv[6],"HAVE_TOKEN\0");
    myData.tokenAutorizationKey = -1;
    myData.lastMessageId = -1;
    memset( (void*) &(myData.lastToken),0, sizeof(myData.lastToken));

    myData.printConnector();
    initializeSocket();

    DWORD userThreadID;
    CreateThread(NULL,0,userLoop,NULL,0,&userThreadID);

    myData.ifTCP ? startTCP() : startUDP();
}

void startTCP(){
//    mainSocket = socket(AF_INET,SOCK_STREAM,IPPROTO_TCP);
//    checkSocketCreation();
//
//    //create sending socket
//    sockaddr_in nextHop;
//    memset(&nextHop,0,sizeof(nextHop));
//    nextHop.sin_family = AF_INET;
//    nextHop.sin_addr.s_addr = inet_addr(sendingIP);
//    nextHop.sin_port = htons(sendingPort);
}

void initializeSocket(){
    WSADATA wsaData;

    int result = WSAStartup( MAKEWORD( 2, 2 ), & wsaData );
    if( result != NO_ERROR ){
        printf( "Initialization error.\n" );
        exit(EXIT_FAILURE);
    }

}



int main(int argc,char* argv[]) {
    printf("%d",argc);
    if(argc != 7){printf("Bad number of arguments");  exit(EXIT_FAILURE);}
    processArguments(argv);
    return 0;
}