package utility;
import data.*;
import exсeptions.FileIssueException;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

/**
 * operates a collection
 */
public class CollectionManager implements Serializable {
    private static final long serialVersionUID = 1;
    private PriorityBlockingQueue<City> cityCollection;
//    private PriorityQueue<City> cityCollection;
    private LocalDateTime initializationTime;
    private String type = "PriorityQueue";
    private FileManager fileManager;
    private Comparator<City> idComparator;
    /**
     *
     * @param fileManager
     */
    public CollectionManager(FileManager fileManager){
        this.fileManager = fileManager;
        this.cityCollection = new PriorityBlockingQueue();
//        this.cityCollection = new PriorityQueue<>();
        this.initializationTime = LocalDateTime.now();
        this.idComparator = (o1, o2) -> o1.getId() - o2.getId();
    }

        /**
     *
     * @return city collection
     */
    public PriorityBlockingQueue<City> getCollection() {
        return cityCollection;
    }

    public City getById(int id){
        return cityCollection.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
    }

    public Boolean removeById(int id, Integer ownerId){return null;}

    public Boolean removeById(int id){
        City temp = getById(id);
        if (temp == null) return false;
        cityCollection.remove(temp);
        return true;
    }

    public boolean addToCollect(City city){
        this.cityCollection.add(city);
        return true;
    }

    public Boolean addIfMax(City city){
        if (this.cityCollection.size() > 0) {
            if (this.cityCollection.peek().compareTo(city) > 0 ) {
                return addToCollect(city);
            }
            else {return false;}
        }
        else {
            return addToCollect(city);
        }
    }

    public boolean update(String[] argument,Integer id, Integer ownerId){return false;}

    public void clearCollection(int ownerId){} //for future generations

    public void clearCollection(){
        this.cityCollection.clear();
    }

    public int countBySOL(StandardOfLiving SOL){
       return (int)cityCollection.stream().filter(x -> (x.getStandardOfLiving() != null && x.getStandardOfLiving().equals(SOL))).count();
    }

    public boolean removeAnyByClimate(Climate climate,Integer ownerId){return true;}

    public boolean removeAnyByClimate(Climate climate){
        City temp = cityCollection.stream().filter(x -> x.getClimate() != null && x.getClimate().equals(climate)).findFirst().orElse(null);
        if (temp == null) return false;
        cityCollection.remove(temp);
        return true;
    }

    public City removeHead(){
        try {
            return this.cityCollection.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public City removeHead(Integer ownerId){return null;}

    public boolean removeFirst(){
        try {
            if(this.cityCollection.take() == null) return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean removeFirst(Integer ownerId){return true;}

    public Set<StandardOfLiving> getUniqueStandards(){
        return  cityCollection.stream().map(x -> x.getStandardOfLiving()).filter(standardOfLiving -> standardOfLiving != null).collect(Collectors.toSet());
    }

    public String getStringCollection(){
        String s = cityCollection.stream().map(x -> x.toString()).collect(Collectors.joining("\n"));
        if (s.length() == 0){return "collection is empty"; }
        return s;
    }

    public Iterator<City> getIterator(){
        return  this.cityCollection.iterator();
    }

    private int createId(){
        if (!cityCollection.isEmpty()){
            return cityCollection.stream().max(idComparator).get().getId() + 1;
        }

        return 1;
    }

    public City createElement(String[] argument, Integer ownerId){return null;}
    public City createElement(String name,int x, int y, int area, int population, Double metersAboveSeaLevel,
                                int telephoneCode, Climate climate, StandardOfLiving standardLiving,String humanName ){

        return new City(createId(),name,new Coordinates(x,y),LocalDateTime.now(),area,population,metersAboveSeaLevel,
                telephoneCode,climate,standardLiving,new Human(humanName));
     }
     @Deprecated
     public Boolean saveCollectionToFile(){
        try {
            fileManager.parseToFile(null);
        } catch (FileIssueException | IOException | JAXBException e) {
            return false;
        }
        return true;
     }

    public LocalDateTime getInitializationTime() {
        return initializationTime;
    }

    public String getType() {
        return type;
    }

    public void setCityCollection(PriorityBlockingQueue<City> cityCollection) {
        this.cityCollection = cityCollection;
    }

    public boolean checkCollection(){
        boolean test = false;
        if(getCollection() == null){
            return test;
        }
        Iterator<City> iter = getIterator();
        City tempCity;
        Set<Integer> id = new HashSet<>();
        while (iter.hasNext()){
            tempCity = iter.next();
            id.add(tempCity.getId());
            if(tempCity.getTelephoneCode() == null){
                if (tempCity.getId() <= 0 || tempCity.getId() == null ||
                        tempCity.getName() == null || tempCity.getCoordinates() == null ||
                        tempCity.getCoordinates().getX() == null ||  tempCity.getCoordinates().getX() <= -251 ||
                        tempCity.getCreationDate() == null
                        || tempCity.getArea() == null
                        ){
                    return test;
                }
                continue;
            }
            if (tempCity.getId() <= 0 || tempCity.getId() == null ||
            tempCity.getName() == null || tempCity.getCoordinates() == null ||
            tempCity.getCoordinates().getX() == null ||  tempCity.getCoordinates().getX() <= -251 ||
            tempCity.getCreationDate() == null
                    || tempCity.getArea() == null
                    || tempCity.getTelephoneCode() == 0
                    || tempCity.getTelephoneCode() < 0
                    || tempCity.getTelephoneCode() > 100000){
                return test;
            }
        }
        if (id.size() != getCollection().size()){
            return test;
        }
        test = true;
        return test;
    }
}
