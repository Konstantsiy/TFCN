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
import jssc.*;

import java.io.UnsupportedEncodingException;

public class COM extends Application {
    private static SerialPort port1; // порт для чтения
    private static final String[] ports = SerialPortList.getPortNames();
    private static TextField input;
    private static TextField output;
    private static TextArea status;

    // запись в порт
    public static void portEntry(SerialPort serialPort, final String data) {
        try {
            serialPort.openPort();
            // параметры
            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            output.clear();
            serialPort.writeString(data, "UTF-8");
            serialPort.closePort();
        } catch (SerialPortException | UnsupportedEncodingException ex) {
            System.out.println("ERROR: COM ports are not supported");
        }
    }

    // проверка на русские символы
    public static boolean check(String str) {
        for(char c : str.toCharArray()) {
            if(Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CYRILLIC) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("COM ports communication");
        stage.setHeight(300);
        Label inputLabel = new Label("Input");
        input = new TextField();
        Label outputLabel = new Label("Output");
        output = new TextField();
        Label statusLabel = new Label("Status");
        status = new TextArea();
        status.setPrefHeight(110);
        output.setEditable(false);
        status.setEditable(false);

        VBox vBox = new VBox(5, inputLabel, input, outputLabel, output, statusLabel, status);
        vBox.setPadding(new Insets(5));
        vBox.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(vBox));
        stage.setResizable(false);
        stage.show();

        SerialPort port2 = null; // порт для записи
        String statusInfo;

        if(SerialPortList.getPortNames().length > 1) { // то, что идёт в статус
            statusInfo = "Virtual connection:\t\t\t" + ports[0] + " to " + ports[1] +
                    "\nBaud rate (" + ports[0] + "/" + ports[1] + "):\t" + 9600 + "/" + 9600 +
                    "\nData bits (" + ports[0] + "/" + ports[1] + "):\t" + 8 + "/" + 8 +
                    "\nStop bits (" + ports[0] + "/" + ports[1] + "):\t" + 1 + "/" + 1 +
                    "\nParity(" + ports[0] + "/" + ports[1] + "):\t\tnone/none";
        } else {
            statusInfo = "Error of creation of virtual connection";
        }

        try {
            port1 = new SerialPort(ports[0]);
            try {
                port1.openPort();
                // параметры порта
                port1.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                int mask = SerialPort.MASK_RXCHAR;
                port1.setEventsMask(mask);
                port1.addEventListener(new SerialPortReader(port1));
            } catch (SerialPortException ex) {
                return;
            }
            port2 = new SerialPort(ports[1]);
        } catch (IndexOutOfBoundsException e) {
            status.clear();
            status.appendText(statusInfo);
        } catch (Exception e) {
            status.clear();
        }

        SerialPort finalPort = port2;

        input.setOnKeyPressed(e -> { // обработка события при нажатии ENTER
            status.clear();
            status.appendText(statusInfo);
            if(e.getCode().equals(KeyCode.ENTER)) {
                if(port1 != null && port1.isOpened()) {
                    String data = input.getText();
                    if(!data.isEmpty()) {
                        if(!check(data)) {
                            portEntry(finalPort, data + "\n");
                        }
                        else {
                            output.clear();
                            status.clear();
                            status.appendText("Russian language is not available\n" + statusInfo);
                        }
                    }
                    input.clear();
                }
            }
        });

        stage.setOnCloseRequest(e -> { // обработка события при закрытии проги
            if (port1 != null && port1.isOpened()) {
                try {
                    port1.closePort();
                } catch (SerialPortException ex) {
                    ex.printStackTrace();
                }
            }
            stage.close();
        });
    }

    static class SerialPortReader implements SerialPortEventListener {
        private final SerialPort serialPort;

        public SerialPortReader(SerialPort serialPort) {
            this.serialPort = serialPort;
        }

        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR()) {
                if (event.getEventValue() > 0) {
                    try {
                        String buffer = serialPort.readString();
                        output.appendText(buffer);
                    } catch (SerialPortException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}