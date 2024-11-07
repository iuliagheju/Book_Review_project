package org.example;
import java.util.Random;
public class InputDevice {
    static Random rand = new Random();

    static String getType() {
        return "random";
    }

    public int nextInt() {
        return rand.nextInt(100);
    }

    static String getLine(){
        return "";
    }

    public static int[] getNumbers(int N) {
        int[] arr = new int[N];
        for (int i = 0; i < N; i++) {
            arr[i] = rand.nextInt(100);

        }
        return arr;
    }

    public String nextLine() {
        System.out.println(getLine());
        return null;
    }
}


