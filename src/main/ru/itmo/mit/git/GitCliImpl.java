package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitCommand.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitCliImpl implements GitCli {

    @Override
    public void runCommand(@NotNull String command, @NotNull List<@NotNull String> arguments) throws GitException {
        switch (command) {
            case GitConstants.INIT: {
                var com = new InitCommand();
                com.run(arguments);
                System.out.println("Project initialized");
                break;
            }
            case GitConstants.ADD: {
                var com = new AddCommand();
                com.run(arguments);
                System.out.println("Add completed successfully");
                break;
            }
            case GitConstants.RM: {
                var com = new RemoveCommand();
                com.run(arguments);
                System.out.println("Remove completed successfully");
                break;
            }
            case GitConstants.COMMIT: {
                var com = new CommitCommand(arguments);
                com.run(arguments);
                break;
            }
            case GitConstants.LOG: {
                var com = new LogCommand();
                com.run(arguments);
                break;
            }
            case GitConstants.STATUS: {
                var com = new StatusCommand();
                com.run(arguments);
                break;
            }
            case GitConstants.RESET: {
                var com = new ResetCommand();
                com.run(arguments);
                System.out.println("Reset completed successfully");
            }
        }
        GitIndex.finishAction();
    }

    @Override
    public void setOutputStream(@NotNull PrintStream outputStream) {
        System.setOut(outputStream);
    }

    @Override
    public @NotNull String getRelativeRevisionFromHead(int n) throws GitException {
        File current = GitNavigation.getHEAD();
        List<String> result;
        String hash;
        try (BufferedReader currReader = new BufferedReader(new FileReader(current.getAbsolutePath()));
             Stream<String> stream = Files.lines(Path.of(GitNavigation.getObjectFileByHash(Integer.parseInt(currReader.readLine())).toURI()))) {
            result = stream.collect(Collectors.toList());
            var currReader0 = new BufferedReader(new FileReader(current.getAbsolutePath()));
            hash = currReader0.readLine();
        } catch (IOException e) {
            throw new GitException(e);
        }

        for (int i = 0; i < n; i++) {
            current = GitNavigation.getObjectFileByHash(Integer.parseInt(result.get(1)));
            try (Stream<String> stream = Files.lines(Path.of(current.getAbsolutePath()))) {
                result = stream.collect(Collectors.toList());
                var hashDir = new File(current.getParent()).getName();
                hash = hashDir + current.getName();
            } catch (IOException e) {
                throw new GitException(e);
            }
        }

        return hash;
    }
}