//
// Created by fudal on 10.03.2019.
//
#ifndef TCP_UDP_STRUCTS_DEF_H
#define TCP_UDP_STRUCTS_DEF_H
#define MSG_SIZE 255
#define USER_NAME_SIZE 16

#define EMPTY_TOKEN 0
#define STRING_MESSAGE 1
#define REGISTER_MESSAGE 2


#include "structs_utils.cpp"

//void assignDataToMessage();
//void assignDataToToken();


struct RegisterRequest{
    char from[USER_NAME_SIZE];
    int port;
    char ip[16];
};

struct Message{
    char from[USER_NAME_SIZE];
    char to[USER_NAME_SIZE];
    char content[MSG_SIZE];
};

struct Token{
    int msgType;
    int msgID;
    int tokenAutorizationKey;
    Message message;
};


#endif //TCP_UDP_STRUCTS_DEF_H