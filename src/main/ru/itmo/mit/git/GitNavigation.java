package ru.itmo.mit.git;

import ru.itmo.mit.git.GitCommand.InitCommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GitNavigation {

    public static File getIndex() {
        String path = "/sas/index";
        return new File(InitCommand.initRepo + path);
    }

    public static File getHEAD() {
        String path = "/sas/HEAD";
        return new File(InitCommand.initRepo + path);
    }

    public static File getObjectFileByHash(Integer hash) {
        StringBuilder builder = new StringBuilder();
        builder.append(System.getProperty("user.dir"));
        builder.append("/sas/objects/");
        builder.append(hash.toString(), 0, 2);
        builder.append("/");
        builder.append(hash.toString(), 2, hash.toString().length());

        return new File(builder.toString());
    }

    public static File getObjectFileRelativePath(Integer hash) {
        StringBuilder builder = new StringBuilder();
        builder.append("/sas/objects/");
        builder.append(hash.toString(), 0, 2);
        builder.append("/");
        builder.append(hash.toString(), 2, hash.toString().length());

        return new File(builder.toString());
    }

    public static File getFileFullPath(String filename) {
        if (!filename.contains("/"))
            return new File(System.getProperty("user.dir") + "/" + filename);
        else {
            var pos = filename.indexOf("/");
            return new File(System.getProperty("user.dir") + filename.substring(pos));
        }
    }

    public static File findParent(String parentName) {
        StringBuilder builder = new StringBuilder();
        builder.append(InitCommand.initRepo);
        builder.append("/sas/objects/");
        builder.append(parentName, 0, 2);
        builder.append("/");
        builder.append(parentName, 2, parentName.length());

        return new File(builder.toString());
    }

    public static File findParentTree(String parentName) throws GitException {
        File parent = findParent(parentName);
        String path = InitCommand.initRepo + "/sas/objects/";
        try (BufferedReader parentReader = new BufferedReader(new FileReader(parent))) {
            String tree = parentReader.readLine();
            path = path + tree.substring(0, 2) + "/" + tree.substring(2);

        } catch (IOException e) {
            throw new GitException("Cannot open object file with reading access");
        }

        return new File(path);
    }

    public static void getAllFiles(String dirName, List<File> allFiles, HashMap<String, Integer> head) {
        File directory = new File(dirName);

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && !GitIndex.indexPos.containsKey(file.getAbsolutePath())
                    && !head.containsKey(file.getAbsolutePath())) {
                    allFiles.add(file);
                } else if (file.isDirectory() && !file.getName().equals("sas")) {
                    getAllFiles(file.getAbsolutePath(), allFiles, head);
                }
            }
        }
    }

    public static void getFilesPath(List<File> allFiles, List<String> result) {
        for (var f : allFiles) {
            var path = f.getAbsolutePath();
            result.add(GitNavigation.trim(path));
        }
    }

    private static String trim(String path) {
        var trimmed = path.split(InitCommand.initRepo);
        return trimmed[1];
    }

}
