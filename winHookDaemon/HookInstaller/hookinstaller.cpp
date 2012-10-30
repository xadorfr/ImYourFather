#include <stdio.h>
#include <tchar.h>
#include "Client.h"
#include <HookProc.h>

int _tmain(int argc, _TCHAR* argv[])
{
	int port = 0;
	if(argc > 1) {
		port = _tstoi(argv[1]);
	}

	if(port < 1023 || port > 49151) {
		fprintf(stderr, "usage %s [port : 1024-49151]", argv[0]);
		return EXIT_FAILURE;
	}

	HookProcSetTid(GetCurrentThreadId());

	HINSTANCE hModule = HookProcGetInstance();
	if(hModule == NULL) {
		fprintf(stderr, "ERROR LoadLibrary");
		return EXIT_FAILURE;
	}

	HHOOK hhk = SetWindowsHookEx(WH_CBT, HookProc, hModule, 0);
	if(hhk == NULL) {
		fprintf(stderr, "ERROR SetWindowsHookEx");
		return EXIT_FAILURE;
	}

	bool res;
	Client client(true);

	if (! client.Connect(port, "127.0.0.1")) {
		return EXIT_FAILURE;
	}	

	/* message loop in order to get the hwnd of the created window */
	MSG msg;
	HWND threadOnly = (HWND) -1; // -1 : only message pass to thread
	int intCreatedWin;
	int creation = 0;
	int intDestroyedWin;
	int destruction = 1;
	while(GetMessage(&msg, threadOnly, 0, 0)) {
		switch(msg.message) {

		case CUSTOM_MSG_CREATION : {
			HWND createdWin = (HWND) msg.wParam;
			intCreatedWin = (int) createdWin;

			/* "64-bit versions of Windows use 32-bit handles for interoperability."
			@see http://msdn.microsoft.com/en-us/library/aa384203%28VS.85%29.aspx */
			client.SendInts(&creation, 1);
			client.SendInts(&intCreatedWin, 1);
			break;
							}
		case CUSTOM_MSG_DESTRUCTION : {
			HWND destroyedWin = (HWND) msg.wParam;
			intDestroyedWin = (int) destroyedWin;

			client.SendInts(&destruction, 1);
			client.SendInts(&intDestroyedWin, 1);
							}
		}
	}
	return EXIT_SUCCESS;
}

