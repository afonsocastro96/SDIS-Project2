package feup.sdis;

import feup.sdis.commands.BackupCommand;
import feup.sdis.commands.DeleteCommand;
import feup.sdis.commands.RestoreCommand;
import feup.sdis.commands.VerificationCommand;
import feup.sdis.logger.Level;

import java.io.File;
import java.util.Scanner;

/**
 * Peer client interface
 */
public class PeerCLI implements Runnable {

    /**
     * Flag to check if it is to close the peer
     */
    private boolean exit = false;

    /**
     * Runnable of the peer
     */
    public void run() {
        final Scanner scanner = new Scanner(System.in);
        while (!exit) {
            System.out.print("$ ");
            handleInput(scanner.nextLine());
        }

        Peer.getInstance().getMonitor().stop();
    }

    /**
     * Handle the user input
     * @param input input from the user
     */
    private void handleInput(String input) {
        final String[] args = input.split(" ");
        final String command = args[0].toLowerCase();
        switch (command) {
            case "backup":
            case "b":
                if (args.length != 3) {
                    Node.getLogger().log(Level.CONSOLE, "USAGE: backup <file> <repDegree>");
                } else {
                    if (!BackupCommand.execute(new File(args[1]), Integer.parseInt(args[2])))
                        Node.getLogger().log(Level.CONSOLE, "Could not backup the file...");
                    else
                        Node.getLogger().log(Level.CONSOLE, "File was backed up successfully!");
                }
                break;
            case "delete":
            case "d":
                if (args.length != 2)
                    Node.getLogger().log(Level.CONSOLE, "USAGE: remove <file>");
                else {
                    if(!DeleteCommand.execute(new File(args[1])))
                        Node.getLogger().log(Level.CONSOLE, "Could not delete the file...");
                    else
                        Node.getLogger().log(Level.CONSOLE, "File was deleted successfully!");
                }
                break;
            case "restore":
            case "r":
                if (args.length != 2) {
                    Node.getLogger().log(Level.CONSOLE, "USAGE: restore <file>");
                } else {
                    if(!RestoreCommand.execute(new File(args[1])))
                        Node.getLogger().log(Level.CONSOLE, "Could not restore the file...");
                    else
                        Node.getLogger().log(Level.CONSOLE, "File was restored successfully!");
                }
                break;
            case "verify":
            case "v":
                if (args.length != 1)
                    Node.getLogger().log(Level.CONSOLE, "USAGE: verify");
                else {
                    if(!VerificationCommand.execute())
                        Node.getLogger().log(Level.CONSOLE, "Could not verify the chunks on the system...");
                    else
                        Node.getLogger().log(Level.CONSOLE, "Chunks were checked successfully!");
                    VerificationCommand.execute();
                }
                break;
            case "exit":
            case "e":
                exit = true;
                break;
            default:
                Node.getLogger().log(Level.CONSOLE, "Unknown command: " + args[0]);
                break;

        }
    }
}
