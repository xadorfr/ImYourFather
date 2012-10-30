/**
 * Important : HookProc() is a Hook fonction, i.e. executed by a thread of the application which is creating, activating and destroying a window
 */

#include "HookProc.h"
#include <stdio.h>

/* TODO (todo tag - code search : #MultiClient)
 * atm the DLL is not prepared to be used by several client programs at the same time
 * a solution would be a collection of thread id : thread id of client programs which await for window's handles
 * In between time, the boolean "loaded" avoid multi programs attachment 
 */
#pragma data_seg(".shared")
bool loaded = false;
DWORD threadId = 0;
#pragma data_seg()
#pragma comment(linker, "/SECTION:.shared,RWS") // /!\ very important to make shared variable available for all DLL instances

HINSTANCE hInstance = NULL;

BOOL APIENTRY DllMain( HMODULE hModule,
                       DWORD  ul_reason_for_call,
                       LPVOID lpReserved
					 )
{
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH: {
		/* TODO#MultiClient */
		if(loaded == true) {
			return FALSE;
		}
		loaded = true;
		hInstance = hModule;
		break;
							 }
	case DLL_THREAD_ATTACH:
		break;
	case DLL_THREAD_DETACH:
		break;
	case DLL_PROCESS_DETACH: {
		if(loaded) {
			loaded = false;
		}
		break;
							 }
	}
	return TRUE;
}

/*
 * wParam : not used
 * lParam : A pointer to a CWPSTRUCT structure that contains details about the message. 
 * CWPSTRUCT description : http://msdn.microsoft.com/en-us/library/windows/desktop/ms644964%28v=vs.85%29.aspx
 */
LRESULT CALLBACK HookProc (int nCode, WPARAM wParam, LPARAM lParam) {
	if (nCode < 0) {
		return CallNextHookEx(NULL, nCode, wParam, lParam);
	}

	switch(nCode) {

	case HCBT_CREATEWND: {
		// tag the created window
		HWND win = (HWND) wParam;
		SetProp(win, WIN_TAG_CREATED, win); 
		break;
						 }
	case HCBT_ACTIVATE: {
		/* WPARAM -> handle to the new window
		 LPARAM -> long pointer to a CBT_CREATEWND structure */
		
		/* check tag */
		HWND win = (HWND) wParam;
		if (GetProp(win, WIN_TAG_CREATED) == NULL) {
			// if the tag tagCreated isn't detected, it means that the activated window has not been freshly created
			break;
		}
		RemoveProp(win, WIN_TAG_CREATED);

		CBTACTIVATESTRUCT* actStruct = (CBTACTIVATESTRUCT*) lParam;
		if(! actStruct->fMouse) { // not activate by a mouse action
			/* TODO#MultiClient */
			if(threadId != 0) {
				PostThreadMessage(threadId, CUSTOM_MSG_CREATION, (WPARAM) win, NULL);
				// TODO : find out how to tag it from java (in order to tag reparented windows only)
				SetProp(win, WIN_TAG_REPARENTED, win);
			}
		}
		break;
						}

	case HCBT_DESTROYWND: {
		HWND win = (HWND) wParam;
		/* TODO#MultiClient */
		if (GetProp(win, WIN_TAG_REPARENTED) == NULL || threadId == 0) {
			// the only windows we are supposed to be interrested in are the one which are reparented
			break;
		}
		PostThreadMessage(threadId, CUSTOM_MSG_DESTRUCTION, (WPARAM) win, NULL);
						  }
	}

	// msdn : "If the hook procedure returns a nonzero value, the system destroys the window"
	return CallNextHookEx(NULL, nCode, wParam, lParam);	
}

/*
 * This setter is used in order to suscribe to notifications : 
 * any PostThreadMessage() in HookProc()
 * 
 * TODO#MultiClient : change this setter by a "addClientThreadId" function 
 * in order to make this DLL useable by several programs
 */
void HookProcSetTid(DWORD tid) {
	threadId = tid;
}

/* we need this getter in order to install the hook 
 *  @see SetWindowsHookEx function (winapi);
 */
HINSTANCE HookProcGetInstance() {
	return hInstance;
}
