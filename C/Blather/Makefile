CFLAGS = -Wall -g
CC     = gcc $(CFLAGS)

all : bl-server bl-client

bl-client : bl-client.o util.o simpio.o
	$(CC) -o $@ $^ -lpthread

bl-server : bl-server.o blather.h simpio.o util.o
	$(CC) -o bl-server server.o bl-server.o blather.h simpio.o util.o -lpthread
	
server.o : server.c blather.h
	$(CC) -c server.c
	
bl-server.o : bl-server.c blather.h server.o
	$(CC) -c bl-server.c
	
bl-client.o : bl-client.c blather.h
	$(CC) -c bl-client.c
	
simpio.o : simpio.c
	$(CC) -c simpio.c

util.o : util.c
	$(CC) -c util.c
	
clean : 
	rm -f *.o
	
make shell-tests : shell_tests.sh shell_tests_data.sh clean-tests
	chmod u+rx shell_tests.sh shell_tests_data.sh normalize.awk filter-semopen-bug.awk
	./shell_tests.sh

clean-tests :
	rm -f test-*.{log,out,expect,diff,valgrindout}  
