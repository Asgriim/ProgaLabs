package database;

import data.*;
import utility.CollectionManager;
import utility.FileManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.PriorityQueue;

public class BasedCollectionManager extends CollectionManager {
    private DatabaseManager databaseManager;

    /**
     * @param fileManager
     */
    public BasedCollectionManager(FileManager fileManager, DatabaseManager databaseManager) {
        super(fileManager);
        this.databaseManager = databaseManager;
    }

    @Override
    public boolean addToCollect(City city) {
        if (addToBase(city)) {
            getCollection().add(city);
            return true;
        }
        return false;
    }

    @Override
    public void clearCollection(int ownerId) {

        String s = "delete from collection where ownerid = ?";
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(s);
            statement.setInt(1, ownerId);
            statement.execute();
            Iterator iterator = getIterator();
            City tempCity;
            while (iterator.hasNext()) {
                tempCity = (City) iterator.next();
                if (tempCity.getOwnerId().equals(ownerId)) iterator.remove();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean removeById(int id, Integer ownerId){
        String s = "select id from collection where id = ? and ownerid = ?";
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(s);
            statement.setInt(1, id);
            statement.setInt(2,ownerId);
            ResultSet set = statement.executeQuery();
            if (!set.next()) return false;
            return removeById(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean removeById(int id) {
        String s = "select id from collection where id = ?";
        try {
//            boolean flag = false;
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(s);
            statement.setInt(1, id);
            ResultSet set = statement.executeQuery();
            if (!set.next()) return false;
//            statement = databaseManager.getConnection().prepareStatement(s)
            databaseManager.getConnection().createStatement().execute("delete from collection where id = " + id);
            return super.removeById(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean removeAnyByClimate(Climate climate, Integer ownerId) {
        City temp =  getCollection().stream().filter(x -> x.getOwnerId().equals(ownerId)).filter(x -> x.getClimate() != null && x.getClimate().equals(climate)).findFirst().orElse(null);
        if (temp == null) return false;
        try {
            databaseManager.getConnection().createStatement().execute("delete from collection where id = " + temp.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return getCollection().remove(temp);
    }

    @Override
    public boolean removeFirst(Integer ownerId) {
        City temp = getCollection().stream().filter(x -> x.getOwnerId().equals(ownerId)).findFirst().orElse(null);
        if (temp == null) return false;
        return removeById(temp.getId());
    }

    @Override
    public City removeHead(Integer ownerId) {
        City temp = getCollection().stream().filter(x -> x.getOwnerId().equals(ownerId)).findFirst().orElse(null);
        if (temp == null) return null;
        City t = temp;
        removeById(temp.getId());

        return t;
    }

    @Override
    public City createElement(String[] argument, Integer ownerId) {
        City tempCity = new City(ownerId,databaseManager.getNextId(),null,new Coordinates(0,0),
                LocalDateTime.now(),null,null,null,null,null,null,
                new Human(null));
        tempCity.setName(argument[0]);
        tempCity.getCoordinates().setX(Integer.valueOf(argument[1]));
        tempCity.getCoordinates().setY(Double.valueOf(argument[2]));
        tempCity.setArea(Integer.valueOf(argument[3]));
        tempCity.setPopulation(Integer.valueOf(argument[4]));
        if (argument[5].equals("")){
            tempCity.setMetersAboveSeaLevel(null);
        }
        else {
            tempCity.setMetersAboveSeaLevel(Double.valueOf(argument[5]));
        }
        if (argument[6].equals("")){
            tempCity.setTelephoneCode(null);
        }
        else {
            tempCity.setTelephoneCode(Integer.valueOf(argument[6]));
        }
        if (argument[7].equals("")){
            tempCity.setClimate(null);
        }
        else {
            tempCity.setClimate(Climate.valueOf(argument[7]));
        }
        if (argument[8].equals("")){
            tempCity.setStandardOfLiving(null);
        }
        else {
            tempCity.setStandardOfLiving(StandardOfLiving.valueOf(argument[8]));
        }
        if (argument[9].equals("")){
            tempCity.setGovernor(null);;
        }
        else {
            tempCity.getGovernor().setName(argument[9]);
        }
        tempCity.setOwnerId(ownerId);
        return tempCity;
    }

    public boolean addToBase(City city){
        return databaseManager.parseToBase(city);
    }

    @Override
    public boolean update(String[] argument, Integer id, Integer ownerId) {
        City tempCity = getById(id);
        if (tempCity == null) return false;
        if (!tempCity.getOwnerId().equals(ownerId)) return false;
        PreparedStatement statement;
        try {
             statement = databaseManager.getConnection().prepareStatement("update collection set " +
                     "name = ?, " +
                    "x = ?, " +
                    "y = ?," +
                    "area = ?, " +
                    "population = ?, " +
                    "metersabovesealevel = ?, " +
                     "telephonecode = ?," +
                     "climate = ?, " +
                     "standardofliving = ?," +
                     "governorname = ?" +
                     "where id = ?");
            tempCity.setName(argument[0]);
            statement.setString(1,argument[0]);
            tempCity.getCoordinates().setX(Integer.valueOf(argument[1]));
            statement.setInt(2,Integer.valueOf(argument[1]));
            tempCity.getCoordinates().setY(Double.valueOf(argument[2]));
            statement.setDouble(3,Double.valueOf(argument[2]));
            tempCity.setArea(Integer.valueOf(argument[3]));
            statement.setInt(4,Integer.valueOf(argument[3]));
            tempCity.setPopulation(Integer.valueOf(argument[4]));
            statement.setInt(5,Integer.valueOf(argument[4]));
            if (argument[5].equals("")){
                tempCity.setMetersAboveSeaLevel(null);
                statement.setNull(6, Types.NULL);
            }
            else {
                tempCity.setMetersAboveSeaLevel(Double.valueOf(argument[5]));
                statement.setString(6,argument[5]);
            }
            if (argument[6].equals("")){
                tempCity.setTelephoneCode(null);
                statement.setNull(7, Types.NULL);
            }
            else {
                tempCity.setTelephoneCode(Integer.valueOf(argument[6]));
                statement.setInt(7,Integer.valueOf(argument[6]));
            }
            if (argument[7].equals("")){
                tempCity.setClimate(null);
                statement.setNull(8, Types.NULL);
            }
            else {
                tempCity.setClimate(Climate.valueOf(argument[7]));
                statement.setString(8,argument[7]);
            }
            if (argument[8].equals("")){
                tempCity.setStandardOfLiving(null);
                statement.setNull(9, Types.NULL);
            }
            else {
                tempCity.setStandardOfLiving(StandardOfLiving.valueOf(argument[8]));
                statement.setString(9,argument[8]);
            }
            if (argument[9].equals("")){
                tempCity.setGovernor(null);
                statement.setNull(10, Types.NULL);
            }
            else {
                if (tempCity.getGovernor() == null){
                    tempCity.setGovernor(new Human(argument[9]));
                }
                else {
                    tempCity.getGovernor().setName(argument[9]);
                }
                statement.setString(10,argument[9]);
            }
            statement.setInt(11,id);
            statement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


    }
}
