#include "blather.h"
#include <stdlib.h>
#include <stdio.h>
#include <signal.h>
#include <sys/stat.h>
#include <pthread.h>

simpio_t a_simpio;
simpio_t *simpio = &a_simpio;
client_t a_client;
client_t *client = &a_client;
server_t a_server;
server_t *server = &a_server;

pthread_t server_thread;
pthread_t client_thread;

void handle_message(mesg_t *message) {
	

	int kind = message->kind;
	if (kind == 10) {
		iprintf(simpio, "[%s] : %s\n", message->name, message->body); 
	} else if (kind == 20) {
		iprintf(simpio, "-- %s JOINED --\n", message->name);
	} else if (kind == 30) {
		iprintf(simpio, "-- %s DEPARTED --\n", message->name);
	} else {
		iprintf(simpio, "!!! server is shutting down !!!\n");
			pthread_cancel(client_thread);
			pthread_cancel(server_thread);
			return;
	}

	
}

void *monitor_server(void *arg) {
	mesg_t message;
	fd_set set;
	FD_SET(client->to_client_fd, &set);
	int maxfd = client->to_client_fd;
	while (1) {
		select(maxfd+1, &set, NULL, NULL, NULL);
		read(client->to_client_fd, &message, sizeof(mesg_t));
		handle_message(&message);
	}
	pthread_cancel(client_thread);
	return NULL;
}

void *monitor_client(void *arg) {
	//printf("Monitor\n");
	mesg_t mesg;
	while (!simpio->end_of_input) {
	simpio_reset(simpio);
	iprintf(simpio, "");
		while (!simpio->line_ready && !simpio->end_of_input) {
			simpio_get_char(simpio);
		}
		mesg.kind = 10;
		strncpy(mesg.name, client->name, MAXNAME);
		strncpy(mesg.body, simpio->buf, MAXLINE);
		write(client->to_server_fd, &mesg, sizeof(mesg_t));
	}
	pthread_cancel(server_thread);
	return NULL;
}

void handle_signals(int sig_num) {
	mesg_t message;
	strncpy(message.name, client->name, MAXNAME);
	message.kind = 30;
	pthread_cancel(server_thread);
	pthread_cancel(client_thread);
	write(client->to_server_fd, &message, sizeof(mesg_t));
	simpio_reset_terminal_mode();
	
}

int main(int argc, char *argv[]) {
	
	signal(SIGTSTP, handle_signals);
	signal(SIGINT, handle_signals);
	signal(SIGTERM, handle_signals);
  	
  	//Initialize server
	snprintf(server->server_name, MAXPATH, "%s.fifo", argv[1]);
	
	//Initialize client
	char name[MAXNAME];
	strncpy(name, argv[2], MAXNAME);
	
	char to_client_fname[MAXPATH];
	char to_server_fname[MAXPATH];
	snprintf(to_client_fname, MAXPATH, "%d.client.fifo", getpid());
	snprintf(to_server_fname, MAXPATH, "%d.server.fifo", getpid());
	mkfifo(to_client_fname, DEFAULT_PERMS);
	mkfifo(to_server_fname, DEFAULT_PERMS);
	client->to_client_fd = open(to_client_fname, O_RDWR);
	client->to_server_fd = open(to_server_fname, O_RDWR);
	
	strncpy(client->name,name, MAXNAME);
	strncpy(client->to_client_fname, to_client_fname, MAXPATH);
	strncpy(client->to_server_fname, to_server_fname, MAXPATH);
	
	
	//Initialize simpio
	char prompt[MAXNAME+2];
	sprintf(prompt, "%s>>", name);
	simpio_set_prompt(simpio, prompt);        
  	simpio_reset(simpio);                      
  	simpio_noncanonical_terminal_mode(); 
  	
  	
	//Create join request
	join_t join_request = {};
	//memset(&join_request," ",sizeof(join_t));
	char f1[MAXPATH];
	char f2[MAXPATH];
	sprintf(f1, "%d.client.fifo", getpid());
	sprintf(f2, "%d.server.fifo", getpid());

	strncpy(join_request.name, name, MAXNAME);
	strncpy(join_request.to_client_fname, f1, MAXPATH);
	strncpy(join_request.to_server_fname, f2, MAXPATH);

	
	int server_fd = open(server->server_name, O_RDWR);
	write(server_fd, &join_request, sizeof(join_t));
	
	
	//Thread for user input
	
	pthread_create(&client_thread, NULL, monitor_client, NULL);
	pthread_create(&server_thread, NULL, monitor_server, NULL);
	
	pthread_join(client_thread, NULL);
	pthread_join(server_thread, NULL);
	
	
	simpio_reset_terminal_mode();
	return 1;
		
}
