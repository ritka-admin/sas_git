package ru.itmo.mit.git.GitCommand;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.GitIndex;
import ru.itmo.mit.git.GitNavigation;
import ru.itmo.mit.git.GitObject.Blob;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class AddCommand implements Command{

    @Override
    public void run(List<String> args) throws GitException {
        if (args.isEmpty())
            throw new GitException("Git add command requires arguments");

        for (var arg : args) {
            var hash = addToObjectsDir(arg);
            addToIndex(arg, hash);
        }
    }

    private int addToObjectsDir(String filename) throws GitException {
        File file = new File(filename);

        if (!file.exists()) {
            throw new GitException("Cannot find the provided file");
        }

        Blob blob = new Blob(Path.of(filename));
        String dirName = System.getProperty("user.dir") + "/sas/objects/" + blob.hash.toString().substring(0, 2);
        File newFile = new File(dirName);
        newFile.mkdir();
        try (FileWriter blobWriter = new FileWriter(GitNavigation.getObjectFileByHash(blob.hash));
             FileReader fileReader = new FileReader(file)) {

            var byt = fileReader.read();
            while(byt != -1) {
                blobWriter.write(byt);
                byt = fileReader.read();
            }

        } catch (IOException e) {
            throw new GitException(e);
        }
        return blob.hash;
    }

    private void addToIndex (String pathToFile, int hash) {
        GitIndex.indexPos.put(pathToFile, hash);
    }
}
