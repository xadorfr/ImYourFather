#include <stdio.h>
#include "hookproc.h"

typedef LRESULT CALLBACK (*HookFunc) (int nCode, WPARAM wParam, LPARAM lParam);
typedef void (*SetTidFunc) (DWORD tid);

int main(int argc, char** argv)
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
	if (!hkModule) {
		fprintf(stderr, "Error loading \"HookProc.dll\".\n");
		return EXIT_FAILURE;
	}
	
	HookFunc Hook =(HookFunc) GetProcAddress(hkModule, "HookProc");
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
	SetTid(GetCurrentThreadId());

	HHOOK hhk = SetWindowsHookEx(WH_CBT, Hook, hkModule, 0);
	if(hhk == NULL) {
		fprintf(stderr, "ERROR SetWindowsHookEx");
		return EXIT_FAILURE;
	}

	// TOD0 select() server
	// client array - sending routine - mutex ... 
	
	
	return EXIT_SUCCESS;
}
