package task_manager.server.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.RowMapper
import task_manager.server.models.User
import org.springframework.stereotype.Service
import java.sql.ResultSet
import javax.validation.constraints.NotNull

@Service
class UserService @Autowired
constructor(private val jdbcTemplate: JdbcTemplate) {

    fun deleteTable() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE")
    }

    fun addUser(@NotNull user: User): Int {
        return jdbcTemplate.queryForObject<Int>(
                "INSERT INTO users(login, email, password) VALUES(?, ?, ?)" + "RETURNING id",
                arrayOf(user.login,
                        user.email,
                        user.password),
                { response, rowNum -> response.getInt("id") }
        )!!
    }

    fun getUserByLogin(login: String): User? {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE (users.login) = ?",
                arrayOf<Any>(login)
        ) { response, rowNum ->
            User(
                    response.getInt("id"),
                    response.getString("login"),
                    response.getString("email"),
                    response.getString("password")
            )
        }
    }

    fun getUserByEmail(email: String): User? {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE (users.email) = ?",
                arrayOf<Any>(email)
        ) { response, rowNum ->
            User(
                    response.getInt("id"),
                    response.getString("login"),
                    response.getString("email"),
                    response.getString("password")
            )
        }
    }

    fun getUserById(id: Int?): User {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE (users.id) = (?)",
                arrayOf(id)
        ) { response, rowNum ->
            User(
                    response.getString("login"),
                    response.getString("email"),
                    response.getString("password")
            )
        }!!
    }

    fun updateUser(user: User, id: Int?): User {
        val querry = StringBuilder()

        querry.append("UPDATE users SET ")
        var changed = false
        if (user.email != null) {
            querry.append("login = '" + user.login + "',")
            changed = true
        }
        if (user.login != null) {
            querry.append("email = '" + user.email + "',")
            changed = true
        }
        if (user.password != null) {
            querry.append("password = '" + user.password + "',")
            changed = true
        }

        querry.deleteCharAt(querry.length - 1)
        querry.append(" WHERE users.id = '$id';")
        if (changed) {
            jdbcTemplate.update(querry.toString())
        }
        return user
    }

    companion object {

        private val USER_MAPPER = { responce: ResultSet, num: Int ->
            User(
                    responce.getInt("id"),
                    responce.getString("login"),
                    responce.getString("password"),
                    responce.getString("email")
            )
        }
    }
}