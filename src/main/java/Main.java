
import Apps.Client;
import Apps.Server;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Locale;


public class Main {
    public static void main(String[] args) {
        if (args.length == 3 && args[0] != null && args[1] != null && args[2] != null) {
           if(args[0].toLowerCase(Locale.ROOT).equals("client")){
               Client client = new Client();
               try {
                   client.run(args[1].toLowerCase(Locale.ROOT),Integer.parseInt(args[2]));
                   return;
               } catch (IOException e) {
                   System.out.println("unexpected error");
               } catch (JAXBException e) {
                   System.out.println("unexpected error");
               }
           }

           if (args[0].toLowerCase(Locale.ROOT).equals("server")){
               Server server = new Server();
               server.run(args[1], Integer.parseInt(args[2]));
               return;
           }
            System.out.println("application not specified");
            System.out.println("it must be java -jar lab6.jar client/server hostName port");
            System.out.println("for example: java -jar lab6.jar client helios.cs.ifmo.ru 228");
            System.out.println("or  java -jar lab6.jar server helios.cs.ifmo.ru 228");
            System.exit(1);
        }
        else {
            System.out.println("application not specified");
            System.out.println("it must be java -jar lab6.jar client/server hostName port");
            System.out.println("for example: java -jar lab6.jar client helios.cs.ifmo.ru 228");
            System.out.println("or  java -jar lab6.jar server helios.cs.ifmo.ru 228");
            System.exit(1);
        }
    }
}