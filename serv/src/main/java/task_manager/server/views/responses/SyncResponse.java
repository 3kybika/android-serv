package task_manager.server.views.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import task_manager.server.views.requests.TaskJsonModel;

import java.sql.Timestamp;
import java.util.List;

public class SyncResponse {


        @JsonProperty("sync_time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        Timestamp lastSyncTime;
        @JsonProperty("updated_tasks")
        List<TaskJsonModel> currentTasks;

        public SyncResponse(
                Timestamp lastSyncTime,
                List<TaskJsonModel> currentTasks
        ) {
            this.lastSyncTime = lastSyncTime;
            this.currentTasks = currentTasks;
        }

}
