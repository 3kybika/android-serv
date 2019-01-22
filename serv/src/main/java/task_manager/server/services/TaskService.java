package task_manager.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import task_manager.server.views.requests.TaskJsonModel;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TaskService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TaskService(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    public void deleteTable() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE");
    }

    private static final RowMapper<TaskJsonModel> TASK_MAPPER = (responce, num) ->
            new TaskJsonModel(
                    responce.getInt("global_id"),

                    responce.getInt("author_id"),
                    responce.getInt("changed_by"),

                    responce.getInt("priority"),
                    responce.getString("name"),
                    responce.getString("about"),

                    responce.getTimestamp("deadline"),
                    responce.getTimestamp("notificationTime"),
                    responce.getTimestamp("lastChangeTime"),

                    responce.getBoolean("completed"),
                    responce.getBoolean("deleted")
            );

    public void updateTasks(List<TaskJsonModel> tasks) {
        for (TaskJsonModel task : tasks) {
            jdbcTemplate.update(
                    "UPDATE Tasks SET " +
                            " author_id = ?," +
                            " changed_by = ?, " +
                            " priority = ?, " +
                            " name = ?, " +
                            " about = ?, " +
                            " deadline = ?, " +
                            " notificationTime = ?, " +
                            " lastChangeTime = ?, " +
                            " completed = ?, " +
                            " deleted = ? " +
                    "WHERE global_id = ? AND lastChangeTime < ?;",
                    task.getAuthorId(),
                    task.getChangedBy(),
                    task.getPriority(),
                    task.getName(),
                    task.getAbout(),
                    task.getDeadline(),
                    task.getNotificationTime(),
                    task.getLastChangeTime(),
                    task.isCompleted(),
                    task.isDeleted(),

                    task.getGlobalId(),
                    task.getLastChangeTime()
            );
        }
    }

    public List<TaskJsonModel> createTasks(List<TaskJsonModel> tasks) {
        for (TaskJsonModel task : tasks) {
            int taskId = jdbcTemplate.queryForObject(
                    "INSERT INTO Tasks (author_id, changed_by, priority, name, about, deadline, notificationTime, lastChangeTime, completed, deleted)" +
                            " VALUES(?,?,?,?,?,?::TIMESTAMP WITH TIME ZONE,?::TIMESTAMP WITH TIME ZONE,?::TIMESTAMP WITH TIME ZONE,?,?) " +
                            " RETURNING global_id;",
                    Integer.class,
                    task.getAuthorId(),
                    task.getChangedBy(),
                    task.getPriority(),
                    task.getName(),
                    task.getAbout(),
                    task.getDeadline(),
                    task.getNotificationTime(),
                    task.getLastChangeTime(),
                    task.isCompleted(),
                    task.isDeleted()
            );
            task.setGlobalId(taskId);
        }

        return tasks;
    }



    public List<TaskJsonModel>  getUpdatedTask(Timestamp syncTime, int authorId, List<Integer> excludedTasks) {
        List<TaskJsonModel> tasks;
        if (syncTime == null) {
            tasks = jdbcTemplate.query(
                    "SELECT * FROM Tasks " +
                            "WHERE " +
                            "author_id = ? ;",
                    TASK_MAPPER,
                    authorId
            );
        } else {
            tasks = jdbcTemplate.query(
                    "SELECT * FROM Tasks " +
                            "WHERE " +
                            "lastChangeTime > ?::TIMESTAMP WITH TIME ZONE AND " +
                            "author_id = ?;",
                    TASK_MAPPER,
                    syncTime,
                    authorId
            );
        }

        return tasks.stream()
                .filter(task -> !excludedTasks.contains(task.getGlobalId()) ).collect(Collectors.toList());
    }
}