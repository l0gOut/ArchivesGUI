package sample.Models;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class MyFileVisitor extends SimpleFileVisitor<Path> {

    private final List<File> fileList = new ArrayList<>();

    public List<File> getFileList() {
        return fileList;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes atr){
        fileList.add(path.toFile());
        return FileVisitResult.CONTINUE;
    }
    public FileVisitResult visitFile (Path path, BasicFileAttributes atr){
        fileList.add(path.toFile());
        return FileVisitResult.CONTINUE;
    }
}
