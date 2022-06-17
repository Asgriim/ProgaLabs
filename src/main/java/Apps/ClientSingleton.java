package Apps;

import API.ServerRequest;
import API.ServerResponse;
import data.City;
import exсeptions.FileIssueException;
import readers.CityReader;
import utility.FileManager;
import utility.SerializationHelper;
import utility.client.ClientCommandManager;
import utility.client.ClientValidator;
import utility.client.WaitResponse;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

public class ClientSingleton {
    public static ClientSingleton client;
    private PriorityBlockingQueue<City> cityCollection;
    private String serverName;
    private int port;
    private DatagramChannel channel;
    private SocketAddress server;
    private ByteBuffer bufferToServer;
    private ByteBuffer fromServer;
    private SerializationHelper serializator;
    private WaitResponse waitResponse;
    private int ownerId;
    private Map<String, String> commandMap;
    private ClientCommandManager clientCommandManager;
    private FileManager fileManager;
    private Locale currLocale = Locale.getDefault();
    public static ClientSingleton getInstance() {
        return client;
    }

    private ClientSingleton(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
        try {
            this.channel = DatagramChannel.open();
            this.channel.configureBlocking(false);
            this.server = new InetSocketAddress(InetAddress.getByName(serverName),port);
            this.bufferToServer = ByteBuffer.allocate(100000);
            this.fromServer = ByteBuffer.allocate(1000000);
            this.serializator = new SerializationHelper();
            this.waitResponse = new WaitResponse();
            try {
                this.fileManager = new FileManager("LABA");
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void clientBuilder(String serverName, int port){
        if (client == null){
            client = new ClientSingleton(serverName,port);
        }
    }

    public boolean requestCollectionFromServer(){
        ServerRequest serverRequest = new ServerRequest();
        serverRequest.setCommand("collection request");
        ServerResponse serverResponse = sendRequest(serverRequest);
        if (serverResponse == null) return false;
        setCityCollection(serverResponse.getCityCollection());
        return true;

    }

    public ServerRequest getServerRequest(){
        ServerRequest serverRequest = new ServerRequest();
        serverRequest.setOwnerId(getOwnerId());
        return serverRequest;
    }

    public ServerResponse sendRequest(ServerRequest request){
        try {
            fromServer.clear();
            bufferToServer.put(serializator.serialize(request));
            bufferToServer.flip();
            channel.send(bufferToServer,server);
            bufferToServer.clear();
            if(!waitResponse.waitReceive(channel,fromServer,5)){
                return null;
            }
            return (ServerResponse) serializator.deSerialization(fromServer);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void execScript(File file){
        try {
            clientCommandManager.execScript(file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeCoommandManager(){
        this.clientCommandManager = new ClientCommandManager(fileManager,new BufferedWriter(new OutputStreamWriter(System.out)),
                new CityReader(new BufferedReader(new InputStreamReader(System.in)),new OutputStreamWriter(System.out)),channel,server);
        clientCommandManager.setCommandClassMap(commandMap);
        clientCommandManager.setValidator(new ClientValidator(commandMap,fileManager));
        clientCommandManager.setOwnerId(ownerId);
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setCommandMap(Map<String, String> commandMap) {
        this.commandMap = commandMap;
    }

    public PriorityBlockingQueue<City> getCityCollection() {
        return cityCollection;
    }

    public void setCityCollection(PriorityBlockingQueue<City> cityCollection) {
        this.cityCollection = cityCollection;
    }

    public String getHash(String s){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-224");
            byte[] bytes = digest.digest(s.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInteger = new BigInteger(1,bytes);
            String hash = bigInteger.toString(16);
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Locale getCurrLocale() {
        return currLocale;
    }

    public void setCurrLocale(Locale currLocale) {
        this.currLocale = currLocale;
    }
}
