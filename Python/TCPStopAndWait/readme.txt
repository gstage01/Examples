The file program.py is the file written by me. It implements the server and client of the TCP Stop and Wait protocol. 
The file networkLayer.py was given to us for testing networks with packet loss and packet destruction.
To run:
Use 3 command shells. First shell (Network Layer): python3 networkLayer.py portNumber delay maxPackets probDrop probDestroy
Second (TCP Server): python2 program.py hostname portNumber
Third (TCP Client): python2 program.py hostname portNumber transferFile