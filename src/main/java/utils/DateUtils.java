package utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private static final int OUTBOUND_DELIVERY_MIN_DAYS_FROM_TODAY = 10;
    private static final DateTimeFormatter OUTBOUND_DELIVERY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtils() {
    }

    /**
     * First calendar date at least 10 days from today that is not Friday or Saturday.
     * Format matches {@code createOutbound(String deliveryDate, ...)} ({@code yyyy-MM-dd}).
     */
    public static String generateOutboundDeliveryDate() {
        LocalDate candidate = LocalDate.now().plusDays(OUTBOUND_DELIVERY_MIN_DAYS_FROM_TODAY);
        while (isFridayOrSaturday(candidate)) {
            candidate = candidate.plusDays(1);
        }
        return candidate.format(OUTBOUND_DELIVERY_DATE_FORMAT);
    }

    private static boolean isFridayOrSaturday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY;
    }
}
