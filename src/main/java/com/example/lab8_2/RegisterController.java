package com.example.lab8_2;

import API.ServerRequest;
import API.ServerResponse;
import Apps.ClientSingleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class RegisterController {
    public Text registerLabel;
    public Text loginLabel;
    public Text passwordLAbel;
    public Text password2Label;
    public Button ruButton;
    public Button rsButton;
    public Button plButton;
    public Button esButton;
    private ClientSingleton client;

    @FXML
    private Label userLabel;

    @FXML
    private Button backButton;

    @FXML
    private TextField loginField;

    @FXML
    private TextField pass1Field;

    @FXML
    private TextField pass2field;

    @FXML
    private Button registerButton;

    private Locale currLocale;

    private ResourceBundle guiBundle;
    private ResourceBundle serverTextBundle;

    public RegisterController() {
        this.client = ClientSingleton.getInstance();
    }

    @FXML
    public void initialize(){
        changeLocalization(client.getCurrLocale());
    }

    public void changeLocalization(Locale locale){
        this.guiBundle = ResourceBundle.getBundle("GUItext", locale);
        this.serverTextBundle = ResourceBundle.getBundle("ServerResponces", locale);
        loginLabel.setText(guiBundle.getString("login_label"));
        registerLabel.setText(guiBundle.getString("register_label"));
        passwordLAbel.setText(guiBundle.getString("password_label"));
        password2Label.setText(guiBundle.getString("repeat_pass"));
        backButton.setText(guiBundle.getString("back"));
        registerButton.setText(guiBundle.getString("register_label"));
    }

    @FXML
    void backToSignIn(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("sign-in.fxml"));
        try {
            loader.load();
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            registerButton.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openMainWindow(){
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("main-table.fxml"));
        try {
            if (!client.requestCollectionFromServer()) {
                userLabel.setText(serverTextBundle.getString("server_is_dead"));
                return;
            }
            loader.load();
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
            registerButton.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void register(ActionEvent event) {
        ServerRequest request = new ServerRequest();
        if (loginField.getText().equals("")) {
            userLabel.setText(guiBundle.getString("empty_login"));
            return;
        }
        if (pass1Field.getText().equals("") || pass2field.getText().equals("")) {
            userLabel.setText(guiBundle.getString("empty_pass"));
            return;
        }
        if (!pass1Field.getText().equals(pass2field.getText())) {
            userLabel.setText(guiBundle.getString("passwords_not_the_same"));
            return;
        }
        request.setLogIn(loginField.getText());
        request.setPassword(client.getHash(pass1Field.getText()));
        request.setCommand("register");
        ServerResponse response = client.sendRequest(request);
        if (response == null) {
            userLabel.setText(serverTextBundle.getString("server_is_dead"));
        }
        System.out.println(response);
        if(response.getResponse().equals("register successfully")){
            client.setOwnerId(response.getOwnerId());
            client.setCommandMap(response.getCommandMap());
            client.initializeCoommandManager();
            openMainWindow();
        }
        if (response.getResponse().equals("this login is already exist\n" +
                " enter different one")) {
            userLabel.setText(serverTextBundle.getString("this_login_is_already_exist"));
        }
        else userLabel.setText(response.getResponse());
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