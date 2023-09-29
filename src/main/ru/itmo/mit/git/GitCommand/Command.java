package ru.itmo.mit.git.GitCommand;

import ru.itmo.mit.git.GitException;

import java.util.List;

public interface Command {

    void run(List<String> args) throws GitException;
}
