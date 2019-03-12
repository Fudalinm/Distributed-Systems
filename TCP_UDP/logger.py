#!/usr/bin/env python

import socket
import struct
import datetime
import sys

file = 'log.txt'
if len(sys.argv) > 1:
    file = sys.argv[1]

MCAST_GRP = '127.0.0.2'
MCAST_PORT = 1500

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
sock.bind((MCAST_GRP, MCAST_PORT))
mreq = struct.pack("4sl", socket.inet_aton(MCAST_GRP), socket.INADDR_ANY)

#sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)

while True:
    data, addr = sock.recvfrom(1024)	#bufsize
    print("%s : %s\n" % (str(datetime.datetime.now())[:-7], data.decode('ASCII')))
    with open("log.txt", "a+") as log_file:
        log_file.write("%s : %s\n" % (str(datetime.datetime.now())[:-7], data.decode('ASCII')))