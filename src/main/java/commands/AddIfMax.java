package commands;

import utility.CollectionManager;

public class AddIfMax extends Add{
    private static final long serialVersionUID = 2;
    public AddIfMax(String name, String description, String usage, CollectionManager collectionManager) {
        super(name, description, usage, collectionManager);
    }

    @Override
    public Boolean execute(String[] argument) {
        return super.getCollectionManager().addIfMax(super.createElement(argument));
    }
}
