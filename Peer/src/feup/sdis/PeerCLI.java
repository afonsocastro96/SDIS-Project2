package feup.sdis;

import java.util.Scanner;

/**
 * Created by Afonso on 22/05/2016.
 */
public class PeerCLI {
    private boolean exit = false;

    public void run(){
        while(true){
            String input = System.console().readLine();
            handleInput(input);
            if(exit)
                return;
        }
    }

    private void handleInput(String input) {
        String[] args = input.split(" ");
        String command = args[0].toLowerCase();
        switch(command){
            case "backup":
            case "bck":
                if(args.length != 3)
                    System.out.println("USAGE: backup <file> <repDegree>");
                else {}
                break;
            case "delete":
            case "dlt":
                if(args.length != 2)
                    System.out.println("USAGE: remove <file>");
                else {}
                break;
            case "restore":
            case "rst":
                if(args.length != 2)
                    System.out.println("USAGE: restore <file>");
                else {}
                break;
            case "recover":
            case "rcv":
                if(args.length != 1)
                    System.out.println("USAGE: recover");
                else {}
                break;
            case "validation":
            case "vld":
                if(args.length != 1)
                    System.out.println("USAGE: validation");
                else {}
                break;
            case "exit":
            case "e":
                exit = true;
                break;
            default:
                System.out.println("Unknown command: " + args[0]);
                break;

        }
    }
}
