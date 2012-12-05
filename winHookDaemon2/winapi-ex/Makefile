all:
	gcc winthread.c -o winthread -lpthreadGC2
	gcc winserver.c -o winserver -lwsock32 -lpthreadGC2

# note : -lpthreadGC2 must be placed at the end 

.PHONY: clean
	
clean:
	rm *.exe 