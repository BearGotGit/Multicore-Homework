class Buffer {
    private int[] buffer;
    private int capacity;
    private int count; // current number of items in buffer
    private int in;    // index for producer
    private int out;   // index for consumer

    public Buffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new int[capacity];
        this.count = 0;
        this.in = 0;
        this.out = 0;
    }

    public synchronized void produce(int item) throws InterruptedException {
        while (count == capacity) { // Buffer is full, producer waits
            wait();
        }
        buffer[in] = item;
        in = (in + 1) % capacity;
        count++;
        System.out.println("Produced: " + item);
        notifyAll(); // Notify waiting consumers
    }

    public synchronized int consume() throws InterruptedException {
        while (count == 0) { // Buffer is empty, consumer waits
            wait();
        }
        int item = buffer[out];
        out = (out + 1) % capacity;
        count--;
        System.out.println("Consumed: " + item);
        notifyAll(); // Notify waiting producers
        return item;
    }
}

class Producer implements Runnable {
    private Buffer buffer;

    public Producer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                buffer.produce(i);
                Thread.sleep(100); // Simulate work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class Consumer implements Runnable {
    private Buffer buffer;

    public Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                buffer.consume();
                Thread.sleep(150); // Simulate work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

public class ProducerConsumerDemo {
    public static void main(String[] args) {
        Buffer buffer = new Buffer(5);
        Thread producerThread = new Thread(new Producer(buffer));
        Thread consumerThread = new Thread(new Consumer(buffer));

        producerThread.start();
        consumerThread.start();
    }
}
