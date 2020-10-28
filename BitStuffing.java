import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BitStuffing extends Application {
    private static TextField inputField;
    private static TextField outputField;
    private static TextArea statusArea;

    public static void main(String[] args) {
        launch(args);
    }

    public static boolean isValid(final String str) {
        for(char c : str.toCharArray()) {
            if(c != '0' && c != '1' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    public static void updateStatus(final String data) {
        statusArea.clear();
        statusArea.appendText(data);
    }

    public static void initGUI(Stage stage) {
        stage.setTitle("Lab2: bit stuffing");
        stage.setHeight(230);
        Label inputLabel = new Label("Input");
        inputField = new TextField();
        Label outputLabel = new Label("Output");
        outputField = new TextField();
        Label statusLabel = new Label("Status");
        statusArea = new TextArea();
        statusArea.setPrefHeight(70);
        outputField.setEditable(false);
        statusArea.setEditable(false);

        VBox vBox = new VBox(5, inputLabel, inputField, outputLabel, outputField, statusLabel, statusArea);
        vBox.setPadding(new Insets(5));
        vBox.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(vBox));
        stage.setResizable(false);
        stage.show();
    }
    // прослушивание несущей
    // обнаружение коллизий
    // расчет случайной задержки по формуле

    public static void printWithBS(final String str1) {
        Matcher m = Pattern.compile("(?=(01110001))").matcher(str1);

        List<Integer> pos = new ArrayList<Integer>();
        while (m.find()) {
            pos.add(m.start() + 7);
        }

        System.out.println(pos);

        for(int i = 0; i < str1.length(); i++) {
            if(pos.contains(i)) {
                statusArea.appendText("[" + str1.charAt(i) + "]");
            }
            else {
                statusArea.appendText("" + str1.charAt(i));
            }
        }

    }

    @Override
    public void start(Stage stage) {

        initGUI(stage);

        String flag = "0111000";
        String newFlag = "01110001";
        String defaultStatusData = "Flag = 112(01110000)\n";

        updateStatus(defaultStatusData);

        inputField.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ENTER)) {
                String data = inputField.getText();
                if(!data.isEmpty()) {
                    if(isValid(data)) {
                        String statusData = data.replaceAll(flag, newFlag);
                        updateStatus(defaultStatusData + "Bit stuffing: ");

                        printWithBS(statusData);

                        outputField.clear();
                        outputField.appendText(statusData.replace(newFlag, flag));
                    }
                    else {
                        outputField.clear();
                        updateStatus("ERROR: only 1 and 0 are supported\n" + "Bit stuffing: " + defaultStatusData);
                    }
                }
                inputField.clear();
            }
        });

        stage.setOnCloseRequest(e -> {
            stage.close();
        });
    }
}