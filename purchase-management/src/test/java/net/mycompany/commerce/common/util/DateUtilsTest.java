package net.mycompany.commerce.common.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {
    @Test
    void testGetDateSixMonthsBack() {
        LocalDate now = LocalDate.of(2025, 9, 21);
        LocalDate expected = LocalDate.of(2025, 3, 21);
        assertEquals(expected, DateUtils.getDateSixMonthsBack(now));
    }

    @Test
    void testIsDateTodayTrue() {
        LocalDate today = LocalDate.now();
        assertTrue(DateUtils.isDateToday(today));
    }

    @Test
    void testIsDateTodayFalse() {
        LocalDate notToday = LocalDate.now().minusDays(1);
        assertFalse(DateUtils.isDateToday(notToday));
    }
}
