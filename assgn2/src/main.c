#include <stdio.h> /* printf */
#include <pthread.h> /* pthread_t, pthread_create, pthread_join */
#include <stdlib.h> /* atoi */
#include <unistd.h> /* sleep */
#include "TreeLock.h" /* Tree, lock, unlock */

// Benchmarking
#include <time.h> /* clock, time */

volatile int shared_counter = 0;
static Tree* global_tree = NULL;

void* thread_func(void* arg) {
    int thread_id = *(int*)arg;
    Tree* tree = global_tree;

    tree->lock(tree, thread_id);
    // sleep(1);
    shared_counter += 1;
    tree->unlock(tree, thread_id);
    return NULL;
}

int main() {
    // Get num threads from terminal

    printf("In this Tree-Lock demo, we'll increment a shared_counter once per thread.\n");
    printf("How many threads do you want to test? : ");
    
    char buffer[20];
    fflush(stdin);
    fgets(buffer, 8, stdin);

    int num_threads = atoi(buffer);
    printf("You want to test with %d threads.\n", num_threads);

    // Construct lock
    Tree* tree = constructor(num_threads);
    if (!tree) {
        fprintf(stderr, "Failed to construct tree lock. Exiting.\n");
        return 1;
    }
    global_tree = tree;

    // Bench: start time
    struct timespec ts_start, ts_end;
    clock_gettime(CLOCK_MONOTONIC, &ts_start);

    pthread_t threads[num_threads];
    int thread_ids[num_threads];

    for (int i = 0; i < num_threads; ++i) {
        thread_ids[i] = i;
        // printf("Main: Creating thread %d\n", i);
        pthread_create(&threads[i], NULL, &thread_func, &thread_ids[i]);
    }

    for (int i = 0; i < num_threads; ++i) {
        // printf("Main: Joining thread %d\n", i);
        pthread_join(threads[i], NULL);
    }

    // Bench: end time
    clock_gettime(CLOCK_MONOTONIC, &ts_end);
    long long elapsed_ns =
        (long long)(ts_end.tv_sec - ts_start.tv_sec) * 1000000000LL +
        (ts_end.tv_nsec - ts_start.tv_nsec);

    // Result
    printf("After executing %d threads for %.4lld ns, the shared_counter = %d !\n", num_threads, elapsed_ns, shared_counter);

    // Cleanup
    destructor(tree);
    global_tree = NULL;

    // printf("All threads finished.\n");
    return 0;
}