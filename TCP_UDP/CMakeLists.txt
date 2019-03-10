cmake_minimum_required(VERSION 3.13)
project(TCP_UDP)

set(CMAKE_CXX_STANDARD 14)

add_executable(TCP_UDP main.cpp structs_def.h Connectors.h structs_utils.cpp UDP_TOKENRING.cpp TCP_TOKENRING.cpp)