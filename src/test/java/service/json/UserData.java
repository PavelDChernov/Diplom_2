package service.json;

public class UserData {
    private Boolean success;
    private User user;

    public UserData() {
    }

    public UserData(Boolean success, User user) {
        this.success = success;
        this.user = user;
    }

    public Boolean isSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
