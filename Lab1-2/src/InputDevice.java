import java.util.Random;

public class InputDevice {
    Random rand = new Random();

    static String getType() {
        return "random";
    }

    public int nextInt() {
        return rand.nextInt(100);
    }

    public void int[] getNumbers(int N) {
        int[] arr = new int[N];
        for (int i = 0; i < N; i++) {
            arr[i] = rand.nextInt(100);

        }
        return arr;
    }
}