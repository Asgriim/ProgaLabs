package Apps;

import API.ServerRequest;
import commands.*;
import data.Climate;
import data.StandardOfLiving;
import exсeptions.FileIssueException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.CollectionManager;
import utility.FileManager;
import utility.SerializationHelper;
import utility.server.ServerCommandManager;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.Locale;

public class Server {
    private Command exit;
    private Command save;
    public Server(){
        this.exit = new Exit("exit","none","none");
    }

    public void run(String hostName,int port)  {
        Logger logger = LogManager.getLogger();
        try {
            String input = "";
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            DatagramChannel channel = DatagramChannel.open();
            channel.bind(new InetSocketAddress(InetAddress.getByName(hostName), port));
            channel.configureBlocking(false);
            SocketAddress clientAddress = null;
            SerializationHelper serializer = new SerializationHelper();
            logger.info("current envVar = " + System.getenv("LABA"));
            FileManager fileManager = new FileManager("LABA");
            CollectionManager collectionManager = new CollectionManager(fileManager);
            this.save = new Save("save","none","none",collectionManager);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
            ServerCommandManager commandManager = new ServerCommandManager(fileManager,writer,channel);
            try {
                collectionManager.setCityCollection(fileManager.parseFromFile());
            } catch (FileIssueException | FileNotFoundException | JAXBException e) {
                logger.error("error at 49 line");
                logger.error(e.getMessage());
                System.exit(1);
            }
            if (!collectionManager.checkCollection()) {
                logger.error("error ot 55 line");
                logger.error("wrong xml data\nenter fields in file properly");
                System.exit(1);
            }
            addCommands(commandManager,collectionManager,fileManager);
            commandManager.createClassMap();
            logger.info("created all command instances");
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            ServerRequest request;
            logger.info("server is ready");
            while (true){
                if (System.in.available() > 0){
                    input = console.readLine().toLowerCase(Locale.ROOT);
                    if (input.equals("save")){
                        save.execute(null);
                        logger.info("save executed");
                    }
                    if (input.equals("exit")){
                        save.execute(null);
                        logger.info("saved, shutdown");
                        exit.execute(null);
                    }
                }
                clientAddress = channel.receive(buffer);
                if (clientAddress != null){
                    logger.info("received a request from: " + clientAddress);
                    try {
                        request = (ServerRequest) serializer.deSerialization(buffer);
                        logger.info("starting to do: " + request);
                        commandManager.setClientAddress(clientAddress);
                        commandManager.setRequest(request);
                        commandManager.setArgument(request.getArgument());
                        commandManager.handle(request.getCommand());
                        buffer.clear();
                    } catch (ClassNotFoundException e) {
                        logger.error("serialization error");
                        logger.error(e.getMessage());
                    }
                }
            }
        } catch (UnknownHostException e) {
            logger.error("no such host or port is already using");
            logger.error(e.getMessage());
        } catch (JAXBException e) {
            logger.error("Xml file error\n" +
                    "wrong xml data");
            logger.error(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public void addCommands(ServerCommandManager commandManager,CollectionManager collectionManager,FileManager fileManager){
        commandManager.addCommand(new Add("add","insert new element in collection","doesn't receive an argument",collectionManager));
        commandManager.addCommand(new AddIfMax("add_if_max","insert new element in collection if it's greater than max","doesn't receive an argument",
                collectionManager));
        commandManager.addCommand(new Clear("clear","clears collection","doesn't receive an argument",collectionManager));
        commandManager.addCommand(new CountBySOL("count_by_standard_of_living","print the number of elements is equal to the specified value",
                "receive standard of living const as argument: " + Arrays.toString(StandardOfLiving.values()),collectionManager));
        commandManager.addCommand(new ExecuteScript("execute_script","execute script from a file","receives a file name as argument,\n\tfile mast be readable and in current directory",
                null,fileManager));
        commandManager.addCommand(new Exit("exit","exit program without saving","doesn't receive an argument"));
        commandManager.addCommand(new Help("help","print a list of commands or description of specified one","can receive an argument as command name\n\t for example: help info",
                commandManager));
        commandManager.addCommand(new Info("info","print information about collection","doesn't receive an argument",
                collectionManager));
        commandManager.addCommand(new PrintUniqueSOL("print_unique_standard_of_living","print unique standard of living values","doesn't receive an argument",
                collectionManager));
        commandManager.addCommand(new RemoveAnyByClimate("remove_any_by_climate","remove firs element that equals specified",
                "receive climate const as argument: " + Arrays.toString(Climate.values()),collectionManager));
        commandManager.addCommand(new RemoveById("remove_by_id","remove element by id","receives integer value as argument",
                collectionManager));
        commandManager.addCommand(new RemoveFirst("remove_first","remove first element","doesn't receive an argument",
                collectionManager));
        commandManager.addCommand(new RemoveHead("remove_head","print and delete first element","doesn't receive an argument",
                collectionManager));
        commandManager.addCommand(new Show("show","print all elements of collection","doesn't receive an argument",
                collectionManager));
        commandManager.addCommand(new Update("update","update value of element whose id is equal to specified","receive an integer value as argument",
                collectionManager));
    }
}




