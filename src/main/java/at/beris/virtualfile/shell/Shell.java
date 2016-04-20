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
import at.beris.virtualfile.util.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static at.beris.virtualfile.util.UrlUtils.maskedUrlString;

public class Shell {
    private final static String NO_WORKING_FILE_MSG = "No working file. use con.";

    private FileContext fileContext;
    private File localFile;
    private File workingFile;

    public Shell() throws IOException {
        fileContext = new FileContext();
        localFile = fileContext.newLocalFile(System.getProperty("user.dir"));
    }

    public void run() throws IOException {
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter(System.lineSeparator());

        System.out.println("VirtualFile Shell");
        String line;
        Pair<Command, List<String>> cmd = null;

        do {
            try {
                System.out.print("> ");
                line = StringUtils.trim(scanner.next());
                cmd = parseCommandLine(line);
                processCommand(cmd);
            } catch (IOException e) {
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
        return Pair.of(Command.EMPTY, Collections.<String>emptyList());
    }

    private void processCommand(Pair<Command, List<String>> cmd) throws IOException {
        switch (cmd.getLeft()) {
            case CD:
                change(cmd.getRight().get(0), false);
                break;
            case CON:
                connect(new URL(cmd.getRight().get(0)));
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
            case LS:
                list(false);
                break;
            case PWD:
                System.out.println(workingFile != null ? maskedUrlString(workingFile.getUrl()) : NO_WORKING_FILE_MSG);
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

    private void displayHelp() {
        String helpFormatStr = "%-10s - %s";
        for (Command cmd : Command.values()) {
            if (cmd == Command.EMPTY)
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
            System.out.println(NO_WORKING_FILE_MSG);
            return;
        }
        for (File childFile : file.list()) {
            System.out.println(String.format("%-20s %d kb %s", childFile.getName(), childFile.getSize() / 1024, childFile.isDirectory() ? "<DIR>" : ""));
        }
    }

    private void change(String directoryName, boolean local) throws IOException {
        if (!local && workingFile == null) {
            System.out.println(NO_WORKING_FILE_MSG);
            return;
        }

        File file = local ? localFile : workingFile;
        Deque<String> pathParts = new LinkedList<>(Arrays.asList(file.getPath().split("/")));

        if (file.getPath().equals("/"))
            pathParts.add("");

        if (directoryName.equals(".."))
            pathParts.pollLast();
        else if (directoryName.startsWith("/")) {
            pathParts.clear();
            pathParts.add(directoryName);
        } else
            pathParts.add(directoryName);

        String newpath = StringUtils.join(pathParts.toArray(), "/") + (directoryName.endsWith("/") ? "" : "/");

        if (local)
            localFile = fileContext.newFile(UrlUtils.newUrlReplacePath(localFile.getUrl(), newpath));
        else
            workingFile = fileContext.newFile(UrlUtils.newUrlReplacePath(workingFile.getUrl(), newpath));
    }
}
