package ru.itmo.mit.git.GitCommand;

import ru.itmo.mit.git.GitException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

public class CheckoutCommand implements Command{
    @Override
    public void run(List<String> args) throws GitException {
        if (!checkCommit())
            throw new GitException("Commit your changes before calling 'checkout'");

        if (args.size() > 1)
            throw new GitException("Illegal number of arguments. Should be: 0, given: " + args.size());
    }

    private boolean checkCommit() throws GitException{
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(stream);

        PrintStream prev = System.out;
        System.setOut(output);

        new StatusCommand().run(Collections.emptyList());
        System.setOut(prev);
        return stream.toString().contains("Everything up-to-date");
    }
}
