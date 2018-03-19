#include <stdio.h>
#include "commando.h"

int main(int argc, char *argv[]) {
	char *in = malloc(MAX_LINE);			//allocate targets for parse_into_tokens()
	char **tokens = malloc(ARG_MAX * sizeof(char *));
	int echo = 0;
	int *ntok = malloc(sizeof(int));
	int i = 2;
	while (i < argc) {	//Allocate the tokens array of pointers
		tokens[i] = malloc(ARG_MAX);
		i++;
	}
	cmdctl_t *ctl = malloc(sizeof(cmdctl_t)); 	//Allocate the ctl for the program
	ctl->size = 0;
	if (argc > 1) {		//if there is more than one argument, check for the --echo variable
		if (getenv("COMMANDO_ECHO") || !strcmp(argv[1], "--echo")) {
			echo = 1;
		}
	}
	while (1) {
		printf("@> ");
		if (fgets(in, BUFSIZE, stdin) == NULL) {	//Check for end of input, freeing memory if it is found
			printf("\nEnd of input");
			cmdctl_freeall(ctl);
			free(ctl);
			int c = 0;
			while (c<argc) {
				free(tokens[c]);
				c++;
			}
			free(tokens);
			free(ntok);
			exit(0);
		}
		if (echo) { 		//Check for echo
			printf("%s", in);
		}
		
		parse_into_tokens(in, tokens, ntok);		//Parses input into tokens
		if (*ntok==0) {
			cmdctl_update_state(ctl, NOBLOCK);		//If there is no input, then update and continue
			continue;
		}
		if (!strcmp(in, "help")) {	//String check for "help", if it is found, print the help message
			printf("COMMANDO COMMANDS\nhelp               : show this message\nexit               : exit the program\nlist               : list all jobs that have been started giving information on each\npause nanos secs   : pause for the given number of nanseconds and seconds\noutput-for int     : print the output for given job number\noutput-all         : print output for all jobs\nwait-for int       : wait until the given job number finishes\nwait-all           : wait for all jobs to finish\ncommand arg1 ...   : non-built-in is run as a job\n");
			cmdctl_update_state(ctl, NOBLOCK);
			continue;
		} else if (!strcmp(in, "exit")) {
			cmdctl_freeall(ctl);		//Exit command. Frees the ctl
			free(ctl);
			int b = 0;
			while (b<argc) {
				free(tokens[b]);
				b++;
			}
			free(tokens);
			free(ntok);
			exit(0);
		} else if (!strcmp(in, "list")) {	//List command prints the ctl and command outputs
			cmdctl_print(ctl);
			cmdctl_update_state(ctl, NOBLOCK);
			continue;
		} else if (!strcmp(tokens[0], "pause")) {	//Pause uses the pause_for function in util.c to stall the program
			pause_for(atol(tokens[1]), atoi(tokens[2]));
			cmdctl_update_state(ctl, NOBLOCK);
			continue;	
		} else if (!strcmp(tokens[0], "wait-for")) {	//wait-for waits for a given command to finish
			int status = 0;
			waitpid(ctl->cmd[atoi(tokens[1])]->pid, &status, NOBLOCK);
			cmdctl_update_state(ctl, NOBLOCK);
			continue; 
		} else if (!strcmp(tokens[0], "wait-all")) {	//wait-all does the same as wait-for, but for all commands
			int k = 0;
			while (k< ctl->size-1) {
				int stat = 0;
				if (!(ctl->cmd[k]->finished)) {
					waitpid(ctl->cmd[k]->pid, &stat, DOBLOCK);
					cmdctl_update_state(ctl, NOBLOCK);	
				} 
				k++;
			}
			continue;
		} else if (!strcmp(tokens[0], "output-for")) {	//prints output for a command onto the screen
			cmd_print_output(ctl->cmd[atoi(tokens[1])]);
			cmdctl_update_state(ctl, NOBLOCK);
			
		} else if (!strcmp(in, "list")) {	//List prints all of the commands in the ctl
			cmdctl_print(ctl);
			cmdctl_update_state(ctl, NOBLOCK);
		} else if (!strcmp(in, "output-all")) {		//output-all prints all output onto the screen
			int j = 0;
			while (j < ctl->size) {
				cmd_print_output(ctl->cmd[j]);
				j++;
			}
			cmdctl_update_state(ctl, NOBLOCK);
		} else	{		//else create a new command from the tokens array, start the command, then add it to the ctl
			cmd_t *cmd = cmd_new(tokens);
			cmdctl_update_state(ctl, NOBLOCK);
			cmd_start(cmd);
			cmdctl_add(ctl, cmd);
			
		}
		
	}
}
