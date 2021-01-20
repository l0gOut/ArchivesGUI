package sample.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sample.Models.MyFileVisitor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ControllerArchive {

    MyFileVisitor myFileVisitor = new MyFileVisitor();

    @FXML
    public Pane pane;
    @FXML
    public TextArea txtArea;
    @FXML
    public TextField txtPathToFiles;
    @FXML
    public TextField txtPathArchive;
    @FXML
    public TextField txtArchiveName;
    @FXML
    public Text label;

    @FXML
    public void buttonPathToFiles(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("C:/"));
        txtPathToFiles.setText(directoryChooser.showDialog(new Stage()).getAbsolutePath());
    }
    @FXML
    public void buttonPathArchive(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("C:/"));
        txtPathArchive.setText(directoryChooser.showDialog(new Stage()).getAbsolutePath());
    }
    @FXML
    public void buttonArchive() throws Exception {
        if(txtPathToFiles.getText().isEmpty()){
            label.setText("Введите путь для архивации!");
            return;
        }
        if (txtPathArchive.getText().isEmpty()){
            label.setText("Введите путь к архиву!");
            return;
        }
        txtArea.setText("");
        label.setText("");
        String pathToFiles = txtPathToFiles.getText();
        String archiveName = txtArchiveName.getText();
        if (archiveName.isEmpty()) {
            archiveName = "archive.zip";
        } else archiveName += ".zip";
        String pathArchive = txtPathArchive.getText() + File.separator + archiveName;
        zipArchive(pathToFiles, pathArchive);
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
    public String subPath (File sourcePath, File file){
        int subPathLength = sourcePath.getParent().length() + 1;
        return file.toString().substring(subPathLength);
    }
    public void zipArchive(String pathToFile,String pathArchive) throws Exception {
        double sum = 0;
        Path pathSource = Paths.get(pathToFile);
        Files.walkFileTree(pathSource, myFileVisitor);
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(pathArchive));
        for (File file: myFileVisitor.getFileList()){
            String archiveEnd = subPath(pathSource.toFile(), file);
            if (file.isDirectory()){
                ZipEntry zipEntry = new ZipEntry(archiveEnd + "/");
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.closeEntry();
                txtArea.setText(String.format("%s \n %-40s Директория архивирована успешно!",txtArea.getText(),file.getAbsolutePath()));
            }
            if (file.isFile()){
                ZipEntry zipEntry = new ZipEntry(archiveEnd);
                zipOutputStream.putNextEntry(zipEntry);
                Files.copy(file.toPath(), zipOutputStream);
                zipOutputStream.closeEntry();
                long size = zipEntry.getSize();
                long sizeComp = zipEntry.getCompressedSize();
                sum += (double) sizeComp/1024;
                txtArea.setText(String.format("%s \n %-40s Файл архивирован успешно! %d (%d) ",txtArea.getText(),file.getAbsolutePath(),size,sizeComp));
            }
        }
        label.setText(String.format("Общий размер архива %.0f КБ",  sum));
        zipOutputStream.close();
    }
}
