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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

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
            } catch (MalformedURLException e) {
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
            case CON:
                connect(new URL(cmd.getRight().get(0)));
                break;
            case HELP:
                displayHelp();
                break;
            case LPWD:
                System.out.println(localFile.getPath());
                break;
            case LS:
                list();
                break;
            case PWD:
                System.out.println(workingFile != null ? workingFile.getUrl().toString() : NO_WORKING_FILE_MSG);
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
        int mb = 1024 * 1024;

        Runtime runtime = Runtime.getRuntime();
        System.out.println("** Heap utilization statistics [MB] **");
        System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);
        System.out.println("Free Memory:" + runtime.freeMemory() / mb);
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);
    }

    private void quit() throws IOException {
//        //TODO dispose all files
//        fileContext.dispose(workFile);
//        if (workFile != null)
//            fileContext.dispose(workFile);
    }

    private void connect(URL url) throws IOException {
        workingFile = fileContext.newFile(url);
    }

    private void list() throws IOException {
        if (workingFile == null) {
            System.out.println(NO_WORKING_FILE_MSG);
            return;
        }
        for(File file : workingFile.list()) {
            System.out.println(String.format("%-20s %d kb %s", file.getName(), file.getSize() / 1024, file.isDirectory() ? "<DIR>" : ""));
        }
    }
}
