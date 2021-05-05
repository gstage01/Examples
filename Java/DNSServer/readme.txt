Compilation: 
javac DNSServer.javac
javac DNSClient.javac

On separate command windows:
java DNSServer
java DNSClient

Compilation was done through javac, coding was done in IntelliJ. A problem I ran into is using .isReachable() in the InetAddress API. Some servers would be unreachable, or have overly long latency times (over 2 seconds)


main() runs the server, by connecting to the port specified (5001 in this case), creates a socket and opens the socket to listen. It then starts a new thread to run the query functions.
IPselection (I was not sure if the comments meant to call this DNSselection) simply checks for multiple IPs for the same domain, then it pings them to get latency, storing and returning the best result.

On run, the order of operations is as follows:

Initialize streams to read input from the client and write output
Read the input from client
Check input against a regular expression for invalid domain formats. Prints an output message to the client in the case of bad domains
Create the cache, if needed, then check its contents against the input from the client. If any matches, save the domain and IP, multiple if found, and format it to select IP from the list
If no match is found, the program checks the IP by using InetAddress to get the host address, and outputs an error message to the client if the host is not found.
The message is then formatted from the domain given, if no errors are present, and sent back to the client. 
All sockets and input/output streams are then closed. Not closing them will cause problems with multithreading, as a new thread is created after a query is resolved. This can cause multiple streams to stay open for no reason and/or interfere with other threads.

It is important to check the cache before checking the query, as there is no need to contact a server if the IP is already known. 
A server can use 2 sockets to connect to clients and to connect to servers. Doing it with InetAddress in Java takes away the need for a second socket, but leads to slower performance than coding a request yourself.
Multiple threads are used to be able to handle multiple requests without blocking. Other methods of handling multiple clients will cause blocking of other clients or interference. 