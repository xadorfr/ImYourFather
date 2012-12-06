all :
	gcc -c hookproc.c
	gcc -shared -o HookProc.dll hookproc.o hookproc.def 
	gcc hookserver.c -o hookserver.exe -std=gnu99 -lwsock32 -lpthreadGC2
	
# -Wl,--out-implib,libhookproc.a
.PHONY: clean
	
clean:
	rm *.o *.exe *.dll
