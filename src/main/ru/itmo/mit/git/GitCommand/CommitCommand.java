package ru.itmo.mit.git.GitCommand;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.GitIndex;
import ru.itmo.mit.git.GitNavigation;
import ru.itmo.mit.git.GitObject.Blob;
import ru.itmo.mit.git.GitObject.Commit;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CommitCommand implements Command {

    public final Commit commit;

    public CommitCommand(List<String> arguments) {
        commit = new Commit(System.getProperty("user.name"), arguments.get(0));
    }

    @Override
    public void run(List<String> args) throws GitException {
        if (args.size() > 1)
            throw new GitException("Illegal number of arguments. Should be: 1, given: "
                    + args.size());

        if (gatherCommit()) {
            createCommit();
            System.out.println("Files committed");
        }
        else
            System.out.println("Index is empty, nothing to commit here");
    }

    private boolean gatherCommit() throws GitException {
        // take commit from head as a parent (if head is not empty) and add modified files
        File head = GitNavigation.getHEAD();
        boolean changed = false;
        if (head.length() != 0) {

            try (BufferedReader headContent = new BufferedReader(new FileReader(head))) {
                String parentName = headContent.readLine();
                commit.parent = Integer.parseInt(parentName);
                File tree = GitNavigation.findParentTree(parentName);
                deserializeCommit(tree);

                // в files уже лежат файлы из прошлого коммита
                List<String> toDelete = new ArrayList<>();
                for (var file : commit.tree.files.entrySet()) {

                    Blob blob = new Blob(Path.of(file.getKey()));
                    if (!Objects.equals(blob.hash, file.getValue())) {
                        changed = true;

                        if (file.getValue() == null) {
                            toDelete.add(file.getKey());
                            continue;
                        }
                        commit.tree.files.put(file.getKey(), blob.hash);
                    }
                }

                for (var t : toDelete) {
                    commit.tree.files.remove(t);
                }

            } catch (IOException e) {
                throw new GitException(e);
            }
        }
        if (GitIndex.indexPos.isEmpty() && !changed) {
            return false;
        }

        File index = GitNavigation.getIndex();

        try  {
            new FileWriter(index, false).close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return !commit.tree.files.isEmpty();
    }

    private void createCommit() throws GitException {
        String dirNameCommit = System.getProperty("user.dir") + "/sas/objects/" + this.commit.hash.toString().substring(0, 2);
        File newDirCommit = new File(dirNameCommit);
        newDirCommit.mkdir();

//        Tree tree = new Tree(GitIndex.indexPos);
//        Tree tree = new Tree(commit.tree.files);
        String dirNameTree = System.getProperty("user.dir") + "/sas/objects/" + commit.tree.hash.toString().substring(0, 2);
        File newFileTree = new File(dirNameTree);
        newFileTree.mkdir();

        GitIndex.indexPos.clear();

        try (FileWriter commitWriter = new FileWriter(GitNavigation.getObjectFileByHash(commit.hash));
             FileOutputStream outFileTree = new FileOutputStream(GitNavigation.getObjectFileByHash(commit.tree.hash));
             ObjectOutputStream objOut = new ObjectOutputStream(outFileTree)) {

            // write tree hash to commit file
            commitWriter.write(commit.tree.hash + "\n" + commit.parent + "\n" + commit.author + "\n" + commit.commitMessage);

            // serialize hashmap to tree file
            objOut.writeObject(commit.tree.files);

        } catch (IOException e) {
            throw new GitException(e);
        }

        try (FileWriter writerHead = new FileWriter(GitNavigation.getHEAD())) {
            writerHead.write(commit.hash.toString());
        } catch (IOException e) {
            throw new GitException(e);
        }
    }

    public void deserializeCommit(File tree) throws GitException {
        try (FileInputStream treeInput = new FileInputStream(tree);
             ObjectInputStream objInput = new ObjectInputStream(treeInput)) {

            commit.tree.files = (HashMap<String, Integer>) objInput.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new GitException(e);
        }
    }
}
