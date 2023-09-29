package ru.itmo.mit.git;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main (String[] args) {
        List<String> argsL = new ArrayList<>(List.of(args));
        GitCliImpl git = new GitCliImpl();

        repo();
        fileArgs(argsL);

        try {
            git.runCommand(argsL.get(0), argsL.subList(1, argsL.size()));
        } catch (GitException e) {
            e.printStackTrace();
        }
    }

    private static void repo() {
        var initRepo = System.getenv("INIT_REPO");
        System.setProperty("user.dir", initRepo); // how to remember git repo init dir
    }

    private static void fileArgs(List<String> argsL) {
        if (Objects.equals(argsL.get(0), GitConstants.ADD) || Objects.equals(argsL.get(0), GitConstants.RM)) {

            for (int i = 1; i < argsL.size(); i++) {
                if (!argsL.get(i).contains(System.getProperty("user.dir")))
                    argsL.set(i, System.getProperty("user.dir") + "/" + argsL.get(i));
            }
        }
    }
}

