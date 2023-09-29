package ru.itmo.mit.git.GitCommand;

import ru.itmo.mit.git.GitException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class InitCommand implements Command {

    public static final String initRepo;

    static { initRepo = System.getProperty("user.dir"); }

    @Override
    public void run(List<String> args) throws GitException {

//        initRepo = System.getProperty("user.dir");

        if (!args.isEmpty())
            throw new GitException("Illegal number of arguments. Should be: 0, given: " +
                    args.size());

        File gitDir = new File(System.getProperty("user.dir") + "/sas");

        if (gitDir.exists())
            throw new GitException("Git repo has already been initialized here");

        gitDir.mkdir();

        File objects = new File(System.getProperty("user.dir") + "/sas/objects");
        objects.mkdir();

        File head = new File(System.getProperty("user.dir") + "/sas/HEAD");
        try {
            head.createNewFile();
        } catch (IOException e) {
            throw new GitException(e);
        }
    }
}
