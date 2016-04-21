/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.shell;

import at.beris.virtualfile.File;
import at.beris.virtualfile.FileContext;
import at.beris.virtualfile.provider.operation.CopyListener;
import at.beris.virtualfile.util.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.*;

import static at.beris.virtualfile.util.UrlUtils.maskedUrlString;

public class Shell {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Shell.class);
    private final static String NOT_CONNECTED_MSG = "You're not connected. use con.";

    private FileContext fileContext;
    private File localFile;
    private File workingFile;
    private Scanner scanner;

    public Shell() throws IOException {
        fileContext = new FileContext();
        localFile = fileContext.newLocalFile(System.getProperty("user.dir"));
        scanner = new Scanner(System.in);
        scanner.useDelimiter(System.lineSeparator());
    }

    public static void main(String args[]) {
        try {
            new Shell().run();
        } catch (IOException e) {
            logException(e);
        }
    }

    public void run() {
        System.out.println("VirtualFile Shell");
        String line;
        Pair<Command, List<String>> cmd = null;

        do {
            try {
                System.out.print("> ");
                line = StringUtils.trim(scanner.next());
                cmd = parseCommandLine(line);
                if (commandArgumentsValid(cmd))
                    processCommand(cmd);
            } catch (IOException e) {
                logException(e);
                System.out.println("Error: " + e.getMessage());
            }

        } while (cmd != null && cmd.getLeft() != Command.QUIT);
    }

    private Pair<Command, List<String>> parseCommandLine(String line) {
        List<String> lineParts = new LinkedList<>(Arrays.asList(line.split(" ")));

        for (Command availCmd : Command.values()) {
            if (availCmd.toString().equals(lineParts.get(0).toUpperCase())) {
                lineParts.remove(0);
                return Pair.of(availCmd, lineParts);
            }
        }
        return Pair.of(Command.UNKNOWN, Collections.<String>emptyList());
    }

    private void processCommand(Pair<Command, List<String>> cmd) throws IOException {
        switch (cmd.getLeft()) {
            case CD:
                change(cmd.getRight().get(0), false);
                break;
            case CON:
                connect(new URL(cmd.getRight().get(0)));
                break;
            case DIS:
                disconnect();
                break;
            case GET:
                get(cmd.getRight().get(0));
                break;
            case HELP:
                displayHelp();
                break;
            case LCD:
                change(cmd.getRight().get(0), true);
                break;
            case LPWD:
                System.out.println(localFile.getPath());
                break;
            case LLS:
                list(true);
                break;
            case LRM:
                remove(true, cmd.getRight().get(0));
                break;
            case LS:
                list(false);
                break;
            case PWD:
                System.out.println(workingFile != null ? maskedUrlString(workingFile.getUrl()) : NOT_CONNECTED_MSG);
                break;
            case RM:
                remove(false, cmd.getRight().get(0));
                break;
            case STAT:
                displayStatistics();
                break;
            case QUIT:
                quit();
                break;
            default:
                System.out.println("Unknown command.");
        }
    }

    private void disconnect() {
        if (workingFile == null)
            System.out.println(NOT_CONNECTED_MSG);
        else
            workingFile = null;
    }

    private boolean commandArgumentsValid(Pair<Command, List<String>> cmd) {
        StringBuilder sb = new StringBuilder();
        Command cmdOp = cmd.getLeft();
        int[] expectedArgCounts = cmdOp.getArgumentCounts();
        List<String> actualArgs = cmd.getRight();

        sb.append("Wrong number of arguments for Command ").append(cmdOp.toString().toLowerCase()).append(". Expected ");

        boolean argCountMatches = false;
        for (int expectedArgCount : expectedArgCounts) {
            if (expectedArgCount == actualArgs.size())
                argCountMatches = true;
            sb.append(expectedArgCount).append(" or ");
        }
        sb.delete(sb.length() - 4, sb.length()).append(".");

        if (!argCountMatches)
            System.out.println(sb.toString());

        return argCountMatches;
    }

    private void displayHelp() {
        String helpFormatStr = "%-10s - %s";
        for (Command cmd : Command.values()) {
            if (cmd == Command.UNKNOWN)
                continue;
            System.out.println(String.format(helpFormatStr, cmd.toString().toLowerCase(), cmd.getDescription()));
        }
    }

    private void displayStatistics() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("** Heap utilization statistics [KB] **");
        System.out.println(String.format("Used Memory: %,d", (runtime.totalMemory() - runtime.freeMemory()) / 1024));
        System.out.println(String.format("Free Memory: %,d", runtime.freeMemory() / 1024));
        System.out.println(String.format("Total Memory: %,d", runtime.totalMemory() / 1024));
        System.out.println(String.format("Max Memory: %,d", runtime.maxMemory() / 1024));
    }

    private void quit() throws IOException {
        fileContext.dispose();
    }

    private void connect(URL url) throws IOException {
        workingFile = fileContext.newFile(url);
    }

    private void list(boolean local) throws IOException {
        File file = local ? localFile : workingFile;

        if (file == null && !local) {
            System.out.println(NOT_CONNECTED_MSG);
            return;
        }
        for (File childFile : file.list()) {
            System.out.println(String.format("%-20s %d kb %s", childFile.getName(), childFile.getSize() / 1024, childFile.isDirectory() ? "<DIR>" : ""));
        }
    }

    private void change(String directoryName, boolean local) throws IOException {
        if (!local && workingFile == null) {
            System.out.println(NOT_CONNECTED_MSG);
            return;
        }

        File file = local ? localFile : workingFile;
        URL newUrl = UrlUtils.normalizeUrl(UrlUtils.newUrl(file.getUrl().toString() + directoryName + (directoryName.endsWith("/") ? "" : "/")));
        File actionFile = fileContext.newFile(newUrl);

        if (actionFile.exists()) {
            if (local)
                localFile = fileContext.newFile(newUrl);
            else
                workingFile = fileContext.newFile(newUrl);
        } else
            throw new NoSuchFileException(actionFile.getUrl().toString());
    }

    private void get(String fileName) throws IOException {
        if (workingFile == null) {
            System.out.println(NOT_CONNECTED_MSG);
            return;
        }

        URL actionUrl = UrlUtils.newUrl(workingFile.getUrl(), fileName);
        File actionFile = fileContext.newFile(actionUrl);
        actionFile.copy(localFile, new CustomCopyListener());
        System.out.println("");
    }

    private void remove(boolean local, String fileName) throws IOException {
        if (!local && workingFile == null) {
            System.out.println(NOT_CONNECTED_MSG);
            return;
        }

        URL actionUrl = UrlUtils.newUrl((local ? localFile : workingFile).getUrl(), fileName);
        fileContext.newFile(actionUrl).delete();
    }

    private static void logException(Exception e) {
        LOGGER.error("Exception occured.", e);
    }

    private class CustomCopyListener implements CopyListener {

        @Override
        public void startFile(File file, long currentFileNumber) {
            try {
                System.out.println("Copying " + file.getPath() + "     ");
            } catch (IOException e) {
                logException(e);
            }
        }

        @Override
        public void finishedFile(File file) {
            System.out.println("");
        }

        @Override
        public void afterBlockCopied(long fileSize, long bytesCopiedBlock, long bytesCopiedTotal) {
            long percent = bytesCopiedTotal * 100 / fileSize;
            if (percent % 4 == 0)
                System.out.print("*");
        }

        @Override
        public boolean interrupt() {
            return false;
        }

        @Override
        public boolean fileExists(File file) {
            try {
                System.out.println(file.getPath() + " already exists. Overwrite? (y/n)");
                String next = scanner.next();
                if ("y".equals(next.toLowerCase())) {
                    file.delete();
                    return true;
                }
            } catch (IOException e) {
                logException(e);
            }
            return false;
        }
    }
}
