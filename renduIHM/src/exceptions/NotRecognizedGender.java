package exceptions;
public class NotRecognizedGender extends Exception {
    String message;
    public NotRecognizedGender(String message) {
        this.message=message;
    }

    public NotRecognizedGender() {
        this("");
    }

    public String getMessage() {
        return message;
    }
}
