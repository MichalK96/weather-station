package michal.api.weatherstationapi.util;

public class Util {

    private Util() {};

    public static boolean validateName(String name) {
        String regex = "^[a-zA-Z0-9-_\\/! ]{2,25}$";
        return name.matches(regex);
    }

}
