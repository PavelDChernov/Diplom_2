package service.json;

public class ErrorResponse {
    private Boolean success;
    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean isSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
