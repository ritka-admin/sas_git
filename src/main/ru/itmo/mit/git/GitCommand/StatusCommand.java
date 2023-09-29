package ru.itmo.mit.git.GitCommand;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.GitIndex;
import ru.itmo.mit.git.GitNavigation;
import ru.itmo.mit.git.GitObject.Blob;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StatusCommand implements Command {
    @Override
    public void run(List<String> args) throws GitException {
        if (!args.isEmpty())
            throw new GitException("Illegal number of arguments. Should be: 0, provided: " + args.size());

        List<String> newFiles = new ArrayList<>();
        List<String> modifiedFiles = new ArrayList<>();
        List<String> deletedFiles = new ArrayList<>();
        for (var item : GitIndex.indexPos.entrySet()) {
            File file = new File(item.getKey());
            if (item.getValue() == null) {
                deletedFiles.add(file.getName());
            } else {
                Blob blob = new Blob(file.toPath());
                if (!Objects.equals(GitIndex.indexPos.get(item.getKey()), blob.hash)) {
                    modifiedFiles.add(file.getName());
                    continue;
                }
                newFiles.add(file.getName());
            }
        }

        // get untracked files
        HashMap<String, Integer> head = getHeadIndex();
        List<File> allFiles = new ArrayList<>();
        GitNavigation.getAllFiles(InitCommand.initRepo, allFiles, head);
        List<String> resultUntracked = new ArrayList<>();
        GitNavigation.getFilesPath(allFiles, resultUntracked);

        if (newFiles.isEmpty() && deletedFiles.isEmpty() && modifiedFiles.isEmpty() && resultUntracked.isEmpty()) {
            System.out.println("Everything up-to-date");
            return;
        }

        if (!newFiles.isEmpty()) {
            System.out.println("New files:");
            for (var n : newFiles) {
                System.out.println("\t" + n);
            }
        }

        if (!modifiedFiles.isEmpty()) {
            System.out.println("Modified files:");
            for (var m : modifiedFiles) {
                System.out.println("\t" + m);
            }
        }

        if (!deletedFiles.isEmpty()) {
            System.out.println("Deleted files:");
            for (var d : deletedFiles) {
                System.out.println("\t" + d);
            }
        }

        if (!resultUntracked.isEmpty()) {
            System.out.println("Untracked files:");
            for (var r : resultUntracked) {
                System.out.println("\t" + r);
            }
        }
    }

    private HashMap<String, Integer> getHeadIndex() throws GitException {
        File head = GitNavigation.getHEAD();
        HashMap<String, Integer> res = new HashMap<>();
        try (BufferedReader headContent = new BufferedReader(new FileReader(head))) {
            String parentName = headContent.readLine();
            if (parentName == null) return res;
            File tree = GitNavigation.findParentTree(parentName);
            res = StatusCommand.deserializeCommit(tree);
        } catch (IOException | GitException e) {
            throw new GitException(e);
        }
        return res;
    }

    public static HashMap<String, Integer> deserializeCommit(File tree) throws GitException {
        HashMap<String, Integer> res;
        try (FileInputStream treeInput = new FileInputStream(tree);
             ObjectInputStream objInput = new ObjectInputStream(treeInput)) {

            res = (HashMap<String, Integer>) objInput.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new GitException(e);
        }
        return res;
    }
}
