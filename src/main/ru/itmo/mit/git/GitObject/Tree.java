package ru.itmo.mit.git.GitObject;

import java.util.HashMap;

import static java.lang.Math.abs;

public class Tree implements GitObject{

    public final Integer hash;
    public HashMap<String, Integer> files;

    public Tree(HashMap<String, Integer> files) {
        this.files = (HashMap<String, Integer>)files.clone();
        this.hash = abs(files.hashCode());
    }
}
