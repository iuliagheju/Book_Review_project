public class Main {
    public static void main(String[] args) {
        inputDevice InputDevice = new inputDevice();
        OutputDevice outputdevice = new OutputDevice();
        Application app = new Application(InputDevice, outputdevice);
    }
}