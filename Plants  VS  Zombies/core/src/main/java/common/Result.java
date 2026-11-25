package common;
// >>> This is for transporting messages between controller and view of both game and app
public record Result(boolean success, String message, String errorCode) {
    public Result(boolean success, String message) {
        this(success, message, null);
    }

    @Override
    public String toString() {
        return message;
    }

    public static Result success(String message) {
        return new Result(true, message, null);
    }

    public static Result failure(String message) {
        return new Result(false, message, null);
    }

    public static Result failure(String errorCode, String message) {
        return new Result(false, message, errorCode);
    }
}
