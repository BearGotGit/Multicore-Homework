# Homework 2 – Tree Lock

Berend Grandt  
(README structure tidied with help from ChatGPT)

## Tree Lock Implementation

I implement a binary tree of 2-thread Peterson locks (a “tree lock”).  
Each internal node is a Peterson lock; threads correspond to leaves (assume number of threads is a power of two).

Acquire (my implementation):

- Thread computes its leaf node (shared by a pair of threads).
- It ascends leaf → parent → … → root, acquiring each Peterson lock on the path.

Release (my implementation):

- It currently also walks leaf → … → root clearing flags (NOT root→leaf reverse order).
- This is a simplification; conventional versions release in reverse (root downward) but correctness (mutual exclusion) still holds; performance/fairness characteristics may differ slightly.

Each Peterson node guarantees only one “winner” proceeds upward, so only one thread can reach and pass the root at a time ⇒ critical section protected.

Build & Run:

```sh
make
make run
```

Or:

```sh
gcc -std=c11 -O2 -pthread src/*.c -o treelock_demo
./treelock_demo
```

Sample:

```stdout
In this Tree-Lock demo, we'll increment a shared_counter once per thread.
How many threads do you want to test? : 128
You want to test with 128 threads.
After executing 128 threads for 3443000 ns, the shared_counter = 128 !
```

## Correctness Properties (Draft Answers)

I will fill these in; brief sketches below.

### 1. Mutual Exclusion (Draft)

For a single Peterson lock, no two threads can reach the critical section. If we organize the Peterson locks such that there is one lock after the two prior, then the 2 previously winning threads need to compete for the next critical section … we guarantee no two threads can access the critical section after acquiring the locks from the leaf to root.

### 2. Freedom from Deadlock (Draft)

There is no cycle to unlock threads. A thread that enters the critical section unlocks immediately after, permitting any (but not all) of the previously blocked threads to proceed.

### 3. Freedom from Starvation (Draft)

Once a thread is queued in the lock, it will advance after the current thread unlocks even if another thread attempts to acquire the same lock.

### 4. Upper Bound on Intervening Critical Section Entries (Draft)

Let's consider what happens when the root-most lock is released. The next-most queued thread is guaranteed to go. For each lock in the tree, this is true. Threads then wait at most on one other thread for each level of the tree. That means the upper bound for the number of failed acquires is O(log(n)), where n is the number of locks in the tree (note num_locks = num_threads - 1).

However, I might be wrong because this considers just a single passage up the tree. Another reasonable upperbound is that every other thread goes first. That is, the upper bound is O(t) where t is the number of threads (ie. t-1 threads go before one of them does).
