package com.example.lab8_2;

import API.ServerRequest;
import API.ServerResponse;
import Apps.ClientSingleton;
import data.*;
import exсeptions.WrongFieldFormatException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class UpdateController {
    public Label nameLablel;
    public Label areaLabel;
    public Label populationLabel;
    public Label climateLabel;
    public Label solLabel;
    public Label seaLAbel;
    public Label telephoneLabel;
    public Label goverLAbel;
    @FXML
    private Button upd_button;

    @FXML
    private TextField area_field;

    @FXML
    private ChoiceBox<Climate> climateChoiceBox;

    @FXML
    private Label error_label;

    @FXML
    private TextField governorNameField;

    @FXML
    private TextField name_field;

    @FXML
    private TextField population_field;

    @FXML
    private TextField sea_field;

    @FXML
    private ChoiceBox<StandardOfLiving> solChoiceBox;

    @FXML
    private TextField telephone_field;

    @FXML
    private TextField x_field;

    @FXML
    private TextField y_field;

    @FXML
    private ClientSingleton client;
    @FXML
    private City currCity;

    @FXML
    private ObservableList<Climate> climateList;

    @FXML
    private ObservableList<StandardOfLiving> solList;

    private ResourceBundle guiBundle;

    private ResourceBundle serverTextBundle;

    public UpdateController(){
        client = ClientSingleton.getInstance();
        this.climateList = FXCollections.observableArrayList();
        this.solList = FXCollections.observableArrayList();
    }


    @FXML
    public void initialize(){
        changeLocalization(client.getCurrLocale());
    }

    @FXML
    void updateCommand(ActionEvent event) {
        List<String> argument = new ArrayList<>();
        try {
            City.validateName(name_field.getText());
            Coordinates.validateX(x_field.getText());
            Coordinates.validateY(y_field.getText());
            City.validateArea(area_field.getText());
            City.validatePopulation(population_field.getText());
            City.validateMetersAboveSea(sea_field.getText());
            City.validateTelephoneCode(telephone_field.getText());
            Human.validateName(governorNameField.getText());
        } catch (WrongFieldFormatException e) {
            error_label.setText(e.getMessage());
            return;
        }
        argument.add(name_field.getText());
        argument.add(x_field.getText());
        argument.add(y_field.getText());
        argument.add(area_field.getText());
        argument.add(population_field.getText());
        argument.add(sea_field.getText());
        argument.add(telephone_field.getText());
        argument.add(climateChoiceBox.getValue().toString());
        argument.add(solChoiceBox.getValue().toString());
        argument.add(governorNameField.getText());
        ServerRequest serverRequest = new ServerRequest();
        serverRequest.setCommand("update");
        serverRequest.setArgument(argument.toArray(String[]::new));
        serverRequest.setOwnerId(client.getOwnerId());
        serverRequest.setId(new String[]{currCity.getId().toString()});
        System.out.println(serverRequest);
        ServerResponse response = client.sendRequest(serverRequest);
        System.out.println(response);
        if (!client.requestCollectionFromServer()) {
            error_label.setText("error idi nahui");
        }
        upd_button.getScene().getWindow().hide();
    }

    public void setCity(City city){
        currCity = city;
        climateList.addAll(Climate.values());
        climateChoiceBox.setItems(climateList);
        climateChoiceBox.setValue(currCity.getClimate());
        solList.addAll(StandardOfLiving.values());
        solChoiceBox.setItems(solList);
        solChoiceBox.setValue(currCity.getStandardOfLiving());
        name_field.setText(currCity.getName());
        x_field.setText(currCity.getCoordinates().getX().toString());
        y_field.setText(String.valueOf(currCity.getCoordinates().getY()));
        area_field.setText(currCity.getArea().toString());
        population_field.setText(currCity.getPopulation().toString());
        sea_field.setText(currCity.getMetersAboveSeaLevel().toString());
        telephone_field.setText(currCity.getTelephoneCode().toString());
        governorNameField.setText(currCity.getGovernorName());
    }

    public void changeLocalization(Locale locale){
        this.guiBundle = ResourceBundle.getBundle("GUItext", locale);
        this.serverTextBundle = ResourceBundle.getBundle("ServerResponces", locale);
        nameLablel.setText(guiBundle.getString("name"));
        areaLabel.setText(guiBundle.getString("area"));
        populationLabel.setText(guiBundle.getString("population"));
        seaLAbel.setText(guiBundle.getString("meters_above_sea"));
        telephoneLabel.setText(guiBundle.getString("phone_code"));
        climateLabel.setText(guiBundle.getString("climate"));
        solLabel.setText(guiBundle.getString("standard_of_living"));
        goverLAbel.setText(guiBundle.getString("governor_name"));
        upd_button.setText(guiBundle.getString("upd"));
    }

}
