#include <stdio.h> /* printf */
#include <stdlib.h> /* malloc, free */
#include <unistd.h> /* sleep */
#include <assert.h> /* assert */

#define true (1 == 1)
#define false (1 == 0)

// Struct to track locking params for peterson
typedef struct PetersonFields {
    int flags[2];
    int victim;
} PetersonFields;

// Struct for organizing Peterson's into tree
typedef struct Tree {
    // 
    int num_threads;
    int num_locks;
    int num_levels;
    int leaf_lock_0;

    // 
    void (*lock)(struct Tree* tree, int thread_id);
    void (*unlock)(struct Tree* tree, int thread_id);

    PetersonFields* locks;
} Tree;

int get_parent(int i) {
    if (i <= 0) return -1;
    return (i - 1) / 2;
}

// Lock func
void lock(Tree* tree, int thread_id) {
    // Base cases
    if (!tree) return;
    if (thread_id < 0 || thread_id >= tree->num_threads) {
        fprintf(stderr, "lock(): invalid thread_id %d\n", thread_id);
        return;
    }
    if (tree->num_levels == 0) {
        // case no lock needed b/c only 1 thread
        return;
    }

    // Logic
    int curr = tree->leaf_lock_0 + thread_id / 2;
    int i = thread_id % 2; // initial side decided by thread id parity (not case after lock leaf lock of tree)
    
    for (int level = 0; level < tree->num_levels; level++) {
        PetersonFields* p = &(tree->locks[curr]);
        p->flags[i] = true;
        p->victim = i;
        while (p->flags[1 - i] && p->victim == i) {}
        // Prepare for parent
        i = curr % 2; // left child -> odd, right child -> even.
        curr = get_parent(curr);
    }
}

// Unlock func
void unlock(Tree* tree, int thread_id) {
    // 
    if (!tree) return;
    if (thread_id < 0 || thread_id >= tree->num_threads) {
        fprintf(stderr, "unlock(): invalid thread_id %d\n", thread_id);
        return;
    }
    if (tree->num_levels == 0) {
        return;
    }

    // 
    int curr = tree->leaf_lock_0 + thread_id / 2;
    int i = thread_id % 2;
    
    for (int level = 0; level < tree->num_levels; level++) {
        PetersonFields* p = &(tree->locks[curr]);
        p->flags[i] = false;
        i = curr % 2;
        curr = get_parent(curr);
    }
}

// Init 
Tree* constructor(const int num_threads) {
    // Assert num_threads is power of 2
    int pow = -1;
    int temp = num_threads;
    while (temp) {
        temp >>= 1;
        pow += 1;
    }

    const int num_levels = (pow > 0) ? pow : 0;          // Handy constant calculation done
    if ((1 << num_levels) != num_threads) {
        fprintf(stderr, "constructor(): num_threads (%d) must be a power of 2.\n", num_threads);
        return NULL;
    }
    
    // Init tree
    Tree* tree = (Tree*)malloc(sizeof(Tree));
    if (!tree) {
        fprintf(stderr, "Failed to allocate Tree struct.\n");
        return NULL;
    }

    const int num_locks = num_threads - 1;
    tree->num_threads = num_threads;
    tree->num_locks = num_locks;
    tree->leaf_lock_0 = (num_locks + 1) / 2 - 1;
    tree->num_levels = num_levels;

    tree->lock = lock;
    tree->unlock = unlock;

    tree->locks = (PetersonFields*)calloc(num_locks, sizeof(PetersonFields));
    if (!tree->locks) {
        fprintf(stderr, "Failed to allocate locks array.\n");
        free(tree);
        return NULL;
    }
    for (int i = 0; i < num_locks; i++) {
        tree->locks[i].flags[0] = false, tree->locks[i].flags[1] = false;
        tree->locks[i].victim = 0;
    }

    return tree;
}

// Destroy
// TODO: Test this func works
void destructor(Tree* tree) {
    if (!tree) return;
    free(tree->locks);
    free(tree);
}
