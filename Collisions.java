package lab;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// прослушивание несущей
// обнаружение коллизий
// расчет случайной задержки по формуле
// окно коллизий 0.5 сек == 500 мсек

public class CSMA extends Application {
    private static TextField inputField;
    private static TextField outputField;
    private static TextArea statusArea;
    private static ComboBox<Integer> comboBox;
    public static int value = 0;

    public static class MyRunnable implements Runnable {
        private String text;
        private int maxCollisionCount;

        public MyRunnable(String str, int count) {
            this.text = str;
            this.maxCollisionCount = count;
        }

        @Override
        public void run() {
            for (String symbol : text.split("")) {
                boolean collision;
                int collisionsCount = 0;
                statusArea.appendText(symbol + ": ");
                for (int j = 0; j < maxCollisionCount; ) {
                    while(true) {
                        try {
                            if(((int) (Math.random() * 10)) <= 3) break;
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    collision = ((int)(Math.random() * 10) <= 3);
                    if (collision) {
                        statusArea.appendText("*");
                        j++;
                        if (j == maxCollisionCount) break;
                        try {
                            Thread.sleep(new Random().nextInt((int) Math.pow(2, j)));
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        if (j == 0) statusArea.appendText("");
                        outputField.appendText(symbol);
                        break;
                    }
                }
                statusArea.appendText("\n");
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static void initGUI(Stage stage) {
        stage.setTitle("Lab4: CSMA");
        stage.setWidth(400);
        stage.setHeight(400);

        Label inputLabel = new Label("Input");
        inputField = new TextField();
        Label outputLabel = new Label("Output");
        outputField = new TextField();
        outputField.setEditable(false);
        Label debugAndControlLabel = new Label("Status");
        statusArea = new TextArea();
        statusArea.setEditable(false);
        statusArea.setPrefHeight(300);
        ObservableList<Integer> collection = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6 ,7, 8, 9, 10);
        comboBox = new ComboBox<>(collection);
        comboBox.setValue(10);
        Label collisionLabel = new Label("Max collision count");
        HBox hBox = new HBox(5, collisionLabel, comboBox);
        hBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(5, inputLabel, inputField, outputLabel, outputField, debugAndControlLabel, statusArea, hBox);
        vBox.setPadding(new Insets(5));
        vBox.setAlignment(Pos.CENTER);
        stage.setScene(new Scene(vBox));
        stage.show();
    }

    @Override
    public void start(Stage stage) {

        initGUI(stage);

        inputField.setOnKeyReleased(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                outputField.clear();
                statusArea.clear();
                String text = inputField.getText();
                if(!text.isEmpty()) {
                    int maxCollisionCount = comboBox.getValue();
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(new MyRunnable(text, maxCollisionCount));
                }
                inputField.clear();
            }
        });
    }
}
