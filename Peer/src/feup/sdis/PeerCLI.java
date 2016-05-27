package feup.sdis;

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
            case "b":
                if(args.length != 3)
                    System.out.println("USAGE: backup <file> <repDegree>");
                else {}
                break;
            case "delete":
            case "dlt":
            case "d":
                if(args.length != 2)
                    System.out.println("USAGE: remove <file>");
                else {}
                break;
            case "restore":
            case "rst":
            case "r":
                if(args.length != 2)
                    System.out.println("USAGE: restore <file>");
                else {}
                break;
            case "start":
            case "str":
            case "s":
                if(args.length != 1)
                    System.out.println("USAGE: start");
                else {}
                break;
            case "verification":
            case "vrf":
            case "v":
                if(args.length != 1)
                    System.out.println("USAGE: verification");
                else {}
                break;
            case "exit":
            case "ext":
            case "e":
                exit = true;
                break;
            default:
                System.out.println("Unknown command: " + args[0]);
                break;

        }
    }

    private void backup(){

    }
}
