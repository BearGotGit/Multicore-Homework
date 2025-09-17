
import java.util.ArrayList;


// public class SyncDemo {
//     public static void main(String[] args) throws InterruptedException {
//         Counter counter = new Counter();

//         Thread t1 = new Thread(() -> {
//             for (int i = 0; i < 1000; i++) counter.increment();
//         });

//         Thread t2 = new Thread(() -> {
//             for (int i = 0; i < 1000; i++) counter.increment();
//         });

//         t1.start();
//         t2.start();
//         t1.join();
//         t2.join();

//         System.out.println("Final count: " + counter.getCount());
//     }
// }

public class SyncDemo {
    public static void main(String[] args) {
        
        Counter ct = new Counter();
        ArrayList<Thread> threads = new ArrayList<>();
        final int n = 5;

        for (int i = 0; i < n; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    ct.increment();
                }
            });

            threads.add(t);
        }
 
        threads.forEach(t -> t.start());
        threads.forEach(t -> {
             try {
                t.join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        });

        System.out.println("Final Count: " + ct.getCount());
    }
}