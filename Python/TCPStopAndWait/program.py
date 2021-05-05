# Garett Stage
# stage043 - 5166169
# 3/19/18

from socket import *
from thread import *
import hashlib
import select
import sys
import os

class TCPClient:

    def __init__(self, host, port, filename):
        print("Connecting to network... ")
        self.host = host
        self.port = port
        self.setup_socket()
        self.file = filename
        self.start_tcp()

    #Connect to the network
    def setup_socket(self):
        self.sock = socket(AF_INET, SOCK_STREAM)
        self.sock.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
        self.sock.connect((self.host, self.port))
        print("Connected to: " + str(self.host) + " on port: " + str(self.port))

    #checksum for TCP
    def checksum(self, data):
        csum = 0
        #Add all byte values together
        for i in range(0, len(data)-1, 2):
            word = (ord(data[i]) << 8) + (ord(data[i+1]))
            csum += word
        #Create a mask to one's compliment any size word
        mask = (1 << csum.bit_length()) - 1
        #One's compliment, then return the hash result
        csum = csum ^ mask
        return hashlib.sha1(str(csum)).hexdigest()

    #Begins the TCP packet sending process
    def start_tcp(self):
        #Reads all of the file contents at once, then proceeds to send in chunks
        #Note: reading at once makes it easier to fix problem of reading the last packet
        #The problem is that the last packet may not be 465 bytes, and thus the read can cause extra issues
        #This can be fixed to read packet data as it is needed, but was not asked for.
        with open(self.file, 'rb') as file:
            read_data = file.read()
            file.close()
        i = 0
        data = []
        for i in range(0, len(read_data), 465):
            if read_data[i:] < 465:
                data.append(read_data[i:])
                break
            else:
                data.append(read_data[i:i+465])
        
        #Check for data size to be less than one packet, and sets the lastPacjet flag
        i = 0
        pno = 1
        timeOut = 4
        #Constantly parse data to be made into packets
        while 1:
            acknowledged = 0
            #Check if the packet being sent is the last one
            if len(data[i]) < 465:
                #Send the last packet
                while not acknowledged:
                    size = len(data[i])
                    if size < 100:
                        size = '{:0>3}'.format(size)
                    last_packet = '{:<465}'.format(data[i])
                    csum = self.checksum(last_packet)
                    self.send_packet(csum, '{:0>3}'.format(pno), size, 0, last_packet)

                    ready = select.select([self.sock], [], [], timeOut)
                    if ready[0]:
                        ack = self.sock.recv(512).decode()
                        print("Received ACK: '" + ack[0:5] + "'")
                        valid_ack = self.check_ack(ack)
                        if not valid_ack:
                            continue
                        if ack[4] == str(pno):
                            acknowledged = 1
                            pno += 1
                #Send the filename for the server to replicate the file. This means an extra packet is sent
                self.send_filename(pno, timeOut)
                print("Finished. Sent " + str(len(read_data)) + " bytes of data.")
                break
                
            #Find the checksum value
            csum = self.checksum(data[i])

            #Send the packet and increment values to the next
            self.send_packet(csum, '{:0>3}'.format(pno), 465, 0, data[i])
            while not acknowledged:
                ready = select.select([self.sock], [], [], timeOut)
                if ready[0]:
                    ack = self.sock.recv(512).decode()
                    print("Received ACK: '" + ack[0:5] + "'")
                    valid_ack = self.check_ack(ack)
                    if not valid_ack:
                        self.send_packet(csum, '{:0>3}'.format(pno), 465, 0, data[i])
                        continue
                    acknowledged = 1
                else:
                    self.send_packet(csum, '{:0>3}'.format(pno), 465, 0, data[i])
            #Check if the ACK matches the desired packet,if it does, begin sending the next one.
            if not ack[4] == str(pno):
                continue
            i = int(ack[4])
            pno += 1

    #Function to send the last packet (filename) to the server
    def send_filename(self, pno, timeOut):
        print("Sending filename")
        data = "{:<465}".format("filename:" + self.file)
        size = "{:>3}".format(9 + len(self.file))
        csum = self.checksum(data)
        acknowledged = 0
        self.send_packet(csum, '{:0>3}'.format(pno), size, 1, data)
        while not acknowledged:
            ready = select.select([self.sock], [], [], timeOut)
            if ready[0]:
                ack = self.sock.recv(512).decode()
                print("Received ACK: '" + ack[0:5] + "'")
                if not self.check_ack(ack):
                    continue
                if not ack[4] == str(pno):
                    continue
                acknowledged = 1
            else:
                self.send_packet(csum, '{:0>3}'.format(pno), size, 1, data)

    #Checks to see if the ACK is valid
    def check_ack(self, ack):
        if len(ack) < 5:
            print("ACK is mangled. Resending packet")
            return 0
        if not ack[0:3] == "ACK":
            print("ACK is mangled. Resending packet")
            return 0
        return 1
    
    #Function for sending the packet to the network
    def send_packet(self, csum, pno, size, isLast, data):
        print("Sending packet " + str(pno) + "...")
        send_string = csum + str(pno) + str(size) + data + str(isLast)
        self.sock.send(send_string)
        

class TCPServer:
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.setup_socket()
        client, addr = self.sock.accept()
        self.read_packets(client)
        
    #Connect to the network
    def setup_socket(self):
        self.sock = socket(AF_INET, SOCK_STREAM)
        self.sock.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
        self.sock.bind((self.host, self.port))
        self.sock.listen(5)
        print("Listening on host: " + str(self.host) + " with port: " + str(self.port))

    #Packet scheme:
    #Bytes 0-39: checksum (40 bytes)
    #Bytes 40-42: packet number (3 bytes)
    #Bytes 43-45: size of data (3 bytes)
    #Bytes 46-510: 465 bytes of data (padded with spaces for the last packet)
    #Byte 511: Last packet flag (1 byte)
    #total: 512 bytes
    #Bytes are all characters. Integer coded bytes were not used
    #As I was unsure if the network would handle objects or multiple writes.
    def read_packets(self, client):
        lastPacket = 0
        out_data = ""
        curr_packet = 1
        while not lastPacket:
            print("Waiting for packet")
            packet = client.recv(512)
            #Parse packet for readability
            csum = packet[0:40]
            pno = packet[40:43]
            data = packet[46:511]
            print("Received packet: " + pno)
            #check if the packet passes checksum
            valid = self.checksum(data, csum)
            if not valid:
                print("Checksum failed")
                self.request_same_packet(client, curr_packet - 1)
                continue

            size = int(packet[43:46])
            
            if packet[511] == '1':
                lastPacket = 1
                
            #Check if the packet is in order
            inOrder = int(pno) == curr_packet
            if not inOrder:
                print("Packet is out of order")
                self.request_same_packet(client, curr_packet - 1)
                continue
            
            
            #Check for last packet to parse out padding spaces
            if lastPacket:
                out_data += data[:int(size)].split("filename:")[0]
                filename = data[:int(size)].split("filename:")[1]
            else:
                out_data += data[:int(size)]
            self.send_ack(client, pno)
            curr_packet += 1

        self.write_to_file(out_data, filename)
        print("Received: " + str(len(out_data)) + " bytes of data")

    #Functions for sending ACK's. The second function 
    #is used to change logged output and provide readability
    def send_ack(self, client, pno):
        client.send('{:<512}'.format('ACK ' + str(int(pno))))

    def request_same_packet(self, client, pno):
        client.send('{:<512}'.format('ACK ' + str(int(pno))))

    #Checksum copied from the client code. Checks if hash output is equal
    #If it is not, returns false and will request the same packet.
    def checksum(self, data, check):
        csum = 0
        for i in range(0, len(data)-1, 2):
            word = (ord(data[i]) << 8) + (ord(data[i+1]))
            csum += word
        mask = (1 << csum.bit_length()) - 1
        csum = csum ^ mask
        out = hashlib.sha1(str(csum)).hexdigest()
        return out == check

    #Function to create and write the data to the file
    def write_to_file(self, data, filename):
        if not os.path.exists("output"):
            os.makedirs("output")
        with open("output\\" + filename, 'w') as file:
            file.write(data)
            file.close()
            
            
if __name__ == '__main__':
    if len(sys.argv) == 4:
        TCPClient(sys.argv[1], int(sys.argv[2]), sys.argv[3])
    else:
        TCPServer(sys.argv[1], int(sys.argv[2]))
            
    
