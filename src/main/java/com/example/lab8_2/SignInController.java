package com.example.lab8_2;


import API.ServerRequest;
import API.ServerResponse;
import Apps.ClientSingleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class SignInController {
    @FXML
    public Text signLabel;

    @FXML
    public Text loginLabel;
    @FXML
    public Text passwordLabel;
    @FXML
    public Button ruButton;
    @FXML
    public Button rsButton;
    @FXML
    public Button plButton;
    @FXML
    public Button esButton;
    private ResourceBundle guiBundle;
    private ResourceBundle serverTextBundle;
    private ClientSingleton client;
    @FXML
    private PasswordField passwordField;
    private Locale currLocale;

    @FXML
    private Label labelForUser;

    @FXML
    private TextField loginField;

    @FXML
    private Button sosi;
    @FXML
    private Hyperlink registerLink;

    public SignInController() {
        this.client = ClientSingleton.getInstance();
//        changeLocalization(Locale.getDefault());
    }

    @FXML
    public void initialize(){
        changeLocalization(client.getCurrLocale());
    }

    @FXML
    private void openMainWindow(){
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("main-table.fxml"));
        try {
            if (!client.requestCollectionFromServer()) {
                labelForUser.setText(serverTextBundle.getString("server_is_dead"));
                return;
            }
            loader.load();
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
            sosi.getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openRegisterWindow(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("register-window.fxml"));
        try {
            loader.load();
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
            sosi.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void signIn(ActionEvent event) {
        ServerRequest request = new ServerRequest();
        String login = loginField.getText();
        if(login.equals("")) {
            labelForUser.setText(guiBundle.getString("empty_login"));
            return;
        }
        String pass = passwordField.getText();
        if(pass.equals("")){
            labelForUser.setText(guiBundle.getString("empty_pass"));
            return;
        }
        request.setLogIn(login);
        request.setPassword(client.getHash(pass));
        request.setCommand("sign");
        ServerResponse response = client.sendRequest(request);
        if (response == null) {
            labelForUser.setText(serverTextBundle.getString("server_is_dead"));
            return;
        }
        System.out.println(response);
        if(response.getResponse().equals("sign in successfully")){
            client.setOwnerId(response.getOwnerId());
            client.setCommandMap(response.getCommandMap());
            client.initializeCoommandManager();
            openMainWindow();
        }
        if (response.getResponse().equals("no such login or wrong password")) {
            labelForUser.setText(serverTextBundle.getString("no_such_login_or_wrong_password"));
        }
        else labelForUser.setText(response.getResponse());
    }

    public void changeLocalization(Locale locale){
        this.guiBundle = ResourceBundle.getBundle("GUItext", locale);
        this.serverTextBundle = ResourceBundle.getBundle("ServerResponces", locale);
        signLabel.setText(guiBundle.getString("sign_in_label"));
        registerLink.setText(guiBundle.getString("huper_link"));
        loginLabel.setText(guiBundle.getString("login_label"));
        passwordLabel.setText(guiBundle.getString("password_label"));
        sosi.setText(guiBundle.getString("sign_in_button"));
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
