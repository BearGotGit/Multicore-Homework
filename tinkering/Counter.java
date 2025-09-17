// class Counter {
//     private int count = 0;

//     public synchronized void increment() {
//         count++;
//     }

//     public int getCount() {
//         return count;
//     }
// }

class Counter {
    private int count = 0;

    public synchronized void increment() {
        count += 1;
    }

// Sync not required if getCount occurs after all interacting Threads join, but I'll put it here for best practice
    public synchronized int getCount() {
        return count;
    }
}