# Assignment 4 - General Multithreaded Non-blocking Reduction Operation

In this assignment, we used Java's `CompletableFuture` feature to delegate non-blocking tasks to a thread pool. We implemented a generalized reduction operation.

In my code, I test each reduction operation a thousand times each for different lists. At the end, I collect the results and report if they agreed with a semantically equivalent sequential form.

## Run code

```sh
make clean
make compile
make run
```
