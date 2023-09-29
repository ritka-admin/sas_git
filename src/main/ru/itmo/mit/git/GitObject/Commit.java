package ru.itmo.mit.git.GitObject;

import ru.itmo.mit.git.GitIndex;

import static java.lang.Math.abs;

public class Commit {
    public final String author;
    public final String commitMessage;
    public Integer parent; // хеш родительского коммита
    public final Integer hash;
    public final Tree tree;

    public Commit(String author, String commitMessage) {
        this.author = author;
        this.commitMessage = commitMessage;

        tree = new Tree(GitIndex.indexPos);
//        tree = GitIndex.indexPos == null ? new Tree(new HashMap<>()) : new Tree(GitIndex.indexPos);
        hash = abs((author + commitMessage + tree.hash).hashCode());
    }
}
