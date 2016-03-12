/* Sample TCP server */

#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <unistd.h>
#include <math.h>
#include <errno.h>


#define BUFLEN 16 * 1024


int main(int argc, char **argv) {
	int sock_fd, cli_fd;
	int len;
	socklen_t cli_len;
	struct sockaddr_in serv_addr;
	struct sockaddr_in cli_addr;
	unsigned char sendline[1];
	char recvline[BUFLEN];
	int ret;
    FILE *received_file;

	if (argc != 2) {
		printf("usage: %s <TCP port>\n", argv[0]);
		exit(EXIT_FAILURE);
	}

	// create the socket (add missing arguments)
	sock_fd = socket(AF_INET, SOCK_STREAM, 0);
	if (!sock_fd) {
		perror("socket");
		exit(EXIT_FAILURE);
	}

	bzero(&serv_addr, sizeof(serv_addr));
	// fill in the socket family, address and port
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	serv_addr.sin_port = htons(atoi(argv[1]));

	// set SO_REUSEADDR socket option (please explain the option's meaning)
	int so_reuseaddr = 1;
	ret = setsockopt(sock_fd,SOL_SOCKET,SO_REUSEADDR,&so_reuseaddr, sizeof so_reuseaddr);
	if (ret<0) {
		perror("setsockopt");
	}

	// bind with the use of bind procedure
	ret = bind(sock_fd, (struct sockaddr*)&serv_addr, sizeof(serv_addr));
	if (ret<0) {
		perror("bind");
	}

	// start listening with the use of listen procedure
	listen(sock_fd, 5);

	long long num;

	while (1) {
		// accept the connection and assign descriptor to cli_fd
		cli_fd = accept(sock_fd, (struct sockaddr*)&cli_addr, &cli_len);

		// receive data to recvline buffer with the "recv" system call and assign number of received bytes to len
        len = recv(cli_fd, recvline, BUFLEN, 0);
        printf("received bytes: %d\n", len);
        recvline[len] = 0;
        printf("received: %s\n", recvline);

        received_file = fopen(recvline, "w");

        if (received_file == NULL)
        {
                fprintf(stderr, "Failed to open file foo --> %s\n", strerror(errno));

                exit(EXIT_FAILURE);
        }


        while (len > 0) {
            len = recv(cli_fd, recvline, BUFLEN, 0);
            printf("received bytes: %d\n", len);
            recvline[len] = 0;
            printf("received: %s\n", recvline);
            fwrite(recvline, sizeof(char), len, received_file);
        }

        fclose(received_file);
		close(cli_fd);
	}

	return EXIT_SUCCESS;
}
