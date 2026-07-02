package com.lld.practice.vendingmachine;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Driver extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    Label label = new Label("Vending Machine");
    Scene scene = new Scene(new StackPane(label), 600, 400);
    stage.setTitle("Vending Machine");
    stage.setScene(scene);
    stage.show();
  }
}
