package com.example.lab8_2;

import API.ServerRequest;
import API.ServerResponse;
import Apps.ClientSingleton;
import data.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainTableController {
    @FXML
    public Button test;
    @FXML
    public Button add;
    @FXML
    public Button addIfMax;
    @FXML
    public Button clearButton;
    @FXML
    public Button countBySOLButton;


    @FXML
    public ChoiceBox climateChoiceBox;
    @FXML
    public ChoiceBox solChoiceBox;
    @FXML
    public Button removeByClimateButton;

    @FXML
    public Label outputField;
    @FXML
    public TextField idTextField;

    @FXML
    public Button printUniqSolButton;
    @FXML
    public Button removeHeadButton;
    @FXML
    public Button removeFirstButton;
    @FXML
    public Button removeByIdButton;

    @FXML
    public Button infoButton;
    @FXML
    public Button helpButton;
    @FXML
    public Button scriptButton;

    @FXML
    public Canvas canvas;
    @FXML
    public Button updateTableButton;
    @FXML
    public Button deleteRow;
    public Button ruButton;
    public Button rsButton;
    public Button plButton;
    public Button esButton;
    public Tab canvasTab;
    public Tab tableTab;

    @FXML
    private ClientSingleton client;

    @FXML
    private TableView<City> mainTable ;

    @FXML
    private TableColumn<City, Integer> area;

    @FXML
    private TableColumn<City, Climate> climate;

    @FXML
    private TableColumn<City, String> date;

    @FXML
    private TableColumn<City, String> governorName;

    @FXML
    private TableColumn<City, Integer> id;

    @FXML
    private TableColumn<City, String> name;

    @FXML
    private TableColumn<City, Integer> population;

    @FXML
    private TableColumn<City, Double> sea;

    @FXML
    private TableColumn<City, StandardOfLiving> sol ;

    @FXML
    private TableColumn<City, Integer> telephone;

    @FXML
    private TableColumn<City, Integer> x;

    @FXML
    private TableColumn<City, Double> y;
    @FXML
    private ObservableList<City> data;

    @FXML
    private ObservableList<Climate> climateList;

    @FXML
    private ObservableList<StandardOfLiving> solList;

    private GraphicsContext context;

    private Locale currLocale;

    private ResourceBundle guiBundle;

    private ResourceBundle serverTextBundle;

    private City tempCity;
    Date lastClicked;
    public MainTableController() {
        this.client = ClientSingleton.getInstance();
        mainTable = new TableView<>();
//        mainTable.setStyle("  -fx-background-color: transparent;");
        this.name = new TableColumn<>();
        this.date = new TableColumn<>();
        this.id = new TableColumn<>();
        this.x = new TableColumn<>();
        this.y = new TableColumn<>();
        this.area = new TableColumn<>();
        this.population = new TableColumn<>();
        this.sea = new TableColumn<>();
        this.telephone = new TableColumn<>();
        this.climate = new TableColumn<>();
        this.sol = new TableColumn<>();
        this.governorName = new TableColumn<>();
        this.data = FXCollections.observableArrayList();
        this.climateList = FXCollections.observableArrayList();
        this.solList = FXCollections.observableArrayList();
        this.canvas = new Canvas();
    }

    @FXML
    public void initialize(){
        changeLocalization(client.getCurrLocale());
        context = canvas.getGraphicsContext2D();
//        mainTable.setStyle("-fx-background-color: #df9390;");
        mainTable.setRowFactory((TableView<City> table) -> new TableRow<>(){
            protected void updateItem(City city, boolean paramBoolean) {
                if (city != null && city.getOwnerId() == client.getOwnerId()) {
                    setStyle("-fx-background-color: #308cd5;");
                }

//                else setStyle("-fx-background-color: #2196f3");
                else setStyle(mainTable.getStyle());
                super.updateItem(city, paramBoolean);
            }
        });
        System.out.println(mainTable.getStyle());
        id.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        name.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getName()));
        x.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCoordinates().getX()));
        y.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCoordinates().getY()));
        area.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getArea()));
        population.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPopulation()));
        sea.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMetersAboveSeaLevel()));
        telephone.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTelephoneCode()));
        climate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getClimate()));
        sol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getStandardOfLiving()));
        governorName.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getGovernorName()));
        setItemsServer();
        climateList.addAll(Climate.values());
        climateChoiceBox.setItems(climateList);
        climateChoiceBox.setValue(Climate.OCEANIC);
        solList.addAll(StandardOfLiving.values());
        solChoiceBox.setItems(solList);
        solChoiceBox.setValue(StandardOfLiving.NIGHTMARE);
        drawFromTable();
    }
    public void drawFromTable(){
        Iterator iterator = data.iterator();
        City tempCity;
        while (iterator.hasNext()){
            tempCity = (City) iterator.next();
            if (tempCity.getOwnerId() == client.getOwnerId()) drawCity(tempCity,Color.LIGHTPINK);
            else drawCity(tempCity,Color.AQUAMARINE);
        }
    }

    public Integer checkArea(Integer area){
        if (area >= 200 ){
            return 200;
        }
        return area;
    }

    public void drawCity(City city, Color color){
        Integer tempArea = checkArea(city.getArea());
        context.setFill(color);
        context.fillOval(city.getCoordinates().getX(),city.getCoordinates().getY(),tempArea,tempArea);
        context.setFill(Color.DARKRED);
        context.fillText(city.getName(),city.getCoordinates().getX() + (tempArea * 0.45),
                city.getCoordinates().getY() + (tempArea * 0.5));
    }

    public void clearCanvas(){
        context.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
    }

    public void updateCollection(){
        data.clear();
        data.addAll(client.getCityCollection().toArray(City[]::new));
        clearCanvas();
        drawFromTable();
    }

    public void setItemsServer(){
        data.addAll(client.getCityCollection().toArray(City[]::new));
        mainTable.setItems(data);
    }

    @FXML
    public void mouseClicked(javafx.scene.input.MouseEvent mouseEvent) {
        City row = mainTable.getSelectionModel().getSelectedItem();
        if (row == null ) return;
        if (row.getOwnerId() != client.getOwnerId()) return;
        if (row != tempCity){
            System.out.println(3);
            tempCity = row;
            lastClicked = new Date();
            System.out.println(1);
        }
        else {
            Date now = new Date();
            long diff = now.getTime() - lastClicked.getTime();
            if (diff < 300){
                FXMLLoader loader1 = new FXMLLoader(HelloApplication.class.getResource("update-window.fxml"));
                try {
                    loader1.load();
                    Parent root = loader1.getRoot();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setResizable(false);
                    UpdateController controller = loader1.getController();
                    controller.setCity(row);
                    stage.showAndWait();
                    updateCollectionOnClient();
                    updateCollection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                lastClicked = new Date();
            }
        }
    }

    @FXML
    public void clickedTwice(javafx.scene.input.MouseEvent mouseEvent) {
    }

    @FXML
    public void test(ActionEvent event) {
        mainTable.getItems().add(client.getCityCollection().peek());
    }

    @FXML
    public void addCommand(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("add.fxml"));
        add.setDisable(true);
        try {
            loader.load();
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            updateCollectionOnClient();
            updateCollection();
            add.setDisable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addIfMax(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("AddIfMax.fxml"));
        addIfMax.setDisable(true);
        try {
            loader.load();
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
            updateCollectionOnClient();
            updateCollection();
            addIfMax.setDisable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void clearCommand(ActionEvent event) {
        ServerRequest serverRequest = client.getServerRequest();
        serverRequest.setCommand("clear");
        ServerResponse response = client.sendRequest(serverRequest);
        if (response == null ) {
            return;
        }
        updateCollectionOnClient();
        updateCollection();
    }

    public boolean updateCollectionOnClient(){
        return client.requestCollectionFromServer();
    }

    @FXML
    public void countBySOLCommand(ActionEvent event) {
        ServerRequest request = client.getServerRequest();
        request.setCommand("count_by_standard_of_living");
        request.setArgument(new String[]{solChoiceBox.getValue().toString()});
        ServerResponse response = client.sendRequest(request);
        if (response == null){
            outputField.setText("error");
            return;
        }
        outputField.setText(response.getResponse());
    }

    @FXML
    public void removeByClimateCommand(ActionEvent event) {
        ServerRequest request = client.getServerRequest();
        request.setCommand("remove_any_by_climate");
        request.setArgument(new String[]{climateChoiceBox.getValue().toString()});
        System.out.println(request);
        ServerResponse response = client.sendRequest(request);
        if (response == null){
            outputField.setText("error");
            return;
        }
        updateCollectionOnClient();
        updateCollection();
    }

    @FXML
    public void removeByIdCommand(ActionEvent event) {
        if(idTextField.getText().equals("")) {
            outputField.setText(guiBundle.getString("id_not_specified"));
            return;
        }
        try {
            Integer.parseInt(idTextField.getText());
        } catch (NumberFormatException e) {
            outputField.setText("id_must_be_int");
            return;
        }
        ServerRequest request = client.getServerRequest();
        request.setArgument(new String[]{idTextField.getText()});
        request.setCommand("remove_by_id");
        ServerResponse response = client.sendRequest(request);
        if(response == null){
            outputField.setText("error");
            return;
        }
        updateCollectionOnClient();
        updateCollection();
    }

    @FXML
    public void removeFirstCommand(ActionEvent event) {
        ServerRequest request = client.getServerRequest();
        request.setCommand("remove_first");
        ServerResponse response = client.sendRequest(request);
        if(response == null){
            outputField.setText("error");
            return;
        }

        updateCollectionOnClient();
        updateCollection();
    }

    @FXML
    public void removeHeadCommand(ActionEvent event) {
        ServerRequest request = client.getServerRequest();
        request.setCommand("remove_head");
        ServerResponse response = client.sendRequest(request);
        if(response == null){
            outputField.setText("error");
            return;
        }
        updateCollectionOnClient();
        updateCollection();
    }
    @FXML
    public void printUniqSolButton(ActionEvent event) {
        ServerRequest request = client.getServerRequest();
        request.setCommand("print_unique_standard_of_living");
        ServerResponse response = client.sendRequest(request);
        if (response == null){
            outputField.setText("error");
            return;
        }
        outputField.setText(response.getResponse());
    }

    @FXML
    public void helpCommand(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("help-window.fxml"));
        try {
            loader.load();
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void infoCommand(ActionEvent event) {
        ServerRequest request = client.getServerRequest();
        request.setCommand("info");
        ServerResponse response = client.sendRequest(request);
        if(response == null){
            outputField.setText("error");
            return;
        }
        outputField.setText(response.getResponse().replace("\n","  ;"));
    }

    @FXML
    public void scriptCommand(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        client.execScript(file);
        updateCollectionOnClient();
        updateCollection();
    }

    public void updateTableAction(ActionEvent event) {
        updateCollectionOnClient();
        updateCollection();
    }

    @FXML
    public void deleteRow(ActionEvent event) {
        City row = mainTable.getSelectionModel().getSelectedItem();
        if (row == null ) return;
        if (row.getOwnerId() != client.getOwnerId()) return;
        ServerRequest request = client.getServerRequest();
        request.setArgument(new String[]{row.getId().toString()});
        request.setCommand("remove_by_id");
        ServerResponse response = client.sendRequest(request);
        updateCollectionOnClient();
        updateCollection();
    }

    public void changeLocalization(Locale locale){
        this.guiBundle = ResourceBundle.getBundle("GUItext", locale);
        this.serverTextBundle = ResourceBundle.getBundle("ServerResponces", locale);
        tableTab.setText(guiBundle.getString("table"));
        canvasTab.setText(guiBundle.getString("canvas"));
        deleteRow.setText(guiBundle.getString("delete"));
        updateTableButton.setText(guiBundle.getString("update_table"));
    }

    public void ruLocalization(ActionEvent event) {
        client.setCurrLocale(new Locale("ru"));
        changeLocalization(client.getCurrLocale());
    }

    public void rsLocalization(ActionEvent event) {
        client.setCurrLocale(new Locale("rs"));
        changeLocalization(client.getCurrLocale());
    }

    public void plLocalization(ActionEvent event) {
        client.setCurrLocale(new Locale("pl"));
        changeLocalization(client.getCurrLocale());
    }

    public void esLocalization(ActionEvent event) {
        client.setCurrLocale(new Locale("es"));
        changeLocalization(client.getCurrLocale());
    }
}
