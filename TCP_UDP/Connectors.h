//
// Created by fudal on 10.03.2019.
//

#ifndef TCP_UDP_CONNECTORS_H
#define TCP_UDP_CONNECTORS_H

#include "structs_def.h"

// TODO: Refactor nextHop socket name
class Connector{
    public:
        char userName[USER_NAME_SIZE];
        int listeningPort; // ONLY IN TCP
        int targetPort;
        char* targetIP;
        bool ifHaveToken;
        bool ifAdmin;
        int tokenAutorizationKey;
        bool ifTCP;
        int lastMessageId;

        char nextHopUserName[USER_NAME_SIZE];
        sockaddr_in nextHopAddress;
        sockaddr_in listeningAddress;
        SOCKET nextHopSocket;
        SOCKET listeningSocket;

        Token lastToken;
        void printConnector();

};

void Connector::printConnector(){
    printf("UserName: %s\n"
           "listeningPort: %d\n,"
           "targetPort:%d\n,"
           "targetIP:%s\n,"
           "ifHaveToken:%d\n,"
           "ifAdmin:%d\n,"
           "tokenAutorizationKey:%d\n,"
           "ifTCP%d\n,"
           "lastMessageID:%d\n"
           ,userName,listeningPort,targetPort,targetIP
           ,ifHaveToken,ifAdmin,tokenAutorizationKey,ifTCP,lastMessageId);
}


#endif //TCP_UDP_CONNECTORS_H
