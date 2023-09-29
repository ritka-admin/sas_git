package ru.itmo.mit.git.GitCommand;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.GitNavigation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogCommand implements Command {

    @Override
    public void run(List<String> args) throws GitException {
        File current = GitNavigation.getHEAD();

        if (current.length() == 0) {
            System.out.println("No commits here yet");
            return;
        }

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

        printCommitLog(result, hash);

        while (!Objects.equals(result.get(1), "null")) {
            current = GitNavigation.getObjectFileByHash(Integer.parseInt(result.get(1)));
            try (Stream<String> stream = Files.lines(Path.of(current.getAbsolutePath()))) {
                result = stream.collect(Collectors.toList());
                var hashDir = new File(current.getParent()).getName();
                hash = hashDir + current.getName();
                printCommitLog(result, hash);
            } catch (IOException e) {
                throw new GitException(e);
            }
        }
    }

    private void printCommitLog(List<String> result, String commitHash) {
        StringBuilder builder = new StringBuilder();
        builder.append("Commit hash: ");
        builder.append(commitHash);
        builder.append("\n");
        builder.append("Commit author: ");
        builder.append(result.get(2));
        builder.append("\n");
        builder.append("Commit message: ");
        builder.append(result.get(3));
        builder.append("\n");

        System.out.println(builder);
    }
}
