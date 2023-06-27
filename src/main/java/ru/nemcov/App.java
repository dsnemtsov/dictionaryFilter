package ru.nemcov;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    public static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Path dictionatyPath = Paths.get(args[0]);
        Path parentPath = dictionatyPath.getParent();
        //String delimiter = args[1] == null ? "=" : args[1];
        String delimiter = "=";

        Set<String> terms;

        Path tempDirectory;
        try {
            tempDirectory = Files.createTempDirectory(parentPath, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Stream<String> lines = Files.lines(dictionatyPath)) {
            terms = lines
                    .map(line -> {
                        logger.info(String.format("Термин: %s", line));
                        String[] split = line.split(delimiter, 2);

                        try {
                            Files.write(Files.createFile(tempDirectory.resolve(split[0])), split[1].getBytes(StandardCharsets.UTF_8));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return split[0];
                    })
                    .sorted()
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Path path = Paths.get(parentPath + "Словарь_отсортированный.txt");
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

        terms.forEach(el -> {
            try {
                Files.write(path, el.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                Files.write(path, delimiter.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
                Files.write(path, Files.readAllBytes(tempDirectory.resolve(el)), StandardOpenOption.APPEND);
                Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);

            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });

        try (Stream<Path> tempFiles = Files.list(tempDirectory)) {
            tempFiles.forEach(file -> {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Files.deleteIfExists(tempDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
