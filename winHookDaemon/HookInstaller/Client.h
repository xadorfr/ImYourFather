#ifndef _CLIENT_H__
#define _CLIENT_H__


#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <winsock2.h>


class Client
{
	public:
		Client(bool debug);
		bool Connect(int iPort, const char* pStrHost);
		~Client();

		bool				Close();
		bool				SendInts(int* pVals, int iLen);

	protected:
		bool				m_debug;
		int					m_iPort;
		SOCKET				m_iSock;
		struct sockaddr_in	m_addrRemote;
};

#endif

