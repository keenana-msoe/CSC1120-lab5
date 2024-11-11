/*
 * Course: CSC 1120
 * Term: Spring 2024
 * Assignment: Lab 5
 * Name: Andrew Keenan
 * Created: 2-14-24
 */
package keenana;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * driver class for lab 3
 */
public class Lab5 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Mean Image Median");
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("Lab5.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
}
