import java.util.Arrays;
public class Application {
    private final InputDevice inputdevice;
    private final OutputDevice outputdevice;

    public Application (InputDevice input, OutputDevice output) {
        this.inputdevice = input;
        this.outputdevice = output;
    }

    public void run() {
        this.outputdevice.writeMessage("Application started");
        this.outputdevice.writeMessage("Today’s lucky numbers are: " + inputdevice.nextInt() + " and " + inputdevice.nextInt());
        playGame();
    }

    private void playGame() {
        int player1 = 0;
        int player2 = 0;

        while (player1 < 5 && player2 < 5) {
            int player1win = inputdevice.nextInt();
            int player2win = inputdevice.nextInt();

            outputdevice.writeMessage("Player 1 chose: " + player1win);
            outputdevice.writeMessage("Player 2 chose: " + player2win);

            if (player1win == player2win) {
                player1 += 2;
                player2 += 2;
                outputdevice.writeMessage("Both players chose the same number. Each gets 2 points.");
            } else {
                int highestnumber = Math.max(player1win, player2win);
                int lowestnumber = Math.min(player1win, player2win);

                if (highestnumber % lowestnumber == 0 && lowestnumber >= 10) {
                    if (highestnumber == player1win) {
                        player2++;
                        outputdevice.writeMessage("Player 2 wins this round by special rule.");
                    } else {
                        player1++;
                        outputdevice.writeMessage("Player 1 wins this round by special rule.");
                    }
                } else {
                    if (highestnumber == player1win) {
                        player1++;
                        outputdevice.writeMessage("Player 1 wins this round.");
                    } else {
                        player2++;
                        outputdevice.writeMessage("Player 2 wins this round.");
                    }
                }
            }
            outputdevice.writeMessage("Score: Player 1 - " + player1 + ", Player 2 - " + player2);
        }
        if (player1 >= 5) {
            outputdevice.writeMessage("Player 1 wins the game!");
        } else {
            outputdevice.writeMessage("Player 2 wins the game!");
        }
    }
}
public void sortNumbers(int[] arr) {
    Arrays.sort(arr);
}

public void randomArraySort(){
    int[] arr = InputDevice.getNumbers(N);
    sortNumbers(arr);

}


