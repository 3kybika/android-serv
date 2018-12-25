package task_manager.server.views.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Alex on 26.02.2018.
 */
public class SuccessResponse {
    @JsonProperty("success")
    private final Boolean success = true;
    @JsonProperty("message")
    private final String message;

    @JsonCreator
    public SuccessResponse(@JsonProperty("message") String msg) {
        this.message = msg;
    }

}
