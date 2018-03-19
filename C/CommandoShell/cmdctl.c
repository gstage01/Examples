#include <stdlib.h>
#include "commando.h"

//Adds a command to the ctl, incrementing size
void cmdctl_add(cmdctl_t *ctl, cmd_t *cmd) {
	int i=ctl->size;
	if (i>MAX_CHILD-1) {
		printf("\nCommand array is full!\n");	
		return;
	}
	ctl->cmd[i] = cmd;
	ctl->size++;
}

//Prints information about the commands in the ctl
void cmdctl_print(cmdctl_t *ctl) {
	int i=0;
	int job = 0;
	printf("JOB  #PID      STAT   STR_STAT OUTB COMMAND\n");
	while (i<ctl->size) {
		if (ctl->size == 0) {
			break;
		} else {
		
			cmd_t *cmd = ctl->cmd[i]; //Create a pointer to the command 
			int j = 1;
			char buf[MAX_LINE];
			strcpy(buf, cmd->argv[0]); //Copy the contents of argv into a printable buffer
			while (cmd->argv[j] != NULL) {
				strcat(strcat(buf, " "), cmd->argv[j]);
				j++;
			}
			strcat(buf, "\0");

			//Buffers for printing integer outputs
			char *jobBuf = malloc(4);
			char *pidBuf = malloc(8);
			char *statBuf = malloc(4);
			char *outBuf = malloc(4);
			sprintf(jobBuf, "%d", job);
			sprintf(pidBuf, "%d", cmd->pid);
			sprintf(statBuf, "%d", cmd->status);
			sprintf(outBuf, "%d", cmd->output_size);
			printf("%-4s #%-8s %4s %10s %4s %s \n", jobBuf, pidBuf, statBuf, cmd->str_status, outBuf, buf); //print with given format
			free(jobBuf);
			free(pidBuf);
			free(statBuf);
			free(outBuf);
			job++;
		}
	i++;
	}
}

//Updates the state of every command in the ctl
void cmdctl_update_state(cmdctl_t *ctl, int block) {
	int i = 0;
	while (i<ctl->size) {
		if (ctl->cmd[i] == NULL) {
			i++;
			continue;
		}
		cmd_update_state(ctl->cmd[i], block);
		i++;
	}
}

//Frees all commands in the ctl
void cmdctl_freeall(cmdctl_t *ctl) {
	int i = 0;
	if (ctl->size > 0) {
		while (i<ctl->size) {
			cmd_free(ctl->cmd[i]);
			i++;
		}
	}
}



