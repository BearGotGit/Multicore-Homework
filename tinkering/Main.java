/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

/**
 *
 * @author berendgrandt
 */
// public class Main {

//     /**
//      * @param args the command line arguments
//      */
//     void main() {
//         // TODO code application logic here
//         System.out.println("Hello mother fuckers, got upgrade");

//     }

// }

import static java.io.IO.print;

class ChopStick {
        private boolean taken = false;

        public void ChopStick() {
        }

        public void Take() {
                taken = true;
        }

        public void Drop() {
                taken = false;
        }
}

class Philosopher {

        public ChopStick left;
        public ChopStick right;

        Philosopher(ChopStick left, ChopStick right) {

                this.left = left;
                this.right = right;

        }

        public void EatThinkAndTryNotToDeadlock() {
                print(left + " " + right);

                while (true) { 

                        try {
                            
                                print("Not fucking failing " + left + " " + right);
                                Thread.sleep(1000);

                        } catch (Exception e) {

                                print("Idk, som happ");
                        }

                }
        }


}


void main() {

        // Shared resources
        ChopStick cs0 = new ChopStick();
        ChopStick cs1 = new ChopStick();
        ChopStick cs2 = new ChopStick();
        ChopStick cs3 = new ChopStick();
        ChopStick cs4 = new ChopStick();

        Philosopher p0 = new Philosopher(cs0, cs1);
        Philosopher p1 = new Philosopher(cs1, cs2);
        Philosopher p2 = new Philosopher(cs2, cs3);
        Philosopher p3 = new Philosopher(cs3, cs4);
        Philosopher p4 = new Philosopher(cs4, cs0);

        // Philosopher threads
        p0.EatThinkAndTryNotToDeadlock();

}