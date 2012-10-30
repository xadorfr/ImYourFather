#include <windows.h>

// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the HOOKPROC_EXPORTS
// symbol defined on the command line. This symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// HOOKPROC_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.
#ifdef HOOKPROC_EXPORTS
#define HOOKPROC_API __declspec(dllexport)
#else
#define HOOKPROC_API __declspec(dllimport)
#endif


HOOKPROC_API LRESULT CALLBACK HookProc (int nCode, WPARAM wParam, LPARAM lParam);
HOOKPROC_API void HookProcSetTid(DWORD tid);
HOOKPROC_API HINSTANCE HookProcGetInstance();

/** constant */

const UINT CUSTOM_MSG_CREATION = WM_APP + 1;
const UINT CUSTOM_MSG_DESTRUCTION = WM_APP + 2;

const LPCWSTR WIN_TAG_CREATED = L"CEA_JUSTCREATED";
const LPCWSTR WIN_TAG_REPARENTED = L"CEA_REPARENTED";


