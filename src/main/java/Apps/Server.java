package Apps;

import API.ServerRequest;
import commands.*;
import data.Climate;
import data.StandardOfLiving;
import database.BasedCollectionManager;
import database.DatabaseManager;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private Command exit;
    private Command save;
    private BufferedReader console;
    private DatagramChannel channel;
    private Logger logger;
    private DatabaseManager databaseManager;
    private SocketAddress clientAddress;
    private ByteBuffer bufferFromClient;
    private CollectionManager collectionManager;
    private FileManager fileManager;
    public Server(){
        this.exit = new Exit("exit","none","none");
        this.logger = LogManager.getLogger();
        this.console = new BufferedReader(new InputStreamReader(System.in));
        this.databaseManager = new DatabaseManager();
        this.bufferFromClient = ByteBuffer.allocate(8192);
    }

    public void prepareChannel(String hostName, int port){
        try {
            channel = DatagramChannel.open();
            channel.bind(new InetSocketAddress(InetAddress.getByName(hostName),port));
            channel.configureBlocking(false);
        } catch (UnknownHostException e) {
            System.out.println("no such host");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run(String hostName,int port)  {
        try {
            prepareChannel(hostName,port);
            SerializationHelper serializer = new SerializationHelper();
            logger.info("current envVar = " + System.getenv("LABA"));
            this.fileManager = new FileManager("LABA");
            this.collectionManager = new BasedCollectionManager(fileManager,databaseManager);
            this.save = new Save("save","none","none",collectionManager);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
            ServerCommandManager commandManager = new ServerCommandManager(fileManager,writer,channel,databaseManager);
            collectionManager.setCityCollection(databaseManager.getCollectionFromBase());
            databaseManager.setSequences();
            addCommands(commandManager,collectionManager,fileManager);
            commandManager.createClassMap();
            logger.info("created all command instances");
            ServerRequest request;
            logger.info("server is ready");
            ExecutorService executorService = Executors.newFixedThreadPool(7);
            new Thread(() -> {
                while (true) waitInput();
            }).start();

            while (true){
                clientAddress = channel.receive(bufferFromClient);
                if (clientAddress != null){
                    logger.info("received a request from: " + clientAddress);
                    try {
                        request = (ServerRequest) serializer.deSerialization(bufferFromClient);
                        logger.info("starting to do: " + request);

                        ServerRequest finalRequest = request;
                        SocketAddress finalClientAddress = clientAddress;
                        executorService.execute(() ->{
                            try {
                                commandManager.setClientAddress(finalClientAddress);
                                commandManager.setRequest(finalRequest);
                                commandManager.setArgument(finalRequest.getArgument());
                                commandManager.handle(finalRequest.getCommand());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        bufferFromClient.clear();
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

    public void waitInput() {
        String input;
        try {
            if (System.in.available() > 0){
                input = console.readLine().toLowerCase(Locale.ROOT);
                if (input.equals("save")){
//                    save.execute(null);
                    logger.info("save executed");
                }
                if (input.equals("exit")){
    //                        save.execute(null);
                    logger.info("saved, shutdown");
                    exit.execute(null);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        commandManager.addCommand(new Show("show","print all elements of collection","can receive an argument\n - show my to see only yours elements",
                collectionManager));
        commandManager.addCommand(new Update("update","update value of element whose id is equal to specified","receive an integer value as argument",
                collectionManager));
    }
}




