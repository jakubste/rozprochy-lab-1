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

#define BUFLEN 10000




/* uncomment the following line to use 'long long' integers */
#define HAS_LONG_LONG

#ifdef HAS_LONG_LONG
#define mul_mod(a,b,m) (( (long long) (a) * (long long) (b) ) % (m))
#else
#define mul_mod(a,b,m) fmod( (double) a * (double) b, m)
#endif

/* return the inverse of x mod y */
int inv_mod(int x, int y)
{
    int q, u, v, a, c, t;

    u = x;
    v = y;
    c = 1;
    a = 0;
    do {
    q = v / u;

    t = c;
    c = a - q * c;
    a = t;

    t = u;
    u = v - q * u;
    v = t;
    } while (u != 0);
    a = a % y;
    if (a < 0)
    a = y + a;
    return a;
}

/* return (a^b) mod m */
int pow_mod(int a, int b, int m)
{
    int r, aa;

    r = 1;
    aa = a;
    while (1) {
    if (b & 1)
        r = mul_mod(r, aa, m);
    b = b >> 1;
    if (b == 0)
        break;
    aa = mul_mod(aa, aa, m);
    }
    return r;
}

/* return true if n is prime */
int is_prime(int n)
{
    int r, i;
    if ((n % 2) == 0)
    return 0;

    r = (int) (sqrt(n));
    for (i = 3; i <= r; i += 2)
    if ((n % i) == 0)
        return 0;
    return 1;
}

/* return the prime number immediatly after n */
int next_prime(int n)
{
    do {
    n++;
    } while (!is_prime(n));
    return n;
}

unsigned char get_digit(long long n)
{
    int av, a, vmax, N, num, den, k, kq, kq2, t, v, s, i;
    double sum;

    N = (int) ((n + 20) * log(10) / log(2));

    sum = 0;

    for (a = 3; a <= (2 * N); a = next_prime(a)) {

    vmax = (int) (log(2 * N) / log(a));
    av = 1;
    for (i = 0; i < vmax; i++)
        av = av * a;

    s = 0;
    num = 1;
    den = 1;
    v = 0;
    kq = 1;
    kq2 = 1;

    for (k = 1; k <= N; k++) {

        t = k;
        if (kq >= a) {
        do {
            t = t / a;
            v--;
        } while ((t % a) == 0);
        kq = 0;
        }
        kq++;
        num = mul_mod(num, t, av);

        t = (2 * k - 1);
        if (kq2 >= a) {
        if (kq2 == a) {
            do {
            t = t / a;
            v++;
            } while ((t % a) == 0);
        }
        kq2 -= a;
        }
        den = mul_mod(den, t, av);
        kq2 += 2;

        if (v > 0) {
        t = inv_mod(den, av);
        t = mul_mod(t, num, av);
        t = mul_mod(t, k, av);
        for (i = v; i < vmax; i++)
            t = mul_mod(t, a, av);
        s += t;
        if (s >= av)
            s -= av;
        }

    }

    t = pow_mod(10, n - 1, av);
    s = mul_mod(s, t, av);
    sum = fmod(sum + (double) s / (double) av, 1.0);
    }
    return 10*sum;
}



int main(int argc, char **argv) {
	int sock_fd, cli_fd;
	int len;
	socklen_t cli_len;
	struct sockaddr_in serv_addr;
	struct sockaddr_in cli_addr;
	unsigned char sendline[1];
	char recvline[BUFLEN];
	int ret;

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

		for (int n = 8; n >= 0; n--)
      		recvline[n] = 0;

		// receive data to recvline buffer with the "recv" system call and assign number of received bytes to len
		len = recv(cli_fd, recvline, BUFLEN, 0);
		printf("received bytes: %d\n", len);
		recvline[len] = 0;
		// printf("received: %s\n", recvline);

		num = 0;
		for (int n = 0; n < len; n++)
		{
			// printf("%lld, %d, %d\n", num, n, recvline[n]);
      		unsigned char dd = recvline[ n ];
      		num = (num << 8) + dd;
      	}
      	
      	printf("%lld\n", num);

      	unsigned char digit = get_digit(num);

		printf("%lld | %d\n", num, digit);

		sendline[0] = digit;

		// send sendline buffer with the "send" system call and assign number of sent bytes to len
		len = send(cli_fd, sendline, 1, 0);
		printf("sent bytes: %d\n", len);
		printf("sent: %d\n", sendline[0]);

		close(cli_fd);
	}

	return EXIT_SUCCESS;
}
