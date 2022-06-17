package commands;

import data.Climate;
import exсeptions.NoValidArgumentException;
import utility.CollectionManager;

import java.io.Serializable;

public class RemoveAnyByClimate implements Command<Boolean>, Serializable {
    private static final long serialVersionUID = 1;
    private Integer ownerId;
    private String name;
    private String description;
    private String usage;
    private CollectionManager collectionManager;

    public RemoveAnyByClimate(String name, String description, String usage, CollectionManager collectionManager) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.collectionManager = collectionManager;
    }

    @Override
    public Boolean execute(String[] argument) {
        return collectionManager.removeAnyByClimate(Climate.valueOf(argument[0].toUpperCase()),ownerId);
    }

    @Override
    public void setOwnerId(Integer id) {
        this.ownerId = id;
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

    @Override
    public boolean validateArgument(String[] arg) throws NoValidArgumentException {
        if (arg[0].length() == 0) throw new NoValidArgumentException("illegal arg");
        String s = arg[0].toUpperCase();
        try {
            Climate.valueOf(s);
        } catch (IllegalArgumentException e) {
            throw new NoValidArgumentException("no such climate");
        }
        return true;
    }
}
