all :
	gcc -c hookproc.c
	gcc -shared -o HookProc.dll hookproc.o hookproc.def 
	gcc hookserver.c -o hookserver.exe -std=gnu99
	
# -Wl,--out-implib,libhookproc.a
.PHONY: clean
	
clean:
	rm *.o *.exe *.dll
