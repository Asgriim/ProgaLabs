package com.example.lab8_2;

import Apps.ClientSingleton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Locale;
import java.util.ResourceBundle;

public class HelpController {

    public Label descLabel;
    public Label commandsLabel;
    @FXML
    private Label addLabel;

    @FXML
    private Label addMaxLabel;

    @FXML
    private Label clearLabel;

    @FXML
    private Label countBySolLabel;

    @FXML
    private Label infoLabel;

    @FXML
    private Label printSolLabel;

    @FXML
    private Label removeByClimateLabel;

    @FXML
    private Label removeByIdLabel;

    @FXML
    private Label removeFirstLabel;

    @FXML
    private Label removeHeadLabel;

    @FXML
    private Label scriptLabel;
    private ClientSingleton client;

    private ResourceBundle guiBundle;

    private ResourceBundle serverTextBundle;

    @FXML
    public void initialize(){
        client = ClientSingleton.getInstance();
        changeLocalization(client.getCurrLocale());
    }


    public void changeLocalization(Locale locale){
        this.guiBundle = ResourceBundle.getBundle("GUItext", locale);
        this.serverTextBundle = ResourceBundle.getBundle("ServerResponces", locale);
        commandsLabel.setText(guiBundle.getString("commands_help"));
        descLabel.setText(guiBundle.getString("desc_help"));
        addLabel.setText(guiBundle.getString("add_help"));
        addMaxLabel.setText(guiBundle.getString("add_if_max_help"));
        clearLabel.setText(guiBundle.getString("clear_help"));
        countBySolLabel.setText(guiBundle.getString("count_by_sol_help"));
        scriptLabel.setText(guiBundle.getString("script_help"));
        infoLabel.setText(guiBundle.getString("info_help"));
        printSolLabel.setText(guiBundle.getString("print_uniq_sol_help"));
        removeByClimateLabel.setText(guiBundle.getString("remove_by_climate_help"));
        removeByIdLabel.setText(guiBundle.getString("remove_by_id_help"));
        removeFirstLabel.setText(guiBundle.getString("remove_first_help"));
        removeHeadLabel.setText(guiBundle.getString("remove_head_help"));
    }
}
