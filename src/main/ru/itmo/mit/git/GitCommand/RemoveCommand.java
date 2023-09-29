package ru.itmo.mit.git.GitCommand;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.GitIndex;

import java.util.List;

public class RemoveCommand implements Command{

    @Override
    public void run(List<String> args) throws GitException {
        for (var arg : args) {
            deleteFromIndex(arg);
        }
    }

    private void deleteFromIndex(String filename) throws GitException{

        if (GitIndex.indexPos.get(filename) != null || !GitIndex.indexPos.containsKey(filename)) {
            GitIndex.indexPos.put(filename, null);
            return;
        }
        throw new GitException("No such file to delete from index");
    }
}
