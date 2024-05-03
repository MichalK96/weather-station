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

    public static double roundToNearestHalf(double value) {
        boolean isPositive = true;
        if (value < 0) {
            value = -value;
            isPositive = false;
        }

        double integerPart = Math.floor(value);
        double fractionalPart = value - integerPart;
        double roundedFractionalPart;

        if (fractionalPart < 0.25) {
            roundedFractionalPart = 0.0;
        } else if (fractionalPart < 0.75) {
            roundedFractionalPart = 0.5;
        } else {
            roundedFractionalPart = 1.0;
        }

        double result = integerPart + roundedFractionalPart;
        return isPositive ? result : -result;
    }

    public static String generateHtml(String name, String body) {
        return String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                        <style>
                            table, th, td {
                                font-size: 20px;
                                border: 2px solid black;
                                border-collapse: collapse;
                                padding: 8px;
                            }
                            
                            p {
                                font-size: 20px;
                               }
                               
                           a.button {
                               padding: 4px 6px;
                               border: 3px outset buttonborder;
                               border-radius: 3px;
                               color: buttontext;
                               background-color: buttonface;
                               text-decoration: none;
                           }
                        </style>
                </head>
                %s
                </html>
                                
                """, name, body);
    }

}
