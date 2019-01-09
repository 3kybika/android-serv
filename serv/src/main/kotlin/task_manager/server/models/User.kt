package task_manager.server.models

/**
 * Created by Alex on 19.02.2018.
 */
import com.fasterxml.jackson.annotation.JsonProperty;

data class User(@JsonProperty("id")
                var id: Int?,
                @JsonProperty("email")
                var email: String?,
                @JsonProperty("login")
                var login: String?,
                @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
                var password: String?) {
    constructor(email: String?, login: String?, password: String?) : this(0, email, login, password)
}
