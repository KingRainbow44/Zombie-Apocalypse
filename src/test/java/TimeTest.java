import lol.magix.zombiesv2.managers.TimeManager;

import java.util.Scanner;

public class TimeTest {
    public static void main(String[] args) {
        var timeManager = new TimeManager();

        var consoleScanner = new Scanner(System.in);
        while (true) {
            var input = consoleScanner.nextLine();
            if (input.equals("exit")) break;

            try {
                var inputTime = Integer.parseInt(input);
                timeManager.initialize(1, inputTime);
                System.out.println("Current Minecraft Time: " + timeManager.internalTimeToMinecraftTime() + " (" + timeManager.getCurrentTime() + ")");
            } catch (NumberFormatException e) {
                System.out.println("Only a integer or the word 'exit' is accepted.");
            }
        }
    }
}
