package ru.nemcov;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    public static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        Options options = new Options();
        options.addOption("p", true, "путь к словарю");
        options.addOption("d", true, "разделитель");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            return;
        }

        String dictionaryPathString = null;
        String delimiter = "-";

        if (cmd.hasOption("p")) {
            dictionaryPathString = cmd.getOptionValue("p");
        }
        if (cmd.hasOption("d")) {
            delimiter = cmd.getOptionValue("d");
        }

        new DictionarySorter(dictionaryPathString, delimiter).run();
    }
}
