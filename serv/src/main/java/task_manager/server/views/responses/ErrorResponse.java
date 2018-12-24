package task_manager.server.views.responses;

import task_manager.server.utilities.ErrorCoder;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

/**
 * Created by Alex on 25.02.2018.
 */
public class ErrorResponse {

    @JsonProperty("success")
    private final Boolean success = false;
    @JsonProperty("msg")
    private final String msg;

    @JsonCreator
    public ErrorResponse(@NotNull Integer code, String msg) {
        this.msg = msg;
    }

    public ErrorResponse(ErrorCoder error) {
        this.msg = error.getMsg();
    }

    public String getDescription() {
        return msg;
    }
}