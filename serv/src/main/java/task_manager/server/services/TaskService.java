package task_manager.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import task_manager.server.models.Task;
import org.springframework.stereotype.Service;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TaskService(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    public void deleteTable() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS tasks CASCADE");
    }

    private static final RowMapper<Task> TASK_MAPPER = (responce, num) ->
            new Task(
                    responce.getInt("id"),
                    responce.getInt("author_id"),
                    responce.getString("caption"),
                    responce.getString("about"),
                    responce.getBoolean("checked")
            );

    public int addTask(@NotNull Task task) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO tasks(author_id, caption, about, checked) VALUES(?, ?, ?, ?)"
                        + "RETURNING id",
                (response, rowNum) -> new Integer(
                        response.getInt("id")
                ),
                task.getAuthorId(),
                task.getCaption(),
                task.getAbout(),
                task.getChecked()
        );
    }

    public Task getTaskById(Integer id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM tasks WHERE (tasks.id) = ?",
                new Object[]{id},
                (response, rowNum) -> new Task(
                        response.getInt("id"),
                        response.getInt("author_id"),
                        response.getString("caption"),
                        response.getString("about"),
                        response.getBoolean("checked")
                )
        );
    }

    public List<Task> getTasksByAuthorId(Integer author_id) {
        return jdbcTemplate.query(
                "SELECT * FROM tasks WHERE (tasks.author_id) = ?",
                new Object[]{author_id},
                TASK_MAPPER);
    }


    public Task updateTask(Task task, Integer id) {
        StringBuilder querry = new StringBuilder();

        querry.append("UPDATE tasks SET ");
        boolean changed = false;

        if (task.getCaption() != null) {
            querry.append("caption = '" + task.getCaption() + "',");
            changed = true;
        }

        if (task.getAbout() != null) {
            querry.append("about = '" + task.getAbout() + "',");
            changed = true;
        }

        if (task.getChecked() != null) {
            querry.append("checked = " + (task.getChecked() ? "True" : "False") + ",");
            changed = true;
        }

        querry.deleteCharAt(querry.length() - 1);
        querry.append(" WHERE tasks.id = '" + id + "';");
        if (changed) {
            jdbcTemplate.update(querry.toString());
        }
        return task;
    }
}