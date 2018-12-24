package task_manager.server.views.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginForm {
    private String email;

    private String password;

    public LoginForm(
            @JsonProperty("email") String email,
            @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY) String password) {
        this.email = email;
        this.password = password ;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
