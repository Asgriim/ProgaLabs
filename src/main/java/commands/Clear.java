package commands;

import exсeptions.NoValidArgumentException;
import utility.CollectionManager;

import java.io.Serializable;

public class Clear implements Command<Boolean>, Serializable {
    private static final long serialVersionUID = 1;
    private String name;
    private String description;
    private String usage;
    private CollectionManager collectionManager;

    public Clear(String name, String description, String usage, CollectionManager collectionManager) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.collectionManager = collectionManager;
    }

    @Override
    public Boolean execute(String[] argument) {
        collectionManager.clearCollection();
        return true;
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
        if (arg[0].length() != 0) throw new NoValidArgumentException("illegal arg");
        return true;
    }
}
