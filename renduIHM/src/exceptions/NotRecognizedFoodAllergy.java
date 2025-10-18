package exceptions;
public class NotRecognizedFoodAllergy extends Exception {
    String message;
    public NotRecognizedFoodAllergy(String message) {
        this.message=message;
    }

    public NotRecognizedFoodAllergy() {
        this("");
    }

    public String getMessage() {
        return message;
    }
}
