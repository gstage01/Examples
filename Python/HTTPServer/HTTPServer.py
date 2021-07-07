<<<<<<< HEAD
from socket import *
from thread import *
import sys
import signal
import urllib2
import os
import datetime

#Implements a simple HTTP Server to handle GET, PUT, POST, Delete, and OPTIONS requests

CRLF = "\r\n"
OK = 'HTTP/1.1 200 OK \r\n'
NOT_FOUND = 'HTTP/1.1 404 NOT FOUND\r\n'
FORBIDDEN = 'HTTP/1.1 403 FORBIDDEN{}Connection: close{}'.format(CRLF, CRLF)
METHOD_NOT_ALLOWED = 'HTTP/1.1 405  METHOD NOT ALLOWED{}Allow: GET, HEAD{}Connection: close{}'.format(CRLF,CRLF,CRLF)
MOVED_PERMANENTLY = 'HTTP/1.1 301 MOVED PERMANENTLY' + CRLF +  'Location:  https://www.cs.umn.edu/' + CRLF + 'Connection: close' + CRLF + CRLF


class HTTPSServer:
    def __init__(self, host, port):
        self.host=host
        self.port=port
        self.setup_socket()
        self.accept()
        self.sock.shutdown()
        self.sock.close()
        
      
    def setup_socket(self):
        print("Listening on port: " + str(port))
        self.sock = socket(AF_INET, SOCK_STREAM)
        self.sock.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
        self.sock.bind((self.host, int(port)))
        self.sock.listen(128)
        
    def take_request(self, client_sock, addr):
        request = client_sock.recv(2048).split("\n")
        print("Received request: '" + request[0] + "' from ('" + addr[0] + "', " + str(addr[1]) + ")")
        spl_req = request[0].split(" ")
        if  spl_req[0] == "GET":
            if "csumn" in spl_req[1]:
                self.redirect(client_sock)
            elif os.path.isfile(spl_req[1].strip("/")):
                print("Sent: 200 OK to (" + addr[0] + str(addr[1]) + ")")
                client_sock.send(OK)
                client_sock.send("Content-Type: text/html" + CRLF)
                client_sock.send(CRLF)
                self.send_body(client_sock, spl_req[1].strip("/"))
                client_sock.close()
            else:
                req = urllib2.Request(spl_req[1].strip("/"))
                try:
                    urllib2.urlopen(req)
                except (urllib2.URLError, ValueError) as e:
                    print(e)
                    if ("getaddrinfo failed" in str(e)) or ("unknown url" in str(e)):
                        print("Sent: 404 NOT FOUND to (" + addr[0] + str(addr[1]) + ")")
                        client_sock.send(NOT_FOUND)
                        client_sock.send("Content-Type: text/html" + CRLF + CRLF)
                        self.send_body(client_sock, "404.html")
                    elif "Forbidden" in str(e):
                        print("Sent: 403 Forbidden to (" + addr[0] + str(addr[1]) + ")")
                        client_sock.send(FORBIDDEN)
                        client_sock.send("Content-Type: text/html" + CRLF + CRLF)
                        self.send_body(client_sock, "403.html")

        elif spl_req[0] == "POST":
            print("Sent: 200 OK to (" + addr[0] + str(addr[1]) + ")")
            client_sock.send(OK)
            client_sock.send("Content-Type: text/html" + CRLF + CRLF)
            form_vals = request[len(request)-1].split("&")
            for i in range(0, len(form_vals)):
                form_vals[i] = form_vals[i].split("=")[1]
            client_sock.send(self.build_post(form_vals))

        elif spl_req[0] == "PUT":
            this_req = spl_req[1].strip("/")
            if os.path.isfile(this_req):
                print("Sent: 200 OK to (" + addr[0] + str(addr[1]) + ")")
                status=OK
            else:
                print("Sent: 201 Created to (" + addr[0] + str(addr[1]) + ")")
                status="HTTP/1.1 201 Created\r\nContent-Location: " + spl_req[1] + CRLF
            with open(this_req, 'w+') as file:
                file.write(request[len(request)-1])
                file.close()
            client_sock.send(status)
            client_sock.send("Content-Location: /" + this_req + CRLF + CRLF)

        elif spl_req[0] == "OPTIONS":
            this_req = spl_req[1]
            print("Sent: 200 OK to (" + addr[0] + str(addr[1]) + ")")
            if this_req == "/calendar.html":
                client_sock.send(OK)
                client_sock.send("Allow: OPTIONS, GET, HEAD" + CRLF)
                client_sock.send("Cache-Control: max-age=604800" + CRLF)
                client_sock.send("Date: " + str(datetime.datetime.now()) + CRLF)
                client_sock.send("Content-Length: 0" + CRLF + CRLF)
            elif this_req == "/form.html":
                client_sock.send(OK)
                client_sock.send("Allow: OPTIONS, GET, HEAD, POST" + CRLF)
                client_sock.send("Cache-Control: max-age=604800" + CRLF)
                client_sock.send("Date: " + str(datetime.datetime.now()) + CRLF)
                client_sock.send("Content-Length: 0" + CRLF + CRLF)
            elif this_req == "/":
                client_sock.send(OK)
                client_sock.send("Allow: OPTIONS, GET, HEAD, POST, DELETE" + CRLF)
                client_sock.send("Cache-Control: max-age=604800" + CRLF)
                client_sock.send("Date: " + str(datetime.datetime.now()) + CRLF)
                client_sock.send("Content-Length: 0" + CRLF + CRLF)
                
        elif spl_req[0] == "DELETE":
            this_file = spl_req[1].strip("/")
            if os.path.isfile(this_file):
                if os.access(this_file, os.W_OK) and os.access(this_file, os.R_OK):
                    os.remove(this_file)
                    print("Sent: 200 OK to (" + addr[0] + str(addr[1]) + ")")
                    client_sock.send(OK)
                    client_sock.send("Date: " + str(datetime.datetime.now()) + CRLF + CRLF)
                else:
                    print("Sent: 403 Forbidden to (" + addr[0] + str(addr[1]) + ")")
                    client_sock.send(FORBIDDEN)
            else:
                print("Sent: 200 OK to (" + addr[0] + str(addr[1]) + ")")
                client_sock.send(OK)
                client_sock.send("Date: " + str(datetime.datetime.now()) + CRLF + CRLF)
        else:
            client_sock.send(METHOD_NOT_ALLOWED)
                        
    def build_post(self, vals):
        time1 = vals[3]
        time2 = vals[4]
        
        time1_str = self.parse_time(time1)
        time2_str = self.parse_time(time2)
        return "<!DOCTYPE html><html><head><meta charset='utf-8'><title></title></head><body><p>Following Form Data Submitted Successfully<br />Place Name: {0}<br />Address Line 1: {1}<br />Address Line 2: {2}<br />Open Time: {3}<br />Close Time: {4}<br />Additional Info: {5}<br />URL: {6}<br /></p></body></html>".format(vals[0], vals[1], vals[2], time1_str, time2_str, vals[5], vals[6])

    def parse_time(self, time):
        hours = int(time[0] + time[1])
        
        if hours - 12 >= 0:
            time_str = str(hours-12) + ":" + time[5] + time[6]
            time_str += " PM"
        else:
            time_str = str(hours) + ":" + time[5] + time[6]
            time_str += " AM"
        return time_str
    
    def send_body(self, client_sock, resource):
        with open(resource, 'r') as file:
            client_sock.send(file.read())
            file.close()
        
    def redirect(self, client_sock):
        print("Sent: 301 MOVED PERMANENTLY")
        client_sock.send(MOVED_PERMANENTLY)
        client_sock.send("Refresh: 0; url=https://www.cs.umn.edu/" + CRLF)
        client_sock.send("Content-Type: text/html")
        
    def accept(self):
        while True:
            client_sock, addr = self.sock.accept()
            print("Found client at addr: %s" % str(addr))
            start_new_thread(self.take_request, (client_sock, addr))


if __name__ == '__main__':
    user_in = raw_input("Enter host or press enter for localhost: ")
    if len(sys.argv) > 1:
        port = sys.argv[1]
    else:
        port = 9001
    if user_in == "":
        HTTPSServer('localhost', port)
    else:
        HTTPServer(user_in, port)
=======
from socket import *
from thread import *
import sys
import signal
import urllib2
import os
import datetime

#Implements a simple HTTP Server to handle GET, PUT, POST, Delete, and OPTIONS requests

CRLF = "\r\n"
OK = 'HTTP/1.1 200 OK \r\n'
NOT_FOUND = 'HTTP/1.1 404 NOT FOUND\r\n'
FORBIDDEN = 'HTTP/1.1 403 FORBIDDEN{}Connection: close{}'.format(CRLF, CRLF)
METHOD_NOT_ALLOWED = 'HTTP/1.1 405  METHOD NOT ALLOWED{}Allow: GET, HEAD{}Connection: close{}'.format(CRLF,CRLF,CRLF)
MOVED_PERMANENTLY = 'HTTP/1.1 301 MOVED PERMANENTLY' + CRLF +  'Location:  https://www.cs.umn.edu/' + CRLF + 'Connection: close' + CRLF + CRLF


class HTTPSServer:
    def __init__(self, host, port):
        self.host=host
        self.port=port
        self.setup_socket()
        self.accept()
        self.sock.shutdown()
        self.sock.close()
        
      
    def setup_socket(self):
        print("Listening on port: " + str(port))
        self.sock = socket(AF_INET, SOCK_STREAM)
        self.sock.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
        self.sock.bind((self.host, int(port)))
        self.sock.listen(128)
        
    def take_request(self, client_sock, addr):
        request = client_sock.recv(2048).split("\n")
        print("Received request: '" + request[0] + "' from ('" + addr[0] + "', " + str(addr[1]) + ")")
        spl_req = request[0].split(" ")
        if  spl_req[0] == "GET":
            if "csumn" in spl_req[1]:
                self.redirect(client_sock)
            elif os.path.isfile(spl_req[1].strip("/")):
                print("Sent: 200 OK to (" + addr[0] + str(addr[1]) + ")")
                client_sock.send(OK)
                client_sock.send("Content-Type: text/html" + CRLF)
                client_sock.send(CRLF)
                self.send_body(client_sock, spl_req[1].strip("/"))
                client_sock.close()
            else:
                req = urllib2.Request(spl_req[1].strip("/"))
                try:
                    urllib2.urlopen(req)
                except (urllib2.URLError, ValueError) as e:
                    print(e)
                    if ("getaddrinfo failed" in str(e)) or ("unknown url" in str(e)):
                        print("Sent: 404 NOT FOUND to (" + addr[0] + str(addr[1]) + ")")
                        client_sock.send(NOT_FOUND)
                        client_sock.send("Content-Type: text/html" + CRLF + CRLF)
                        self.send_body(client_sock, "404.html")
                    elif "Forbidden" in str(e):
                        print("Sent: 403 Forbidden to (" + addr[0] + str(addr[1]) + ")")
                        client_sock.send(FORBIDDEN)
                        client_sock.send("Content-Type: text/html" + CRLF + CRLF)
                        self.send_body(client_sock, "403.html")

        elif spl_req[0] == "POST":
            print("Sent: 200 OK to (" + addr[0] + str(addr[1]) + ")")
            client_sock.send(OK)
            client_sock.send("Content-Type: text/html" + CRLF + CRLF)
            form_vals = request[len(request)-1].split("&")
            for i in range(0, len(form_vals)):
                form_vals[i] = form_vals[i].split("=")[1]
            client_sock.send(self.build_post(form_vals))

        elif spl_req[0] == "PUT":
            this_req = spl_req[1].strip("/")
            if os.path.isfile(this_req):
                print("Sent: 200 OK to (" + addr[0] + str(addr[1]) + ")")
                status=OK
            else:
                print("Sent: 201 Created to (" + addr[0] + str(addr[1]) + ")")
                status="HTTP/1.1 201 Created\r\nContent-Location: " + spl_req[1] + CRLF
            with open(this_req, 'w+') as file:
                file.write(request[len(request)-1])
                file.close()
            client_sock.send(status)
            client_sock.send("Content-Location: /" + this_req + CRLF + CRLF)

        elif spl_req[0] == "OPTIONS":
            this_req = spl_req[1]
            print("Sent: 200 OK to (" + addr[0] + str(addr[1]) + ")")
            if this_req == "/calendar.html":
                client_sock.send(OK)
                client_sock.send("Allow: OPTIONS, GET, HEAD" + CRLF)
                client_sock.send("Cache-Control: max-age=604800" + CRLF)
                client_sock.send("Date: " + str(datetime.datetime.now()) + CRLF)
                client_sock.send("Content-Length: 0" + CRLF + CRLF)
            elif this_req == "/form.html":
                client_sock.send(OK)
                client_sock.send("Allow: OPTIONS, GET, HEAD, POST" + CRLF)
                client_sock.send("Cache-Control: max-age=604800" + CRLF)
                client_sock.send("Date: " + str(datetime.datetime.now()) + CRLF)
                client_sock.send("Content-Length: 0" + CRLF + CRLF)
            elif this_req == "/":
                client_sock.send(OK)
                client_sock.send("Allow: OPTIONS, GET, HEAD, POST, DELETE" + CRLF)
                client_sock.send("Cache-Control: max-age=604800" + CRLF)
                client_sock.send("Date: " + str(datetime.datetime.now()) + CRLF)
                client_sock.send("Content-Length: 0" + CRLF + CRLF)
                
        elif spl_req[0] == "DELETE":
            this_file = spl_req[1].strip("/")
            if os.path.isfile(this_file):
                if os.access(this_file, os.W_OK) and os.access(this_file, os.R_OK):
                    os.remove(this_file)
                    print("Sent: 200 OK to (" + addr[0] + str(addr[1]) + ")")
                    client_sock.send(OK)
                    client_sock.send("Date: " + str(datetime.datetime.now()) + CRLF + CRLF)
                else:
                    print("Sent: 403 Forbidden to (" + addr[0] + str(addr[1]) + ")")
                    client_sock.send(FORBIDDEN)
            else:
                print("Sent: 200 OK to (" + addr[0] + str(addr[1]) + ")")
                client_sock.send(OK)
                client_sock.send("Date: " + str(datetime.datetime.now()) + CRLF + CRLF)
        else:
            client_sock.send(METHOD_NOT_ALLOWED)
                        
    def build_post(self, vals):
        time1 = vals[3]
        time2 = vals[4]
        
        time1_str = self.parse_time(time1)
        time2_str = self.parse_time(time2)
        return "<!DOCTYPE html><html><head><meta charset='utf-8'><title></title></head><body><p>Following Form Data Submitted Successfully<br />Place Name: {0}<br />Address Line 1: {1}<br />Address Line 2: {2}<br />Open Time: {3}<br />Close Time: {4}<br />Additional Info: {5}<br />URL: {6}<br /></p></body></html>".format(vals[0], vals[1], vals[2], time1_str, time2_str, vals[5], vals[6])

    def parse_time(self, time):
        hours = int(time[0] + time[1])
        
        if hours - 12 >= 0:
            time_str = str(hours-12) + ":" + time[5] + time[6]
            time_str += " PM"
        else:
            time_str = str(hours) + ":" + time[5] + time[6]
            time_str += " AM"
        return time_str
    
    def send_body(self, client_sock, resource):
        with open(resource, 'r') as file:
            client_sock.send(file.read())
            file.close()
        
    def redirect(self, client_sock):
        print("Sent: 301 MOVED PERMANENTLY")
        client_sock.send(MOVED_PERMANENTLY)
        client_sock.send("Refresh: 0; url=https://www.cs.umn.edu/" + CRLF)
        client_sock.send("Content-Type: text/html")
        
    def accept(self):
        while True:
            client_sock, addr = self.sock.accept()
            print("Found client at addr: %s" % str(addr))
            start_new_thread(self.take_request, (client_sock, addr))


if __name__ == '__main__':
    user_in = raw_input("Enter host or press enter for localhost: ")
    if len(sys.argv) > 1:
        port = sys.argv[1]
    else:
        port = 9001
    if user_in == "":
        HTTPSServer('localhost', port)
    else:
        HTTPServer(user_in, port)
>>>>>>> fac084134fc8c24124d12babe72cf259ce33f12b
