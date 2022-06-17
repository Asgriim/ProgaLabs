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

public class AddIfMaxController {
    public Label nameLablel;
    public Label areaLabel;
    public Label populationLabel;
    public Label climateLabel;
    public Label solLabel;
    public Label seaLAbel;
    public Label telephoneLabel;
    public Label goverLAbel;
    @FXML
    private ClientSingleton client;

    @FXML
    private Button add_button;

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

    private ResourceBundle guiBundle;

    private ResourceBundle serverTextBundle;

    private Locale currLocale;
    @FXML
    private ObservableList<Climate> climateList;

    @FXML
    private ObservableList<StandardOfLiving> solList;

    public AddIfMaxController(){
        this.client = ClientSingleton.getInstance();
        this.climateList = FXCollections.observableArrayList();
        this.solList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize(){
        climateList.addAll(Climate.values());
        climateChoiceBox.setItems(climateList);
        climateChoiceBox.setValue(Climate.OCEANIC);
        solList.addAll(StandardOfLiving.values());
        solChoiceBox.setItems(solList);
        solChoiceBox.setValue(StandardOfLiving.NIGHTMARE);
        changeLocalization(client.getCurrLocale());
    }

    @FXML
    void addCommand(ActionEvent event) {
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
        serverRequest.setCommand("add");
        serverRequest.setArgument(argument.toArray(String[]::new));
        serverRequest.setOwnerId(client.getOwnerId());
        System.out.println(serverRequest);
        ServerResponse response = client.sendRequest(serverRequest);
        System.out.println(response);
        if (!client.requestCollectionFromServer()) {
            error_label.setText(serverTextBundle.getString("server_is_dead"));
        }
        add_button.getScene().getWindow().hide();
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
        add_button.setText(guiBundle.getString("add"));
    }

}

