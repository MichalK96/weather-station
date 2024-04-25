package michal.api.weatherstationapi.htmlresponse;

import java.util.Calendar;
import java.util.Date;

public class HtmlUtil {

    private HtmlUtil() {};
    private static final String[] months = {"styczeń", "luty", "marzec", "kwiecień", "maj", "czerwiec", "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień"};

    public static String getDayOfWeekName(Date date) {
        var calender = Calendar.getInstance();
        calender.setTime(date);
        var dayOfWeek = calender.get(Calendar.DAY_OF_WEEK);

        return switch (dayOfWeek) {
            case Calendar.SUNDAY -> "Niedziela";
            case Calendar.MONDAY -> "Poniedziałek";
            case Calendar.TUESDAY -> "Wtorek";
            case Calendar.WEDNESDAY -> "Środa";
            case Calendar.THURSDAY -> "Czwartek";
            case Calendar.FRIDAY -> "Piątek";
            case Calendar.SATURDAY -> "Sobota";
            default -> "-----";
        };
    }

    public static String getMonthName(Date date) {
        var calender = Calendar.getInstance();
        calender.setTime(date);
        var month = calender.get(Calendar.MONTH);
        return months[month];
    }

    public static int getMonthDay(Date date) {
        var calender = Calendar.getInstance();
        calender.setTime(date);
        return calender.get(Calendar.DAY_OF_MONTH);
    }

}
