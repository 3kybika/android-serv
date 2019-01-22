package task_manager.server.views.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.List;

public class SyncRequest {
    @JsonProperty("last_sync_time")
    Timestamp lastSyncTime;
    @JsonProperty("current_tasks")
    List<TaskJsonModel> currentTasks;

    public SyncRequest(
            @JsonProperty("last_sync_time") Timestamp lastSyncTime,
            @JsonProperty("current_tasks") List<TaskJsonModel> currentTasks
    ){
        this.lastSyncTime = lastSyncTime;
        this.currentTasks = currentTasks;
    }

    public List<TaskJsonModel> getCurrentTasks() {
        return currentTasks;
    }

    public Timestamp getLastSyncTime() {
        return lastSyncTime;
    }
}
