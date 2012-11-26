#include <stdio.h>
#include "hookproc.h"

typedef LRESULT CALLBACK (*HookFunc) (int nCode, WPARAM wParam, LPARAM lParam);
typedef void (*SetTidFunc) (DWORD tid);

int 
main(int argc, char** argv)
{
	int port = 0;
	if(argc > 1) {
		port = atoi(argv[1]);
	}

	if(port < 1023 || port > 49151) {
		fprintf(stderr, "usage %s [port : 1024-49151]", argv[0]);
		return EXIT_FAILURE;
	}
	
	HMODULE hkModule = LoadLibrary(TEXT("HookProc.dll"));
	if (! hkModule) {
		fprintf(stderr, "Error loading \"HookProc.dll\".\n");
		return EXIT_FAILURE;
	}
	
	HookFunc Hook = (HookFunc) GetProcAddress(hkModule, "HookProc");
	if (! Hook) {
		fprintf(stderr, "Error locating \"HookProc\" function.\n");
		return EXIT_FAILURE;
    }
	
	SetTidFunc SetTid = (SetTidFunc) GetProcAddress(hkModule, "HookProcSetTid");
	if (! SetTid) {
		fprintf(stderr, "Error locating \"SetTid\" function.\n");
		return EXIT_FAILURE;
    }
	// printf("SetTid function is located at address %p.\n", SetTid);
	int myTid = GetCurrentThreadId();
	SetTid(myTid);

	// printf("TID is : %d \n", myTid);
	
	HHOOK hhk = SetWindowsHookEx(WH_CBT, Hook, hkModule, 0);
	if(hhk == NULL) {
		fprintf(stderr, "ERROR SetWindowsHookEx");
		return EXIT_FAILURE;
	}

	// TOD0 select() server
	// client array - sending routine - mutex ... 
	
	/* message loop in order to get the hwnd of the created window */
	MSG msg;
	HWND threadOnly = (HWND) -1; // -1 : only message pass to thread
	int intCreatedWin;
	int creation = 0;
	int intDestroyedWin;
	int destruction = 1;
	while(GetMessage(&msg, threadOnly, 0, 0)) {
		printf("Get a message\n");
		
		if(msg.message == CUSTOM_MSG_CREATION) {
			HWND createdWin = (HWND) msg.wParam;
			intCreatedWin = (int) createdWin;

			/* "64-bit versions of Windows use 32-bit handles for interoperability."
			@see http://msdn.microsoft.com/en-us/library/aa384203%28VS.85%29.aspx */
			
			printf("created : %d\n", intCreatedWin);
			fflush(stdout);
			continue;
		}
		
		if(msg.message == CUSTOM_MSG_DESTRUCTION) {
			HWND destroyedWin = (HWND) msg.wParam;
			intDestroyedWin = (int) destroyedWin;
			printf("destroyed : %d\n", intDestroyedWin);
			fflush(stdout);
		}
	}
	
	return EXIT_SUCCESS;
}
