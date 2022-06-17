package database;

import data.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.concurrent.PriorityBlockingQueue;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager() {
        Path path = Paths.get("config.properties");
        try {
            FileInputStream fileInputStream = new FileInputStream(path.toFile());
            Properties properties = new Properties();
            properties.load(fileInputStream);
            if (properties.getProperty("link") == null || properties.getProperty("pass") == null) {
                System.out.println("no linl or pass properties in config file");
                System.exit(1);
            }
            this.connection = DriverManager.getConnection(properties.getProperty("link"),"s335181",properties.getProperty("pass"));
        } catch (IOException e) {
            System.out.println("config.properties not found");
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public Connection getConnection(){
        return connection;
    }

    public int getNextOwnerId(){
        ResultSet set = null;
        try {
            set = getConnection().createStatement().executeQuery("select nextval('ownerid_generator')");
            set.next();
            return set.getInt("nextval");
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getNextId(){
        ResultSet set = null;
        try {
            set = getConnection().createStatement().executeQuery("select nextval('id_generator')");
            set.next();
            return set.getInt("nextval");
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setSequences(){
        try {
            getConnection().createStatement().execute("drop sequence if exists id_generator");
            ResultSet set = getConnection().createStatement().executeQuery("select MAX(id) from collection");
            set.next();
            int id = set.getInt("max") + 1;
            getConnection().createStatement().execute("create sequence if not exists id_generator start with " + id + " increment by 1 minvalue 0");
            getConnection().createStatement().execute("drop sequence if exists ownerid_generator");
            set = getConnection().createStatement().executeQuery("select MAX(id) from users");
            set.next();
            id = set.getInt("max") + 1;
            getConnection().createStatement().execute("create sequence if not exists ownerid_generator start with " + id + " increment by 1 minvalue 0");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PriorityBlockingQueue<City> getCollectionFromBase(){
        City tempCity;
        PriorityBlockingQueue<City> priorityQueue = new PriorityBlockingQueue<>();
        Connection connection = getConnection();
        String query = "select * from collection";
        try {
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()){
                tempCity = new City(set.getInt("ownerid"),set.getInt("id"),set.getString("name"),
                        new Coordinates(set.getInt("x"),set.getLong("y")),
                        LocalDateTime.parse(set.getString("creationdate")),
                        set.getInt("area"),
                        set.getInt("population"),
                        null,null,null,null,null );
                if (set.getString("metersabovesealevel") != null) tempCity.setMetersAboveSeaLevel(set.getDouble("metersabovesealevel"));
                if (set.getString("telephonecode") != null) tempCity.setTelephoneCode(set.getInt("telephonecode"));
                if (set.getString("climate") != null) tempCity.setClimate(Climate.valueOf(set.getString("climate").toUpperCase()));
                if (set.getString("standardofliving") != null) tempCity.setStandardOfLiving(StandardOfLiving.valueOf(set.getString("standardofliving").toUpperCase()));
                if (set.getString("governorname") != null) tempCity.setGovernor(new Human(set.getString("governorname")));
                priorityQueue.add(tempCity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return priorityQueue;
    }

    public boolean parseToBase(City city){
        String s = "insert into collection values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement statement = getConnection().prepareStatement(s);
            statement.setInt(1,city.getOwnerId());
            statement.setInt(2,city.getId());
            statement.setString(3,city.getName());
            statement.setInt(4,city.getCoordinates().getX());
            statement.setDouble(5,city.getCoordinates().getY());
            statement.setString(6,city.getDateString());
            statement.setInt(7,city.getArea());
            statement.setInt(8,city.getPopulation());
            if (city.getMetersAboveSeaLevel() == null) statement.setNull(9,Types.NULL);
            else statement.setString(9,city.getMetersAboveSeaLevel().toString());
            if (city.getTelephoneCode() == null) statement.setNull(10,Types.NULL);
            else statement.setInt(10,city.getTelephoneCode());
            if(city.getClimate() == null) statement.setNull(11,Types.NULL);
            else statement.setString(11,city.getClimate().toString());
            if(city.getStandardOfLiving() == null) statement.setNull(12,Types.NULL);
            else statement.setString(12,city.getStandardOfLiving().toString());
            if (city.getGovernor() == null) statement.setString(13,null);
            else statement.setString(13,city.getGovernor().getName());
            statement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}