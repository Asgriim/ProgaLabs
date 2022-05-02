package utility;
import data.*;
import exсeptions.FileIssueException;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * operates a collection
 */
public class CollectionManager implements Serializable {
    private static final long serialVersionUID = 1;
    private PriorityQueue<City> cityCollection;
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
        this.cityCollection = new PriorityQueue<>();
        this.initializationTime = LocalDateTime.now();
        this.idComparator = (o1, o2) -> o1.getId() - o2.getId();
    }

        /**
     *
     * @return city collection
     */
    public PriorityQueue<City> getCollection() {
        return cityCollection;
    }

    public City getById(int id){
        return cityCollection.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
    }

    public Boolean removeById(int id){
        City temp = getById(id);
        if (temp == null) return false;
        cityCollection.remove(temp);
        return true;
    }

    public void addToCollect(City city){
        this.cityCollection.add(city);
    }

    public Boolean addIfMax(City city){
        if (this.cityCollection.size() > 0) {
            if (this.cityCollection.peek().compareTo(city) > 0 ) {
                addToCollect(city);
                return true;
            }
            else {return false;}
        }
        else {
            addToCollect(city);
            return true;
        }
    }

    public void clearCollection(){
        this.cityCollection.clear();
    }

    public int countBySOL(StandardOfLiving SOL){
       return (int)cityCollection.stream().filter(x -> (x.getStandardOfLiving() != null && x.getStandardOfLiving().equals(SOL))).count();
    }

    public boolean removeAnyByClimate(Climate climate){
        City temp = cityCollection.stream().filter(x -> x.getClimate() != null && x.getClimate().equals(climate)).findFirst().orElse(null);
        if (temp == null) return false;
        cityCollection.remove(temp);
        return true;
    }

    public City removeHead(){
       return this.cityCollection.poll();
    }

    public boolean removeFirst(){
        if(this.cityCollection.poll() == null) return false;
        return true;
    }

    public Set<StandardOfLiving> getUniqueStandards(){
        return  cityCollection.stream().map(x -> x.getStandardOfLiving()).filter(standardOfLiving -> standardOfLiving != null).collect(Collectors.toSet());
    }

    public String getStringCollection(){
        String s = cityCollection.stream().map(x -> x.toString()).collect(Collectors.joining("\n"));
        if (s.length() == 0){return "collection is empty"; }
        return s;
    }

    private Iterator<City> getIterator(){
        return  this.cityCollection.iterator();
    }

    private int createId(){
        if (!cityCollection.isEmpty()){
            return cityCollection.stream().max(idComparator).get().getId() + 1;
        }

        return 1;
    }

     public City createElement(String name,int x, int y, int area, int population, Double metersAboveSeaLevel,
                                int telephoneCode, Climate climate, StandardOfLiving standardLiving,String humanName ){
        return new City(createId(),name,new Coordinates(x,y),LocalDateTime.now(),area,population,metersAboveSeaLevel,
                telephoneCode,climate,standardLiving,new Human(humanName));
     }

     public Boolean saveCollectionToFile(){
        try {
            fileManager.parseToFile(cityCollection);
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

    public void setCityCollection(PriorityQueue<City> cityCollection) {
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
