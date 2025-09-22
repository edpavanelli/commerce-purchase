package net.mycompany.commerce.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {
    @Test
    void testCapitalizeFirstLetterNormal() {
        assertEquals("Hello", StringUtils.capitalizeFirstLetter("hello"));
        assertEquals("Hello", StringUtils.capitalizeFirstLetter("HELLO"));
        assertEquals("H", StringUtils.capitalizeFirstLetter("h"));
    }

    @Test
    void testCapitalizeFirstLetterEmptyOrNull() {
        assertEquals("", StringUtils.capitalizeFirstLetter(""));
        assertNull(StringUtils.capitalizeFirstLetter(null));
    }

    @Test
    void testCapitalizeFirstLetterAlreadyCapitalized() {
        assertEquals("Hello", StringUtils.capitalizeFirstLetter("Hello"));
    }
}
