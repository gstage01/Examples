#include "commando.h"

//Function to read all characters from a file and store it in a buffer, setting the bytes read
char *read_all(int fd, int *nread) {
	char *buf = malloc(20*BUFSIZE);
	int n = read(fd, buf, 20*BUFSIZE);
	*nread = n;
	return buf;
}

//Initialization function for a new command
cmd_t *cmd_new(char *argv[]) {
	cmd_t *com = malloc(sizeof(cmd_t)); //Allocate space for the command 
	int i = 0;
	while (argv[i] != NULL) { 		//Allocate and copy the argv elements to the commands argv argument
		com->argv[i] = malloc(ARG_MAX);
		strcpy(com->argv[i], argv[i]);
		i++;	
	}
	com->argv[i] = NULL;
	strcpy(com->name, argv[0]); 	//Copy the name of the command
	com->finished = 0;	
	com->pid = -1;
	com->output_size = -1;
	snprintf(com->str_status, STATUS_LEN, "INIT");
	com->status = -1;
	return com;
}

//Frees the memory allocated by a given command.
void cmd_free(cmd_t *cmd) {
	int i=0;
	while (cmd->argv[i] != NULL) {
		free(cmd->argv[i]);
		i++;
	}
	if (cmd->output != NULL) {
		free(cmd->output);	
	}
	free(cmd);
}

//Creates a pipe for the command, fork() a child, and then has the child run the given command
void cmd_start(cmd_t *cmd) {
	
	pipe(cmd->out_pipe);
	snprintf(cmd->str_status, STATUS_LEN+1, "RUN");
	pid_t child = fork();
	if (child == 0) {
		dup2(cmd->out_pipe[PWRITE], 1);		//Switch output to the out_pipe write side
		close(cmd->out_pipe[PREAD]);		//Closes the read end
		execvp(cmd->name, cmd->argv);		//Execute the command and exit
		exit(0);
	} else {
		cmd->pid = child;			//pid is saved
		close(cmd->out_pipe[PWRITE]);		//Parent closes write end
	}
}

//Updates status information for the command and checks for it to be finished
void cmd_update_state(cmd_t *cmd, int block) {
	if (cmd->finished == 1) {
		return;
	}
	int status = 0;
	
	if (WIFEXITED(status) != 0) { 		//If the command is finished
		cmd->finished = 1;
		cmd->status = WEXITSTATUS(status);	//Set the exit status	
		char *buf = malloc(12); 		//Buffer for the running status
		sprintf(buf, "EXIT(%d)", status);
		strcpy(cmd->str_status, buf);
		printf("@!!! %s[#%d]: EXIT(%d)\n", cmd->name, cmd->pid, cmd->status);
		cmd_fetch_output(cmd);		//Command is finished, so grab the output of the command
		free(buf);
	}
}

//Stores the command output into cmd->output, taking it from the read end of the pipe.
void cmd_fetch_output(cmd_t *cmd) {
	
	if (!cmd->finished) {
		printf("\n%s[#%d] not finished yet\n", cmd->name, cmd->pid);
	} else {
		close(cmd->out_pipe[PWRITE]);
		char *buff;
		int *n = malloc(sizeof(int));
		buff = read_all(cmd->out_pipe[PREAD], n); 	//Uses read_all to read the bytes from the pipe
		cmd->output_size = *n;			
		cmd->output = malloc(*n+1);
		buff[*n] = "\0";
		strncpy(cmd->output, buff, *n+1);		//Copies the contents of buff to the command output field
		free(n);
		free(buff);
	}
}
//
//Takes the output of a command and prints it onto the screen
void cmd_print_output(cmd_t *cmd) {
	if ((char *)cmd->output == NULL) {	//If there is no output
		printf("\n%s[#%d] has no output yet\n", cmd->name, cmd->pid);
	} else { 
		printf("@<<< Output for %s[#%d] (%d bytes):\n", cmd->name, cmd->pid, cmd->output_size);
		printf("----------------------------------------\n");
		printf("%.*s", cmd->output_size,(char *)cmd->output);	//print the cmd->output line
		printf("----------------------------------------\n");
	}
}


