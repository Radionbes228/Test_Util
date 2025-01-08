package com.test_demo;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileFilterUtil {
    private static final String DEFAULT_NAME_OUT_FILE_INTEGER = "integer.txt";
    private static final String DEFAULT_NAME_OUT_FILE_FLOAT = "float.txt";
    private static final String DEFAULT_NAME_OUT_FILE_STRING = "string.txt";
    private static final String OPTION_RESULT_OUT_FILE_PATH = "-o";
    private static final String OPTION_RESULT_OUT_FILE_PREFIX = "-p";
    private static final String OPTION_RESULT_OUT_FILE_OVERWRITE = "-a";
    private static final String OPTION_BRIEF_STATISTICS = "-s";
    private static final String OPTION_FULL_STATISTICS = "-f";
    private static boolean isFullStatistics = false;
    private static boolean isBriefStatistics = false;
    private static boolean append = true;
    private static String outputFilePath = "./";
    private static String prefix = "";
    private static Path dirPath;
    private static final List<String> files = new ArrayList<>();
    private static final List<BigInteger> bigIntegers = new ArrayList<>();
    private static final List<Double> floats = new ArrayList<>();
    private static final List<String> strings = new ArrayList<>();

    public static void main(String[] args){
        if (args.length != 0) {
            try {
                for (int i = 0; i < args.length; i++) {
                    whatOption(args[i]);
                    findFiles(args[i]);
                    setOptionResultOutFilePath(args[i], i, args);
                    setPrefix(args[i], i, args);
                }
                createFile(prefix);
                readFiles();
                printStatistics();
                writeFiles();
            } catch (RuntimeException runtimeException){
                System.out.println(runtimeException.getMessage());
            }
        } else {
            System.out.println("No arguments given!");
        }
    }

    private static void writeFiles(){
        writeFile(prefix + DEFAULT_NAME_OUT_FILE_INTEGER, bigIntegers.stream().map(String::valueOf).toList());
        writeFile(prefix + DEFAULT_NAME_OUT_FILE_FLOAT, floats.stream().map(String::valueOf).toList());
        writeFile(prefix + DEFAULT_NAME_OUT_FILE_STRING, strings);
    }
    private static void findFiles(String filesPath){
        if (filesPath.endsWith(".txt")){
            files.add(filesPath);
        }
    }
    private static void createFile(String fileName)throws RuntimeException {
        try {
            dirPath = Paths.get(outputFilePath);
            Files.createDirectories(dirPath);

            if (!bigIntegers.isEmpty()) {
                Path filePath = dirPath.resolve(fileName + DEFAULT_NAME_OUT_FILE_INTEGER);
                Files.createFile(filePath);
            }
            if (!floats.isEmpty()) {
                Path filePath = dirPath.resolve(fileName + DEFAULT_NAME_OUT_FILE_FLOAT);
                Files.createFile(filePath);
            }
            if (!strings.isEmpty()) {
                Path filePath = dirPath.resolve(fileName + DEFAULT_NAME_OUT_FILE_STRING);
                Files.createFile(filePath);
            }
        }catch (IOException e) {
            throw new RuntimeException("You did not specify the result folder: -o ?");
        }
    }
    private static void readFiles(){
        for (String file : files) {
            Path filePath = Paths.get(file);
            try {
                BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()));
                String line;
                while ((line = reader.readLine()) != null) {
                    processLine(line, filePath.toFile().getName());
                }
            }  catch (IOException e) {
                System.err.printf("Error read file %s : %s\n", file, e.getMessage());
            }
        }
    }
    private static void writeFile(String fileName, List<String> content) {
        if (content.isEmpty()) return;
        Path filePath = dirPath.resolve(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), append))) {
            for (String line : content) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.printf("Error writing to file %s : %s\n", fileName, e.getMessage());
        }
    }
    private static void processLine(String line, String fileName){
        try {
            if (line.matches("-?\\d+")) {
                bigIntegers.add(new BigInteger(line));
            } else if (line.matches("-?\\d+(\\.\\d+)?([eE][-+]?\\d+)?")) {
                floats.add(Double.parseDouble(line));
            } else {
                strings.add(line);
            }
        } catch (NumberFormatException e) {
            System.err.printf("Invalid number format to file %s -> %s%n", fileName, line);
        }
    }
    private static void setOptionResultOutFilePath(String arg, int index, String[] args) {
        if (OPTION_RESULT_OUT_FILE_PATH.equals(arg)) {
            outputFilePath = args[index + 1];
        }
    }
    private static void setPrefix(String arg, int index, String[] args) throws IllegalArgumentException {
        if (OPTION_RESULT_OUT_FILE_PREFIX.equals(arg)) {
            prefix = args[index + 1];
            if (!prefix.matches("^[a-zA-Z]+[-_]?$")){
                throw new IllegalArgumentException("Invalid prefix: %s\n".formatted(prefix));
            }
        }
    }
    private static void whatOption(String option) {
        switch (option) {
            case OPTION_BRIEF_STATISTICS:
                isBriefStatistics = true;
                break;
            case OPTION_RESULT_OUT_FILE_OVERWRITE:
                append = false;
                break;
            case OPTION_FULL_STATISTICS:
                isFullStatistics = true;
                break;
        }
    }
    private static void printStatistics() {
        if (isBriefStatistics) {
            System.out.println();
            System.out.println("Short Statistics:");
            System.out.println("Integers: " + bigIntegers.size());
            System.out.println("Floats: " + floats.size());
            System.out.println("Strings: " + strings.size());
        }
        if (isFullStatistics) {
            System.out.println();
            System.out.println("Full Statistics:");
            printIntegerStats();
            printFloatStats();
            printStringStats();
        }
    }
    private static void printIntegerStats() {
        if (bigIntegers.isEmpty()) return;
        BigInteger min = Collections.min(bigIntegers);
        BigInteger max = Collections.max(bigIntegers);
        double sum = bigIntegers.stream().mapToLong(BigInteger::longValue).sum();
        double avg = sum / bigIntegers.size();
        System.out.printf("Integers: count=%s, min=%s, max=%s, sum=%s, avg=%s;\n", bigIntegers.size(), min, max, sum, avg);
    }
    private static void printFloatStats() {
        if (floats.isEmpty()) return;
        double min = Collections.min(floats);
        double max = Collections.max(floats);
        double sum = floats.stream().mapToDouble(Double::doubleValue).sum();
        double avg = sum / floats.size();
        System.out.printf("Floats: count=%s, min=%s, max=%s, sum=%s, avg=%s;\n", floats.size(), min, max, sum, avg);
    }
    private static void printStringStats() {
        if (strings.isEmpty()) return;
        int minLength = strings.stream().mapToInt(String::length).min().orElse(0);
        int maxLength = strings.stream().mapToInt(String::length).max().orElse(0);
        System.out.printf("Strings: count=%s, minLength=%s, maxLength=%s;\n", strings.size(), minLength, maxLength);
    }
}