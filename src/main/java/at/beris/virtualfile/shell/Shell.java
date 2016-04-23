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
import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.attribute.PosixFilePermission;
import at.beris.virtualfile.provider.operation.CopyListener;
import at.beris.virtualfile.util.FileUtils;
import at.beris.virtualfile.util.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.text.DateFormat;
import java.util.*;

import static at.beris.virtualfile.util.UrlUtils.maskedUrlString;

public class Shell {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Shell.class);
    private final static String NOT_CONNECTED_MSG = "You're not connected. use con.";
    private final static DateFormat DATE_FORMATTER = DateFormat.getDateInstance();
    private final static DateFormat TIME_FORMATTER = DateFormat.getTimeInstance();


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
            case PUT:
                put(cmd.getRight().get(0));
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

        int maxLengthOwner = 0, maxLengthGroup = 0, maxLengthSize = 0;
        int maxLengthDateStr = 0, maxLengthTimeStr = 0;

        StringBuilder sb = new StringBuilder();
        List<ExtFileModel> fileModelList = new ArrayList<>();
        for (File childFile : file.list()) {
            ExtFileModel model = new ExtFileModel();
            model.setUrl(childFile.getUrl());
            model.setAttributes(childFile.getAttributes());
            model.setDirectory(childFile.isDirectory());
            model.setOwner(childFile.getOwner());
            model.setGroup(childFile.getGroup());
            model.setSize(childFile.getSize());
            model.setLastModifiedTime(childFile.getLastModifiedTime());

            UserPrincipal owner = model.getOwner();
            if (owner != null && maxLengthOwner < owner.getName().length())
                maxLengthOwner = model.getOwner().getName().length();
            GroupPrincipal group = model.getGroup();
            if (group != null && maxLengthGroup < group.getName().length())
                maxLengthGroup = model.getGroup().getName().length();
            String sizeString = String.valueOf(model.getSize());
            if (maxLengthSize < sizeString.length())
                maxLengthSize = sizeString.length();

            if (model.getLastModifiedTime() != null) {
                Date lastModifiedDate = new Date(model.getLastModifiedTime().toMillis());
                model.dateStr = DATE_FORMATTER.format(lastModifiedDate);
                if (maxLengthDateStr < model.dateStr.length())
                    maxLengthDateStr = model.dateStr.length();
                model.timeStr = TIME_FORMATTER.format(lastModifiedDate);
                if (maxLengthTimeStr < model.timeStr.length())
                    maxLengthTimeStr = model.timeStr.length();
            } else {
                model.dateStr = "";
                model.timeStr = "";
            }

            fileModelList.add(model);
        }

        for (ExtFileModel model : fileModelList) {
            sb.setLength(0);
            sb.append(model.isDirectory() ? 'd' : '-');
            sb.append(model.getAttributes().contains(PosixFilePermission.OWNER_READ) ? 'r' : '-');
            sb.append(model.getAttributes().contains(PosixFilePermission.OWNER_WRITE) ? 'w' : '-');
            sb.append(model.getAttributes().contains(PosixFilePermission.OWNER_EXECUTE) ? 'x' : '-');
            sb.append(model.getAttributes().contains(PosixFilePermission.GROUP_READ) ? 'r' : '-');
            sb.append(model.getAttributes().contains(PosixFilePermission.GROUP_WRITE) ? 'w' : '-');
            sb.append(model.getAttributes().contains(PosixFilePermission.GROUP_EXECUTE) ? 'x' : '-');
            sb.append(model.getAttributes().contains(PosixFilePermission.OTHERS_READ) ? 'r' : '-');
            sb.append(model.getAttributes().contains(PosixFilePermission.OTHERS_WRITE) ? 'w' : '-');
            sb.append(model.getAttributes().contains(PosixFilePermission.OTHERS_EXECUTE) ? 'x' : '-').append(' ');
            sb.append(StringUtils.rightPad(model.getOwner().getName(), maxLengthOwner, ' ')).append(' ');
            sb.append(StringUtils.rightPad(model.getGroup().getName(), maxLengthGroup, ' ')).append(' ');
            sb.append(StringUtils.leftPad(String.valueOf(model.getSize()), maxLengthSize, ' ')).append(' ');
            sb.append(StringUtils.leftPad(model.dateStr, maxLengthDateStr, ' ')).append(' ');
            sb.append(StringUtils.leftPad(model.timeStr, maxLengthTimeStr, ' ')).append(' ');
            sb.append(FileUtils.getName(model.getPath()));
            System.out.println(sb.toString());
        }

        for (FileModel fileModel : fileModelList)
            fileModel.clear();
        fileModelList.clear();
    }

    private void change(String directoryName, boolean local) throws IOException {
        if (!local && workingFile == null) {
            System.out.println(NOT_CONNECTED_MSG);
            return;
        }

        File file = local ? localFile : workingFile;

        URL newUrl;
        if (directoryName.startsWith("/"))
            newUrl = UrlUtils.normalizeUrl(UrlUtils.newUrlReplacePath(file.getUrl(), directoryName + (directoryName.endsWith("/") ? "" : "/")));
        else {
            newUrl = UrlUtils.normalizeUrl(UrlUtils.newUrl(file.getUrl().toString() + directoryName + (directoryName.endsWith("/") ? "" : "/")));
        }

        File actionFile = fileContext.newFile(newUrl);

        if (actionFile.exists()) {
            if (local)
                localFile = actionFile;
            else
                workingFile = actionFile;
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

    private void put(String fileName) throws IOException {
        if (workingFile == null) {
            System.out.println(NOT_CONNECTED_MSG);
            return;
        }

        URL sourceUrl = UrlUtils.normalizeUrl(UrlUtils.newUrl(localFile.getUrl(), fileName));
        File sourceFile = fileContext.newFile(sourceUrl);
        URL targetUrl = UrlUtils.normalizeUrl(UrlUtils.newUrl(workingFile.getUrl(), fileName));
        File targetFile = fileContext.newFile(targetUrl);

        sourceFile.copy(targetFile, new CustomCopyListener());
        System.out.println("");
    }

    private void remove(boolean local, String fileName) throws IOException {
        if (!local && workingFile == null) {
            System.out.println(NOT_CONNECTED_MSG);
            return;
        }

        URL actionUrl = UrlUtils.normalizeUrl(UrlUtils.newUrl((local ? localFile : workingFile).getUrl(), fileName));
        fileContext.newFile(actionUrl).delete();
    }

    private static void logException(Exception e) {
        LOGGER.error("Exception occured.", e);
    }

    private class CustomCopyListener implements CopyListener {
        private final static char PROGRESS_CHAR = '*';
        private final static int NUM_PROGRESS_CHARS_100_PERC = 80;

        private int progressCharsPrinted;

        @Override
        public void startFile(File file, long currentFileNumber) {
            try {
                System.out.println("Copying " + file.getPath());
                progressCharsPrinted = 0;
            } catch (IOException e) {
                logException(e);
            }
        }

        @Override
        public void finishedFile(File file) {
            if (NUM_PROGRESS_CHARS_100_PERC - progressCharsPrinted > 0)
                System.out.print(StringUtils.repeat(PROGRESS_CHAR, (NUM_PROGRESS_CHARS_100_PERC - progressCharsPrinted)));
            System.out.println("");
        }

        @Override
        public void afterBlockCopied(long fileSize, long bytesCopiedBlock, long bytesCopiedTotal) {
            int percent = (int) ((bytesCopiedTotal * 100) / fileSize);
//            int currentNumProgressCharsToPrint = percent / (100 / NUM_PROGRESS_CHARS_100_PERC);
            int currentNumProgressCharsToPrint = (int) Math.floor(percent / (100 / (float) NUM_PROGRESS_CHARS_100_PERC));

            if (progressCharsPrinted < currentNumProgressCharsToPrint) {
                System.out.print(PROGRESS_CHAR);
                System.out.flush();
                progressCharsPrinted++;
            }
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

    private class ExtFileModel extends FileModel {
        public String dateStr;
        public String timeStr;
    }
}
