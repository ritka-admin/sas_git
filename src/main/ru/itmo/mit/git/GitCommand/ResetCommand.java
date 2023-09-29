package ru.itmo.mit.git.GitCommand;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.GitIndex;
import ru.itmo.mit.git.GitNavigation;

import java.io.*;
import java.util.List;

public class ResetCommand implements Command {
    @Override
    public void run(List<String> args) throws GitException {
        if (args.size() > 1)
            throw new GitException("Illegal number of arguments. Should be: 0, given: " + args.size());

        File obj = GitNavigation.getObjectFileByHash(Integer.parseInt(args.get(0)));
        File head = GitNavigation.getHEAD();
        try (BufferedReader reader = new BufferedReader(new FileReader(obj));
            FileWriter headWriter = new FileWriter(head)) {
            String tree = reader.readLine();
            // TODO: extract deserialization
            GitIndex.indexPos = StatusCommand.deserializeCommit(GitNavigation.getObjectFileByHash(Integer.parseInt(tree)));
            headWriter.write(args.get(0));
        } catch (IOException e) {
            throw new GitException(e);
        }

        for (var f : GitIndex.indexPos.keySet()) {
            File resetGoal = GitNavigation.getObjectFileByHash(GitIndex.indexPos.get(f));
            File currVersion = new File(f);

            try (FileWriter currWriter = new FileWriter(currVersion);
                FileReader resetReader = new FileReader(resetGoal)) {

                var byt = resetReader.read();
                while(byt != -1) {
                    currWriter.write(byt);
                    byt = resetReader.read();
                }

            } catch (IOException e) {
                throw new GitException(e);
            }
        }
    }
}
