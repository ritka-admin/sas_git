package ru.itmo.mit.git;

import java.io.*;
import java.util.HashMap;

public class GitIndex implements Serializable {
    // Из имя в хеш контента
    public static HashMap<String, Integer> indexPos;

    static {
        // deserialization from file
        File index = GitNavigation.getIndex();
        if (index.exists()) {
            try (FileInputStream indexInput = new FileInputStream(index);
                ObjectInputStream indexObj = new ObjectInputStream(indexInput)) {

                indexPos = (HashMap)indexObj.readObject();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            indexPos = new HashMap<>();
            try {
                index.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void finishAction() {

        try (FileOutputStream outFile = new FileOutputStream(GitNavigation.getIndex());
            ObjectOutputStream objOut = new ObjectOutputStream(outFile)) {

            objOut.writeObject(indexPos);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
