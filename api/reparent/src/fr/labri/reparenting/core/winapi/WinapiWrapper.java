package fr.labri.reparenting.core.winapi;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

/**
 * Winapi call for reparenting
 * TODO pass long handle to int handle
 */
public class WinapiWrapper {
	private static User32Custom user32 = User32Custom.INSTANCE;
	private static Kernel32Custom kernel32 = Kernel32Custom.INSTANCE;
	
	private static int WS_CLIPCHILDREN = 0x02000000;
	private static int WS_CLIPSIBLINGS = 0x04000000;
	
	public static long getExecutableType(String path) {
		LongByReference ret = new LongByReference();
		kernel32.GetBinaryType(path, ret);
		return ret.getValue();
	}
	
	public static long findWindow(String className, String name) {
		HWND handle = user32.FindWindow(className, name);
		if(handle == null)
			return 0;
		
		Pointer pointer = handle.getPointer();
		return Pointer.nativeValue(pointer);
	}
	
	public static long reparent(long handleChild, long handleParent) {
			if(handleChild == 0 || handleParent == 0)  {
				return 0;
			}
			
			HWND hwndChild = new HWND(new Pointer(handleChild));
			HWND hwndParent = new HWND(new Pointer(handleParent));

			// If the function fails, the return value is zero. To get extended error information, call GetLastError.
			int status = user32.SetWindowLong(hwndChild, WinUser.GWL_STYLE, WinUser.WS_VISIBLE | WS_CLIPCHILDREN | WS_CLIPSIBLINGS);
			
			
			if(status == 0) {
				return 0;
			}
			
			/* If the function succeeds, the return value is a handle to the previous parent window.
			 * If the function fails, the return value is NULL. To get extended error information, call GetLastError. */
			HWND prevHwnd = user32.SetParent(hwndChild, hwndParent);
			if(prevHwnd == null) {
				return 0;
			}

			return prevHwnd.getPointer().getLong(0);
	}
		
	public static String GetWindowName(long handle) {
		HWND hwnd = new HWND(new Pointer(handle));
		char name[] = new char[255];
		user32.GetWindowText(hwnd, name, 255);
		String s = new String(name);
		return s.trim();
	}

	public static String GetClassName(long handle) {
		HWND hwnd = new HWND(new Pointer(handle));
		char name[] = new char[255];
		user32.GetClassName(hwnd, name, 255);
		String s = new String(name);
		return s.trim();
	}
	
	public static int GetWindowPid(long handle) {
		HWND hwnd = new HWND(new Pointer(handle));
		IntByReference pid = new IntByReference();
		user32.GetWindowThreadProcessId(hwnd, pid);
		return pid.getValue();
	}
	
	public static void SetProp(long handle, String prop, long valueHandle) {
		HWND hwnd = new HWND(new Pointer(handle));
		user32.SetProp(hwnd, prop, hwnd);
	}
	
	public static boolean hasProp(long handle, String prop) {
		HWND hwnd = new HWND(new Pointer(handle));
		return (user32.GetProp(hwnd, prop) != null);
	}
	
	public static void DestroyWindow(long handle) {
		HWND hwnd = new HWND(new Pointer(handle));
		user32.DestroyWindow(hwnd);
	}
	
	public static long GetParent(long handle) {
		HWND hwnd = new HWND(new Pointer(handle));
		HWND parentHwnd = user32.GetParent(hwnd);
		if(parentHwnd == null) {
			return 0;
		}
		return parentHwnd.getPointer().getLong(0);
	}
}
