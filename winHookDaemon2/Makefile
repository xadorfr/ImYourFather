all :
	gcc -c hookproc.c
	gcc -shared -o HookProc.dll hookproc.o hookproc.def 
	gcc hookserver.c -o hookserver.exe -std=gnu99 -lwsock32 -lpthread

.PHONY: clean
	
clean:
	del *.o *.exe *.dll
