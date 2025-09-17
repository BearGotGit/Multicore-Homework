# Homework 1 Attempt 2

Berend Grandt
89-971-1576

## Dining Philosophers

### Implementation of DP

My implementation of dining philosophers prevents deadlock by preventing cycle of dependence on chopsticks. For example, not every philosopher may pick up left chopstick first. Chopsticks are indexed and philosophers must pick up chopstick with lower index first. Since the philosophers are said to be sitting in a circle, one philosopher must pick up chopstick to his _right_ instead of his left (like the others). If you draw this out, it becomes clearer why it stops deadlock.

My implementation fairly schedules philosophers for a chopstick by preventing monopolization on chopsticks. I used a fair `java.util.concurrent.locks.ReentrantLock` which automatically schedules threads based on the order they try to secure the lock. This way, philosophers cannot secure resource over and over by chance.

### Running DP

```sh
# n = number of philosophers, int
# h = starting hunger for philosopher, int
make run dp <n> <h>
```

### Expected output for DP

To test the fairness of my approach, I tracked chopstick usage with an arraylist. I wanted to see who was using the chopstick and when. If one philosopher used the same chopstick over and over. I could be sure it was being monopolized. Over all the philosophers and all the chopsticks, I tracked the longest streak held by any philosopher. In practice, I've seen 2,3, and 4, but not much higher even when testing with hundreds of philosophers, so I think it's quite fair.

## Red Blue Threads

The rules required that we make symmetric locking mechanism for 2 thread types: red and blue. I implemented FIFO queue. When a thread of different color than active showed up, it's put in queue. Threads in queue have priority over threads that just showed up.

My implementation of RedBlueLock provides `lockRed()`, `lockBlue()`, and `unlock()` methods. Threads that make calls to `lockRed()` are called red threads; threads that call to `lockBlue()` are blue. We track the threads that are waiting. `unlock()` will release lock for any thread type.

- If no active threads, either blue or red is allowed
- If active threads and no threads wait, same color is allowed
- If active threads but threads wait (must be other color), thread waits

All threads wait in the same queue, even if they're different colors, so a common `unlock()` method is provided.

- Not last thread, decrement number of active
- Last thread, signals all threads, but rules set up such that first in line (different color) goes

### Running RB Threads

```sh
#
make run rb <n> <t>
```

### Expected output for RB Threads

To test both the disjoint nature of red-blue access to the shared resource, I tested on a shared array. Every element of the array would be accessed by different threads of the same color, but the same element could also be accessed by a thread of different color. To illustrate how my code doesn't lead to race conditions, I kept a running tally of the number of increments by one color and the number of decrements from the other. The threads were set up to incerement and decrement the same number of times, so every element of the array should be 0. In fact it is.

To test the fairness of my approach, I tracked the usage across a single element, just like I tracked the chopsticks during DP. Just as with the DPs, I expected usage to be mixed well. That is, no blue or red threads monopolized for a time. I reported the max streak again, over all indices of the array. In testing, I get varying values for the max streak. For example, if I set the number of iterations per thread to 500 for 20 threads, I still obtained a max streak of 14, which means the threads trade resources rather frequently still.
