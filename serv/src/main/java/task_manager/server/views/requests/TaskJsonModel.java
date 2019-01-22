package task_manager.server.views.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class TaskJsonModel {
    @JsonProperty("global_id")
    private int globalId;
    @JsonProperty("local_id")
    private int localId;

    @JsonProperty("author_id")
    private int authorId;
    @JsonProperty("changed_by")
    private int changedBy;
    @JsonProperty("priority")
    private int priority;

    @JsonProperty("name")
    private String name;
    @JsonProperty("about")
    private String about;

    @JsonProperty("deadline")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Timestamp deadline;
    @JsonProperty("notification_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Timestamp notificationTime;
    @JsonProperty("last_change_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Timestamp lastChangeTime;

    @JsonProperty("completed")
    private boolean completed;
    @JsonProperty("deleted")
    private boolean deleted;

    public TaskJsonModel(
            @JsonProperty("global_id") int globalId,
            @JsonProperty("local_id")int localId,

            @JsonProperty("author_id")int authorId,
            @JsonProperty("changed_by")int changedBy,

            @JsonProperty("priority")int priority,
            @JsonProperty("name")String name,
            @JsonProperty("about") String about,

            @JsonProperty("deadline")Timestamp deadline,
            @JsonProperty("notification_time")Timestamp notificationTime,
            @JsonProperty("last_change_time")Timestamp lastChangeTime,

            @JsonProperty("completed")boolean completed,
            @JsonProperty("deleted")boolean deleted
    ) {
        this.globalId = globalId;
        this.localId = localId;

        this.authorId = authorId;
        this.changedBy = changedBy;
        this.priority = priority;

        this.name = name;
        this.about = about;

        this.deadline = deadline;
        this.notificationTime = notificationTime;
        this.lastChangeTime = lastChangeTime;

        this.completed = completed;
        this.deleted = deleted;
    }

    public TaskJsonModel(
            int globalId,

            int authorId,
            int changedBy,

            int priority,
            String name,
            String about,

            Timestamp deadline,
            Timestamp notificationTime,
            Timestamp lastChangeTime,

            boolean completed,
            boolean deleted
    ) {
        this.globalId = globalId;
        this.localId = -1;

        this.authorId = authorId;
        this.changedBy = changedBy;
        this.priority = priority;

        this.name = name;
        this.about = about;

        this.deadline = deadline;
        this.notificationTime = notificationTime;
        this.lastChangeTime = lastChangeTime;

        this.completed = completed;
        this.deleted = deleted;
    }

    public int getAuthorId() {
        return authorId;
    }

    public int getChangedBy() {
        return changedBy;
    }

    public int getGlobalId() {
        return globalId;
    }

    public int getLocalId() {
        return localId;
    }

    public int getPriority() {
        return priority;
    }

    public String getAbout() {
        return about;
    }

    public String getName() {
        return name;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public Timestamp getLastChangeTime() {
        return lastChangeTime;
    }

    public Timestamp getNotificationTime() {
        return notificationTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setGlobalId(int globalId) {
        this.globalId = globalId;
    }
}
