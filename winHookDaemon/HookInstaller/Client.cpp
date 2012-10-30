#include "Client.h"

Client::Client(bool debug) 
{
	m_debug = debug;
}

bool Client::Connect(int iPort, const char* pStrHost)
{
	struct hostent*	he = NULL;
	m_iPort = iPort;

	WORD wVersionRequested;
	WSADATA wsaData;

	/* win32 sock API init */
	wVersionRequested = MAKEWORD(2, 2);
	int err = WSAStartup(wVersionRequested, &wsaData);
	if (err != 0) {
		if(m_debug) {
			perror("WSAStartup failed with error");
		}
		return false;
	}

	he = gethostbyname(pStrHost);
	if(he == NULL) {
		if (m_debug) {
			perror("gethostbyname error");
		}
		return false;
	}

	m_iSock = socket(AF_INET, SOCK_STREAM, 0);
	if (m_iSock == INVALID_SOCKET) {
		if (m_debug) {
			fprintf(stderr, "socket error, errnum : %d\n", WSAGetLastError());
		}
		return false;
	}

	m_addrRemote.sin_family		= AF_INET;        
	m_addrRemote.sin_port		= htons(m_iPort);      
	m_addrRemote.sin_addr		= *((struct in_addr *) he->h_addr); 
	memset(&(m_addrRemote.sin_zero), 0, 8);

	if (connect(m_iSock, (struct sockaddr *) &m_addrRemote, sizeof(struct sockaddr)) == -1) {
		if(m_debug) {
			perror("connect m_iSock");
		}
		return false;
	}

	if (m_debug) {
		fprintf(stderr, "Connected to %s on port %d\n", pStrHost, m_iPort);
	}

	return true;
}

bool Client::SendInts(int* pVals, int iLen)
{
	int netVals = ntohl(*pVals);

	if (send(m_iSock, (char *) &netVals, sizeof(int) * iLen, 0) == -1) {
		if(m_debug) {
			perror("send ints");              
		}
		return false;
	}

	if(m_debug) {
		printf("Client sends %d ints - ", iLen);                       
		for (int i = 0; i < iLen; i++)
			printf("%d ", pVals[i]);
		printf("\n");
	}

	return true;
}

Client::~Client()
{
	WSACleanup();
}
