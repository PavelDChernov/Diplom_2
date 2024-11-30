package service.json;

public class AuthorizedUserData {
    private Boolean success;
    private String accessToken;
    private String refreshToken;
    private User user;

    public AuthorizedUserData() {
    }

    public AuthorizedUserData(Boolean success, String accessToken, String refreshToken, User user) {
        this.success = success;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public AuthorizedUserData(Boolean success, User user) {
        this.success = success;
        this.user = user;
        this.accessToken = null;
        this.refreshToken = null;
    }

    public Boolean isSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
