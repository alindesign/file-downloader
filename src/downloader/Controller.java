package downloader;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TextField addressField;
    public Button downloadButton;
    public TextArea contentText;
    public Button saveButton;

    private String content = "";
    private FileChooser fileChooser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addressField.setText("http://inf.ucv.ro");

        addressField.textProperty().addListener((e) -> this.content = "");
        downloadButton.onActionProperty().setValue((e) -> {
            try {
                this.downloadAction();
            } catch (IOException ex) {
                this.alert(Alert.AlertType.ERROR, "Request error!", ex.getMessage());

                ex.printStackTrace();
            }
        });

        saveButton.onActionProperty().setValue((e) -> {
            try {
                this.save();
            } catch (IOException ex) {
                this.alert(Alert.AlertType.ERROR, "Save error!", ex.getMessage());
                ex.printStackTrace();
            }
        });

        this.fileChooser = new FileChooser();
    }

    private void alert(Alert.AlertType t, String s, String message) {
        Alert alert = new Alert(t);
        alert.setTitle(s);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void save() throws IOException {
        if (addressField.getText().equals("")) {
            this.alert(Alert.AlertType.WARNING, "Validation error!", "The address isn't specified!");
        } else {
            if (this.content.equals("")) {
                this.downloadAction();
            }

            fileChooser.setTitle("Save file");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.setInitialFileName("index.html");

            this.write(fileChooser.showSaveDialog(this.downloadButton.getScene().getWindow()));
        }
    }

    private void write(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(this.content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadAction() throws IOException {
        this.content = addressField.getText();

        String url = this.addressField.getText();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(String.format("%s\n", inputLine));
        }

        in.close();

        this.content = response.toString().trim();

        this.fillContent();
    }

    private void fillContent() {
        contentText.setText(this.content);
    }
}
