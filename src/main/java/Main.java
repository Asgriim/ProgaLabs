import Apps.*;
import data.*;
import database.DatabaseManager;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Locale;


public class Main {
    public static void main(String[] args) {
//        test();
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

    public static void test2(){
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-224");
//            byte[] bytes = digest.digest("4d0911d6fa124aac4c18e9bfed86ca34a862ce72f2c6412e5fbaf1471".getBytes(StandardCharsets.UTF_8));
//            BigInteger bigInteger = new BigInteger(1,bytes);
//            String hash = bigInteger.toString(16);
//            System.out.println(hash);
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }

    }

    public static void test(){
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:1111/studs","s335181","oau795");

            Statement statement = connection.createStatement();
            String prekol = "create table collection(" +
                    "    ownerId bigint not null check ( ownerId > 0 )," +
                    "    id bigint not null check ( id > 0 )," +
                    "    name Text not null," +
                    "    x bigint not null check ( x > -251 )," +
                    "    y float8 not null," +
                    "    creationDate text not null," +
                    "    area bigint not null check ( area > 0 )," +
                    "    population bigint not null check ( population > 0 )," +
                    "    metersAboveSeaLevel float8 nullable ," +
                    "    telephoneCode bigint nullable check ( telephoneCode > 0 and telephoneCode < 100000)," +
                    "    climate Text nullable ," +
                    "    standardOfLiving Text nullable ," +
                    "    governorName Text nullable" +
                    " )";

//            System.out.println(prekol);
            String s = "select * from collection";

            City city = new City(2,1,"name",new Coordinates(1,2), LocalDateTime.now(),
                    123,321,2232.0,null, Climate.MEDITERRANIAN, StandardOfLiving.HIGH,
                    new Human("beb"));

            s = "select id from collection where id = 12";
//            Statement statementp = connection.createStatement();
//            statementp.setInt(1,12);
//            ResultSet set = statementp.executeQuery(s);
//            while (set.next()){
//                System.out.println("lox ti");
//            }

            DatabaseManager databaseManager = new DatabaseManager();
            Statement statement1 = databaseManager.getConnection().createStatement();
            ResultSet set = statement1.executeQuery("select * from users where login = 'tes'");
            while (set.next()){
                System.out.println("1");
            }
//            databaseManager.setSequences();
//            CollectionManager collectionMan
//            ager = new BasedCollectionManager(null,databaseManager);
//            collectionManager.setCityCollection(databaseManager.getCollectionFromBase());
//            collectionManager.removeAnyByClimate(Climate.MEDITERRANIAN,1);
//            System.out.println(new DatabaseManager().parseToBase(city));
//            System.out.println(new DatabaseManager().getCollectionFromBase());
//            boolean set = statement.execute(prekol);
//            ResultSet set = statement.executeQuery(s);
//            while (set.next()){
//                System.out.println(set.getDouble("metersabovesealevel"));
//            }
//            System.out.println(set.getString(1));

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}