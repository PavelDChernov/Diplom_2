package service.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthorizedUserData {
    private Boolean success;
    private String accessToken;
    private String refreshToken;
    private User user;

    public AuthorizedUserData(Boolean success, User user) {
        this.success = success;
        this.user = user;
        this.accessToken = null;
        this.refreshToken = null;
    }
}
