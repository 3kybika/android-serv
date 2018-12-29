package task_manager.server.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import task_manager.server.models.Task
import org.springframework.stereotype.Service
import java.sql.ResultSet
import javax.validation.constraints.NotNull
import java.util.ArrayList

@Service
class TaskService @Autowired
constructor(private val jdbcTemplate: JdbcTemplate) {

    fun deleteTable() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS tasks CASCADE")
    }

    fun addTask(@NotNull task: Task): Int {
        return jdbcTemplate.queryForObject<Int?>(
                "INSERT INTO tasks(author_id, caption, about, checked) VALUES(?, ?, ?, ?)" + "RETURNING id",
                arrayOf(
                        task.author_id,
                        task.caption,
                        task.about,
                        task.checked
                ),
                { response: ResultSet, rowNum: Int -> response.getInt("id") }
        )!!
    }

    fun getTaskById(id: Int?): Task {
        return jdbcTemplate.queryForObject<Task>(
                "SELECT * FROM tasks WHERE (tasks.id) = ?",
                arrayOf(id)
        ) { response, rowNum ->
            Task(
                    response.getInt("id"),
                    response.getInt("author_id"),
                    response.getString("caption"),
                    response.getString("about"),
                    response.getBoolean("checked")
            )
        }!!
    }

    fun getTasksByAuthorId(author_id: Int): List<Task> {
        return jdbcTemplate.query(
                "SELECT * FROM tasks WHERE (tasks.author_id) = ?",
                arrayOf(author_id),
                TASK_MAPPER)
    }


    fun updateTask(task: Task, id: Int?): Task {
        val querry = StringBuilder()

        querry.append("UPDATE tasks SET ")
        var changed = false

        if (task.caption != null) {
            querry.append("caption = '" + task.caption + "',")
            changed = true
        }

        if (task.about != null) {
            querry.append("about = '" + task.about + "',")
            changed = true
        }

        if (task.checked != null) {
            querry.append("checked = " + (if (task.checked!!) "True" else "False") + ",")
            changed = true
        }

        querry.deleteCharAt(querry.length - 1)
        querry.append(" WHERE tasks.id = '$id';")
        if (changed) {
            jdbcTemplate.update(querry.toString())
        }
        return task
    }

    companion object {
        private val TASK_MAPPER = { responce: ResultSet, num: Int ->
            Task(
                    responce.getInt("id"),
                    responce.getInt("author_id"),
                    responce.getString("caption"),
                    responce.getString("about"),
                    responce.getBoolean("checked")
            )
        }
    }
}