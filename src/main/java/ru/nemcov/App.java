package ru.nemcov;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    public static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Set<String> terms = new HashSet<>();
        Map<String, Path> tempFiles = new HashMap<>();

        try (Stream<String> lines = Files.lines(Paths.get("D:\\Словарь.txt"))) {
            lines.forEach(line -> {
                logger.info(String.format("Термин: %s", line));

                String[] split = line.split("=");
                try {
                    Path temp = Files.createTempFile(split[0], ".txt");
                    Files.write(temp, split[1].getBytes(StandardCharsets.UTF_8));
                    tempFiles.put(split[0], temp);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }

                terms.add(split[0]);
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        ArrayList<String> arrayList = new ArrayList<>(terms);
        Collections.sort(arrayList);

        try (FileWriter fw = new FileWriter("D:\\" + "Sorted_" + new Date().getTime() + ".txt")) {
            arrayList.forEach(el -> {
                try {
                    File nextFile = tempFiles.get(el).toFile();
                    Scanner scanner = new Scanner(nextFile);
                    String next = scanner.nextLine();

                    fw.write(el + "=" + next);
                    fw.write(System.getProperty("line.separator"));

                    scanner.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        tempFiles.values().forEach(p -> {
            try {
                Files.delete(p);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });
    }
}
