package ru.itmo.mit.git;

import org.junit.jupiter.api.Test;
import ru.itmo.mit.git.GitCommand.InitCommand;

/*
 * Т.к. в коммитах при каждом новом запуске получаются разные хеши и
 *   разное время отправки, то в expected логах на их местах используются
 *   COMMIT_HASH и COMMIT_DATE заглушки соответственно
 */
public class GitTest extends AbstractGitTest {
    @Override
    protected GitCli createCli(String workingDir) {
//        throw new UnsupportedOperationException();
        return new GitCliImpl();
    }

    @Override
    protected TestMode testMode() {
        return TestMode.SYSTEM_OUT;
    }

//    protected TestMode testMode() {
//        return TestMode.TEST_DATA;
//    }

    @Test
    public void testAdd() throws Exception {
        createFile("file.txt", "aaa");
        status();
        // в тестах git init производится в текущей директории, а файл добавляется из playground
        add(InitCommand.initRepo + "/file.txt");
        status();
        commit("First commit");
        status();
        createFile("file1.txt", "bbb");
        status();
        log();
    }

    @Test
    public void testMultipleCommits() throws Exception {
        String file1 = "file1.txt";
        String file2 = "file2.txt";
        createFile(file1, "aaa");
        createFile(file2, "bbb");
        status();
        add(System.getProperty("user.dir") + "/" + file1);
        add(System.getProperty("user.dir") + "/" + file2);
        status();
        rm(System.getProperty("user.dir") + "/" + file2);
        status();
        commit("Add file1.txt");
        add(System.getProperty("user.dir") + "/" + file2);
        commit("Add file2.txt");
        status();
        createFile("file3.txt", "ccc");
        add(System.getProperty("user.dir") + "/file3.txt");
        commit("Add file3.txt");
        log();
    }


    @Test
    public void testReset() throws Exception {
        String file = "file.txt";
        createFile(file, "aaa");
        add(System.getProperty("user.dir") + "/" + file);
        commit("First commit");

        createFile(file, "bbb");
        add(System.getProperty("user.dir") + "/" + file);
        commit("Second commit");
        log();

        reset(1);
        fileContent(file);
        log();

        createFile(file, "ccc");
        add(System.getProperty("user.dir") + "/" + file);
        commit("Third commit");
        log();
    }
}
