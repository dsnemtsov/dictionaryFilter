package ru.nemcov;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DictionarySorter {
    public static final Logger logger = LoggerFactory.getLogger(DictionarySorter.class);

    private final String dictionaryPathString;
    private final String delimiter;
    private Path dictionaryPath;
    private Path directoryPath;
    private Path tempDirectory;
    Set<String> terms = new HashSet<>();

    public DictionarySorter(String dictionaryPathString, String delimiter) {
        this.dictionaryPathString = dictionaryPathString;
        this.delimiter = delimiter;
    }

    public void run() {
        dictionaryPath = Paths.get(dictionaryPathString);
        directoryPath = dictionaryPath.getParent();

        if (!createTempDirectory()) return;

        fillTerms();

        if (terms.isEmpty()) {
            logger.info("Список терминов пуст");
            return;
        }

        LinkedHashSet<String> sortedTerms = terms.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        logger.info("Термины отсортированы");

        if (saveSortedTerms(sortedTerms)) {
            deleteTempFiles();
        } else {
            logger.error("Ошибка сохранения отсортированного словаря");
        }
    }

    private void fillTerms() {
        try (Stream<String> lines = Files.lines(dictionaryPath)) {
            lines.forEach(this::saveTerm);
            logger.info("Сохранено терминов: {}", terms.size());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void saveTerm(String term) {
        logger.info("Сохранение термина: {}", term);
        String[] split = term.split(delimiter, 2);

        if (split.length != 2) {
            logger.error("Ошибка сохранения термина {}", term);
        }

        String termDefinition = split[0];
        String termValue = split[1];

        if (!terms.contains(termDefinition)) {
            try {
                Files.write(Files.createFile(tempDirectory.resolve(termDefinition)), termValue.getBytes(StandardCharsets.UTF_8));
                terms.add(termDefinition);
                logger.info("Термин сохранен: {}", termDefinition);
            } catch (IOException e) {
                logger.error("Ошибка сохранения термина {}", term);
            }
        } else {
            logger.info("Термин дублируется и не будет сохранен: {}", termDefinition);
        }
    }

    private boolean createTempDirectory() {
        try {
            tempDirectory = Files.createTempDirectory(directoryPath, null);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        logger.info("Создана папка для хранения временных файлов: {}", tempDirectory);
        return true;
    }

    private boolean saveSortedTerms(LinkedHashSet<String> sortedTerms) {
        Path outPutPath = Paths.get(directoryPath + "Словарь_отсортированный.txt");
        if (Files.exists(outPutPath)) {
            try {
                Files.delete(outPutPath);
            } catch (IOException e) {
                logger.error(e.getMessage());
                return false;
            }
        }

        sortedTerms.forEach(el -> {
            try {
                Files.write(outPutPath, el.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                Files.write(outPutPath, delimiter.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
                Files.write(outPutPath, Files.readAllBytes(tempDirectory.resolve(el)), StandardOpenOption.APPEND);
                Files.write(outPutPath, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);

            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });

        logger.info("Термины сохранены в файл: {}", outPutPath);
        return true;
    }

    private void deleteTempFiles() {
        try (Stream<Path> tempFiles = Files.list(tempDirectory)) {
            tempFiles.forEach(file -> {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            Files.deleteIfExists(tempDirectory);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("Временные файлы удалены");
    }

    public Path getTempDirectory() {
        return tempDirectory;
    }
}
