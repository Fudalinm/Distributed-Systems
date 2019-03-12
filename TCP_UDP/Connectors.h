//
// Created by fudal on 10.03.2019.
//

#ifndef TCP_UDP_CONNECTORS_H
#define TCP_UDP_CONNECTORS_H

#include "structs_def.h"



// TODO: Refactor nextHop socket name
class Connector {
public:
    char userName[USER_NAME_SIZE];
    int listeningPort; // ONLY IN TCP
    int targetPort;
    char *targetIP;
    bool ifHaveToken;
    bool ifAdmin;
    int tokenAutorizationKey;
    bool ifTCP;
    int lastMessageId;
    bool ifJoining;
    //char nextHopUserName[USER_NAME_SIZE];
    sockaddr_in nextHopAddress;
    sockaddr_in listeningAddress;
    SOCKET nextHopSocket;
    SOCKET listeningSocket;
    SOCKET tmpListeningSocket;
    Token lastToken;
    void printConnector();

};

void Connector::printConnector() {
    printf("UserName: %s\n"
           "listeningPort: %d\n,"
           "targetPort:%d\n,"
           "targetIP:%s\n,"
           "ifHaveToken:%d\n,"
           "ifAdmin:%d\n,"
           "tokenAutorizationKey:%d\n,"
           "ifTCP%d\n,"
           "lastMessageID:%d\n", userName, listeningPort, targetPort, targetIP, ifHaveToken, ifAdmin,
           tokenAutorizationKey, ifTCP, lastMessageId);
}

Connector myData;
bool ifNewMessageToSend = false;
char userName[USER_NAME_SIZE];
char message[MSG_SIZE];
SOCKET MCAST_SOCKET;



void initMulticast() {
    if ((MCAST_SOCKET = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
        printf("multi socket");
        exit(1);
    }
}

void sendMulticast() {
    struct sockaddr_in addr;
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = inet_addr(MCAST_GRP);
    addr.sin_port = htons(MCAST_PORT);

    if ((sendto(MCAST_SOCKET, myData.userName, sizeof(myData.userName), 0, (struct sockaddr *) &addr, sizeof(addr))) < 0) {
        perror("sendto");
        exit(1);
    }
}

#endif //TCP_UDP_CONNECTORS_H
