#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>  
void*
service(void* port)
{	
    SOCKET sock_server;

    WSADATA wsaData;
    SOCKADDR_IN local;

    int wsaret = WSAStartup(MAKEWORD(2, 2), &wsaData);
    if(wsaret != 0) {
        return 0;
    }

    local.sin_family = AF_INET;
    local.sin_addr.s_addr = INADDR_ANY;
    local.sin_port = htons((u_short) 5555);

    sock_server = socket(AF_INET,SOCK_STREAM, 0);
    if(sock_server == INVALID_SOCKET) {
        return 0;
    }

    if(bind(sock_server, (SOCKADDR*) &local, sizeof(local)) != 0) {
        return 0;
    }

    if(listen(sock_server, 10) != 0) {
        return 0;
    }

    SOCKET client;
    SOCKADDR_IN from;
    int fromlen = sizeof(from);

    while(1) {
		char test[] = "lol";
        client = accept(sock_server, (struct sockaddr*)&from, &fromlen);
        send(client, test, strlen(test), 0);
		
        closesocket(client);
    }

    closesocket(sock_server);
    WSACleanup(); 
	
    return 0;
}

int main(int argc, char** argv)
{
    int nRetCode = 0;
	pthread_t thread;
	
    int rc = pthread_create(&thread, NULL, service, (void*) 0);
	if(rc != 0) {
      exit(EXIT_FAILURE);
	}
	
	pthread_exit(NULL);
    return nRetCode;
}