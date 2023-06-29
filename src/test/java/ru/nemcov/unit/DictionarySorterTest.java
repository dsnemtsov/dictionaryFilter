package ru.nemcov.unit;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import ru.nemcov.DictionarySorter;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DictionarySorterTest {

    @Test
    void shouldNotCreateTempDirectory() {
        DictionarySorter dictionarySorterNull = new DictionarySorter(null, null);
        DictionarySorter dictionarySorterWrong = new DictionarySorter("NotValid", null);

        assertAll(
                () -> assertThrows(NullPointerException.class, dictionarySorterNull::run),
                () -> assertThrows(NullPointerException.class, dictionarySorterWrong::run)
        );

    }

    @Test
    void shouldCreateTempDirectory() throws IOException {
        DictionarySorter dictionarySorter = new DictionarySorter(System.getProperty("java.io.tmpdir"), null);
        dictionarySorter.run();
        assertNotNull(dictionarySorter.getTempDirectory());
        assertTrue(Files.deleteIfExists(dictionarySorter.getTempDirectory()));
    }
}
