package commands;

import data.City;
import data.Climate;
import data.StandardOfLiving;
import exсeptions.NoValidArgumentException;
import utility.CollectionManager;

import java.io.Serializable;

public class Add implements Command<Boolean>, Serializable {
    private static final long serialVersionUID = 1;
    private Integer ownerId;
    private String name;
    private String description;
    private String usage;
    private CollectionManager collectionManager;
    public Add(String name, String description, String usage, CollectionManager collectionManager) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.collectionManager = collectionManager;
    }

    @Override
    public Boolean execute(String[] argument) {
        return collectionManager.addToCollect(createElement(argument));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getUsage() {
        return this.usage;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    @Override
    public void setOwnerId(Integer id) {
        this.ownerId = id;
    }

    @Override
    public boolean validateArgument(String[] arg) throws NoValidArgumentException {
        if (arg[0].length() != 0) throw new NoValidArgumentException("illegal arg");
        return true;
    }

    City createElement(String[] fields) {
        return collectionManager.createElement(fields, ownerId);
    }
}
