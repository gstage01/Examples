#include "blather.h"
#include <stdlib.h>
#include <stdio.h>
#include <signal.h>
#include <sys/stat.h>


server_t *serverp;

void handle_signals(int sig_num) {
	server_shutdown(serverp);
}

int main(int argc, char* argv[]) {
	signal(SIGTSTP, handle_signals);
	signal(SIGINT, handle_signals);
	signal(SIGTERM, handle_signals);
	server_t server;
	serverp = &server;
	server_start(&server, argv[1], DEFAULT_PERMS);
	int i;
	while (1) {
		server_check_sources(&server);
		if (server_join_ready(&server)) {
			server_handle_join(&server);
		}
		for (i=0; i<server.n_clients; i++) {
			if (server_client_ready(&server, i)) {
				server_handle_client(&server, i); 
			}
		}
	}
	return 0;
}
