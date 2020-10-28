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


public class HammingCode extends Application {
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
        stage.setTitle("Lab3: Hamming code");
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

    static public void pushing(String str) {
        if(str.length() == 20) statusArea.appendText(printHC(coding(str).toCharArray()) + "\n");
        if(str.length() > 20) {
            statusArea.appendText(printHC(coding(str.substring(0, 20)).toCharArray()) + "\n");
            pushing(str.substring(20, str.length()));
        }
        if(str.length() < 20) {
            StringBuilder s = new StringBuilder();
            s.append(str);
            for(int i = 0 ; i < 20 - str.length(); i++) {
                s.append("0");
            }
            statusArea.appendText(printHC(coding(s.toString()).toCharArray()));
        }
    }

    public static String coding(String str) { // 20

        char[] data_str = str.toCharArray();
        char[] bits_new = new char[5];
        int counter_new = 0;
        char[] data = new char[data_str.length + 5];
        boolean flag0;

        for(int i1 = 0, i2 = 0; i1 < data.length && i2 < data_str.length; i1++) {
            flag0 = false;
            for(int j = 0; j <= 4; j++) {
                if(i1 + 1 == Math.pow(2, j)) {
                    flag0 = true;
                    data[i1] = '0';
                    break;
                }
            }
            if(!flag0) data[i1] = data_str[i2++];
        }

        for (int i = 0; i < 24; i++) {
            for(int j = 0; j <= 4; j++) {
                if(i + 1 == Math.pow(2, j)) {
                    int count_1 = 0;
                    for(int pos = i, d = 1; pos < 25;) {
                        if(data[pos] == '1') count_1++;
                        if(d + 1 == i + 2) {
                            pos += i + 2;
                            d = 1;
                            continue;
                        }
                        pos++;
                        d++;
                    }
                    if (count_1 % 2 == 1) {
                        bits_new[counter_new] = '1';
                    }
                    else {
                        bits_new[counter_new] = '0';
                    }
                    counter_new++;
                }
            }
        }

        for(int i = 0; i <= 4; i++) {
            data[(int)Math.pow(2, i) - 1] = bits_new[i];
        }
        return new String(data); // 25
    }

    public static void popping() {
        String statusData = statusArea.getText().replaceAll("[^0-9\n]", "");
        outputField.clear();
        String[] status = statusData.split("\n");
        for(int i = 1; i < status.length; i++) {
            outputField.appendText(decoding(status[i]));
        }
    }

    public static void reduceArray(char[] array1, char[] array2) {
        for(int j = 0, k = 0; k < 25; k++) { // 25 -> 20
            if(k != 0 && k != 1 && k != 3 && k != 7 && k != 15) {
                array1[j++] = array2[k];
            }
        }
    }

    public static String decoding(String str) { // 25
        char[] str_bits = str.toCharArray();
        char[] newStr = new char[20]; // 20

        reduceArray(newStr, str_bits); // 25 -> 20
//        System.out.println(Arrays.toString(newStr) + "\t\t\t\t\tbits");

//        if(newStr[13] == '0') newStr[13] = '1';
//        else newStr[13] = '0';

        char[] codingData = str.toCharArray(); // 25
        char[] newCodingData = coding(new String(newStr)).toCharArray(); // 25

//        System.out.println(Arrays.toString(codingData) + "\t\tcoding bits");
//        System.out.println(Arrays.toString(newStr) + "\t\t\t\t\tincorrect bits");
//        System.out.println(Arrays.toString(newCodingData) + "\t\tincorrect coding bits");

        int i = 0;
        boolean flag = false;
        for(int j = 0; j <= 4; j++) {
            int pos = (int)Math.pow(2, j);
            if(newCodingData[pos - 1] != codingData[pos - 1]) {
                flag = true;
                i += pos;
            }
        }
        if(flag) {
            if(newCodingData[i - 1] == '0') newCodingData[i - 1] = '1';
            else newCodingData[i - 1] = '0';
        }
        char[] result = new char[20];
        reduceArray(result, newCodingData); //25 -> 20
//        System.out.println(Arrays.toString(result) + "\t\t\t\t\tbits");
//        System.out.println("----------------------------------------");
        return new String(result);
    }

    public static String printHC(char[] str) {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < str.length; i++) {
            if(i == 0 || i == 1 || i == 3 || i == 7 || i == 15) {
                s.append('[');
                s.append(str[i]);
                s.append(']');
            }
            else s.append(str[i]);
        }
        return s.toString();
    }

    @Override
    public void start(Stage stage) {

        initGUI(stage);

        updateStatus("Hamming code: ");

        inputField.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ENTER)) {
                String str = inputField.getText();
                if(!str.isEmpty()) {
                    if(isValid(str)) {
                        updateStatus("Hamming code:\n");
                        pushing(str);
                        popping();
                    }
                    else {
                        outputField.clear();
                        updateStatus("ERROR: only 1 and 0 are supported\n" + "Hamming code: ");
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