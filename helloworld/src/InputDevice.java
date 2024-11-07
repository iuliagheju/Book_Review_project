import java.util.Random;
public class inputDevice {
    static Random rand = new Random();

    static String getType() {
        return "random";
    }

    public int nextInt() {
    return rand.nextInt(100);
    }

    static String getLine(){
        return "The quick brown fox jumps over the lazy dog";
    }

    public static int[] getNumbers(int N) {
        int[] arr = new int[N];
        for (int i = 0; i < N; i++) {
            arr[i] = rand.nextInt(100);

        }
        return arr;
    }
   }


