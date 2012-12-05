#include <pthread.h>                                         
#include <stdio.h>                                           
#include <stdlib.h>                                          

#define NUM_THREADS 5

void* print_hello(void *thread_id) {
  long tid;                         
  tid = (long)thread_id;            
  printf("Thread #%ld responding.\n", tid);
  pthread_exit(NULL);                      
  return NULL;                             
}                                          

int main(int argc, char *argv[]) {
  pthread_t threads[NUM_THREADS];
  pthread_attr_t attr;
  int rc, status;
  long i;

  for (i = 0; i < NUM_THREADS; i++) {
    printf("In main: creating thread %ld\n", i);
    rc = pthread_create(&threads[i], NULL, print_hello, (void *)i);
    if (rc) {
      printf("Error: return code from pthread_create() is %d\n", rc);
      exit(EXIT_FAILURE);
    }
  }

  pthread_attr_destroy(&attr);
  for (i = 0; i < NUM_THREADS; i++) {
    rc = pthread_join(threads[i], (void **)&status);
    if (rc) {
      printf("Error: return code from pthread_join() is %d\n", rc);
      exit(EXIT_FAILURE);
    }
    printf("Completed join with thread %d, status = %d\n", i, status);
  }
  pthread_exit(NULL);

  exit(EXIT_SUCCESS);
}