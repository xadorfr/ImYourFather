package fr.labri.reparenting.api.core.winapi;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.ptr.LongByReference;

public interface Kernel32Custom extends Kernel32 {
	Kernel32Custom INSTANCE = (Kernel32Custom) Native.loadLibrary("kernel32.dll", Kernel32Custom.class);
	
	/*
     * SCS_WOW_BINARY   : A 16-bit Windows-based application 	
	 * SCS_32BIT_BINARY : A 32-bit Windows-based application
	 * SCS_64BIT_BINARY : A 64-bit Windows-based application.
	 * SCS_DOS_BINARY   : An MS-DOS � based application
	 * SCS_OS216_BINARY : A 16-bit OS/2-based application
	 * SCS_PIF_BINARY   : A PIF file that executes an MS-DOS � based application
	 * SCS_POSIX_BINARY : A POSIX � based application
	 */
	public enum BinaryTypes { SCS_32BIT_BINARY, SCS_DOS_BINARY, SCS_WOW_BINARY, SCS_PIF_BINARY, SCS_POSIX_BINARY, SCS_OS216_BINARY, SCS_64BIT_BINARY};

	/**
	 * 
	 * @param _in  lpApplicationName
	 * @param _out binaryType
	 * Determines whether a file is an executable (.exe) file, and if so, which subsystem runs the executable file.
	 */
	boolean GetBinaryType(String lpApplicationName, LongByReference binaryType);
	
	
	/**
	 * 
	 * @param FileName
	 * Get the winapi handle to a dll
	 */
	LongByReference LoadLibraryA(String FileName);
	LongByReference LoadLibraryW(String FileName);
	
	/**
	 * 
	 * @param dllHandle
	 * @param functionName
	 * Get the winapi handle to a dll function 
	 */
	LongByReference GetProcAddress(LongByReference dllHandle, String functionName);
}
