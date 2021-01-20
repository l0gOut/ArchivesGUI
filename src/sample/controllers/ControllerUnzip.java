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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
        ZipFile zipFile = new ZipFile(pathToArchive.toFile());
        for (Enumeration<? extends ZipEntry> enumeration = zipFile.entries(); enumeration.hasMoreElements();){
            list.add(enumeration.nextElement());
        }

        list.add(0, new ZipEntry(dirName + "/"));

        for (ZipEntry zipEntry: list) {
            Path path = concatPath(pathToDir, zipEntry);
            System.out.println(path);
            if (zipEntry.isDirectory() && Files.notExists(path)){
                Files.createDirectory(path);
                txtArea.setText(txtArea.getText() + "\n" + path + " Директория создана!");
            } else if (Files.notExists(path)){
                InputStream inputStream = zipFile.getInputStream(zipEntry);
                Files.copy(inputStream,path);
                txtArea.setText(txtArea.getText() + "\n" + path + " Файл создан!");
            }
        }
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

    public Path concatPath(Path pathToDir, ZipEntry zipEntry){
        return Paths.get(pathToDir + File.separator +  zipEntry.toString());
    }
}
