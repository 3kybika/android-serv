package task_manager.server.views.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdForm {
    private int id;


    public IdForm(@JsonProperty("id") int id) {
        this.id = id;
    }

    public int getEmail() {
        return id;
    }

    public void setEmail(int id) {
        this.id = id;
    }

}

