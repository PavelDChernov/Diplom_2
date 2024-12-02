package service.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    private String email;
    private String password;
    private String name;

    public User(String email, String name) {
        this.email = email;
        this.name = name;
        this.password = null;
    }
}
