#include "blather.h"
#include <stdlib.h>
#include <stdio.h>
#include <signal.h>
#include <sys/stat.h>


client_t *server_get_client(server_t *server, int idx) {
	return &server->client[idx];
}

void server_start(server_t *server, char *server_name, int perms) {
	//Initialize server fields

	printf("server_start()\n");		
	char filename[MAXPATH];
	server->join_ready = 0;
	sprintf(filename, "%s.fifo", server_name);
	remove(filename);
	strcpy(server->server_name, filename);
	mkfifo(filename, DEFAULT_PERMS);
	server->join_fd = open(filename, O_RDWR);
	server->n_clients = 0;
	printf("server_start(): end\n");
}

void server_shutdown(server_t *server) {

	//Create shutdown message to send to clients
	mesg_t message;
	message.kind = 40;
	strncpy(message.name, "", MAXNAME);
	strncpy(message.body, "", MAXLINE);

	//Send message to clients and close the server/unlink fifo
	server_broadcast(server, &message);
	close(server->join_fd);
	unlink(server->server_name);
	exit(0);
}

int server_add_client(server_t *server, join_t *join) {
	if (server->n_clients == MAXCLIENTS) {
		return -1;
	}
	printf("server_add_client(): %s %s %s\n", join->name, join->to_client_fname, join->to_server_fname);
	
	//Initialize client to add to server
	client_t new_client = {.data_ready = 0};
	strncpy(new_client.name, join->name, MAXNAME);
	strncpy(new_client.to_client_fname, join->to_client_fname, MAXPATH);
	strncpy(new_client.to_server_fname, join->to_server_fname, MAXPATH);
	new_client.to_client_fd = open(new_client.to_client_fname, O_RDWR);
	new_client.to_server_fd = open(new_client.to_server_fname, O_RDWR);

	//Add client, create join message and broadcast
	server->client[server->n_clients] = new_client;
	server->n_clients++;
	mesg_t message;
	message.kind = 20;
	strncpy(message.name, join->name, MAXNAME);
	strncpy(message.body, "", MAXLINE);
	server_broadcast(server, &message);
	return 0;
	
}

int server_remove_client(server_t *server, int idx) {
	//Locate client, close client and server fifos. Remove and unlink fifos.
	client_t *client = server_get_client(server, idx);
	close(client->to_client_fd);
	close(client->to_server_fd);
	remove(client->to_client_fname);
	remove(client->to_server_fname);
	unlink(client->to_client_fname);
	unlink(client->to_server_fname);
	
	//Shift clients and decrement count
	int i;
	for (i=idx+1; i<MAXCLIENTS; i++) {
		server->client[i-1] = server->client[i]; 
	}
	server->n_clients--;
	return 0;
}

int server_broadcast(server_t *server, mesg_t *mesg) {
	printf("server_broadcast(): %d from %s - %s\n", mesg->kind, mesg->name, mesg->body);
	int i;
	
	//Write message to each client fifo
	for (i=0; i<server->n_clients; i++) {
		write(server->client[i].to_client_fd, mesg, sizeof(mesg_t));
	}
	return 0;
}

void server_check_sources(server_t *server) {

	//Initialize fd_set for select()
	fd_set set;
	int maxfd;
	maxfd = server->join_fd;
	FD_ZERO(&set);
	FD_SET(server->join_fd, &set);		//Adds the server's join fifo to the set
	int i;

	//Add client to server fifos to the set, then wait for one to have input
	for (i=0; i<server->n_clients; i++) {
		FD_SET(server->client[i].to_server_fd, &set);
		printf("%d", server->client[i].to_server_fd);
		if (maxfd < server->client[i].to_server_fd) {
			maxfd = server->client[i].to_server_fd;
		}
	}
	select(maxfd+1, &set, NULL, NULL, NULL);
	
	if (FD_ISSET(server->join_fd, &set)) {
		server->join_ready = 1;
	}
	for (i=0; i<server->n_clients; i++) {
		if (FD_ISSET(server->client[i].to_server_fd, &set)) {
			server->client[i].data_ready = 1;
		}
	}
}

int server_client_ready(server_t *server, int idx) {
	return server->client[idx].data_ready;
}

int server_join_ready(server_t *server) {
	return server->join_ready;
}

int server_handle_client(server_t *server, int idx) {
	mesg_t message;

	//Check if there is no info to read
	if (read(server->client[idx].to_server_fd, &message, sizeof(mesg_t)) == 0) {
		return -1;
	}
	printf("server: mesg received from client %d %s : %s\n", idx, message.name, message.body);
	server->client[idx].data_ready = 0;
	server_broadcast(server, &message);
	if (message.kind == 30) {
		server_remove_client(server, idx);
	}
	return 0;
}

int server_handle_join(server_t *server) {
	printf("server_handle_join()\n");
	join_t join;
	if (read(server->join_fd, &join, sizeof(join_t)) == 0) {
		return -1;
	}
	server->join_ready = 0;
	server_add_client(server, &join);
	return 0;
	
}

