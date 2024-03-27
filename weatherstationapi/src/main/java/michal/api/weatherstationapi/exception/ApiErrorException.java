package michal.api.weatherstationapi.exception;

public class ApiErrorException extends RuntimeException {

    public ApiErrorException() {
        super("API error");
    }

    public ApiErrorException(String message) {
        super(message);
    }

}
