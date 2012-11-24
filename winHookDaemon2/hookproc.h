#ifndef HOOKPROC_H
#define HOOKPROC_H

#ifdef __cplusplus
extern "C"
{
#endif

#include <windows.h>

LRESULT CALLBACK HookProc (int nCode, WPARAM wParam, LPARAM lParam);
void HookProcSetTid(DWORD tid);

/** constant */

const UINT CUSTOM_MSG_CREATION = WM_APP + 1;
const UINT CUSTOM_MSG_DESTRUCTION = WM_APP + 2;

const LPCSTR WIN_TAG_CREATED = "JUSTCREATED";
const LPCSTR WIN_TAG_REPARENTED = "REPARENTED";

#ifdef __cplusplus
}
#endif

#endif