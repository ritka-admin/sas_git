package ru.itmo.mit.git.GitObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static java.lang.Math.abs;

public class Blob implements GitObject {

    public Integer hash;

    public Blob(Path path) {
        try {
            byte[] arr = Files.readAllBytes(path);
            hash = abs(Arrays.hashCode(arr));
        } catch (IOException e) {
            System.err.println("Unlucky");
        }
    }
}
