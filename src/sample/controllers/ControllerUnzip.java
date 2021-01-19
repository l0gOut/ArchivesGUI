package sample.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ControllerUnzip {

    @FXML
    public Pane pane;
    @FXML
    public TextArea txtArea;
    @FXML
    public TextField txtPathToArchive;
    @FXML
    public TextField txtPathToDir;
    @FXML
    public TextField txtDirName;

    @FXML
    public void buttonArchive(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите путь к архиву");
        fileChooser.setInitialDirectory(new File("C:/archive"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip Files,","*.zip","rar"));
        txtPathToArchive.setText(fileChooser.showOpenDialog(new Stage()).getAbsolutePath());
    }

    @FXML
    public void buttonDir(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("C:/"));
        directoryChooser.setTitle("Выберите куда разорхивировать");
        txtPathToDir.setText(directoryChooser.showDialog(new Stage()).getAbsolutePath());
    }

    @FXML
    public void unzip() throws IOException {
        List<ZipEntry> list = new ArrayList<>();
        Path pathToArchive = Paths.get(txtPathToArchive.getText());
        Path pathToDir = Paths.get(txtPathToDir.getText());
        String dirName = txtDirName.getText();
        if (dirName.isEmpty()){
            int length = pathToArchive.getFileName().toString().length() - 4;
            dirName = pathToArchive.getFileName().toString().substring(0,length);
        }
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(pathToArchive.toString()));
        while (zipInputStream.available() > 0){
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            if (zipEntry == null) break;
            list.add(zipEntry);
        }
        if (Files.notExists(Paths.get(pathToDir + File.separator + dirName))){
            Files.createDirectory(Paths.get(pathToDir + File.separator + dirName));
        }
        for (ZipEntry file: list){
            Path path = Paths.get(pathToDir + File.separator + dirName + File.separator + file.toString());
            if (file.isDirectory() && Files.notExists(path)){
                Files.createDirectory(path);
                txtArea.setText(txtArea.getText() + "\n" + path + "\t Директория создана!");
                continue;
            }
            if (Files.notExists(path)){
                Files.createFile(path);
                txtArea.setText(txtArea.getText() + "\n" + path + "\t Файл создан!");
                Files.copy(zipInputStream, path, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        zipInputStream.closeEntry();
        zipInputStream.close();
    }

    @FXML
    public void back() throws IOException {
        pane.getScene().getWindow().hide();
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/sample/fxml/choser.fxml"));
        primaryStage.setTitle("Выборка");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}
