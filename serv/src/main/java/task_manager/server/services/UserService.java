package task_manager.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import task_manager.server.models.User;
import org.springframework.stereotype.Service;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class UserService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    public void deleteTable() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE");
    }

    private static final RowMapper<User> USER_MAPPER = (responce, num) ->
            new User(
                    responce.getInt("id"),
                    responce.getString("login"),
                    responce.getString("password"),
                    responce.getString("email")
             );

    public int addUser(@NotNull User user) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO users(login, email, password) VALUES(?, ?, ?)"
                        + "RETURNING id",
        (response, rowNum) -> new Integer(
                response.getInt("id")
        ),
                        user.getLogin(),
                        user.getEmail(),
                        user.getPassword()
        );
    }

     public User getUserByLogin(String login) {
         return jdbcTemplate.queryForObject(
                 "SELECT * FROM users WHERE (users.login) = ?",
                 new Object[]{login},
                 (response, rowNum) -> new User(
                         response.getInt("id"),
                         response.getString("login"),
                         response.getString("email"),
                         response.getString("password")
                 )
         );
    }

    public User getUserByEmail(String email) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE (users.email) = ?",
                new Object[]{email},
                (response, rowNum) -> new User(
                        response.getInt("id"),
                        response.getString("login"),
                        response.getString("email"),
                        response.getString("password")
                )
        );
    }

    public User getUserById(Integer id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE (users.id) = (?)",
                new Object[]{id},
                (response, rowNum) -> new User(
                        response.getString("login"),
                        response.getString("email"),
                        response.getString("password")
                )
        );
    }

    public User updateUser(User user, Integer id) {
        StringBuilder querry = new StringBuilder();

        querry.append("UPDATE users SET ");
        boolean changed = false;
        if (user.getEmail() != null) {
            querry.append("login = '" + user.getLogin() + "',");
            changed = true;
        }
        if (user.getLogin() != null) {
            querry.append("email = '" + user.getEmail() + "',");
            changed = true;
        }
        if (user.getPassword() != null) {
            querry.append("password = '" + user.getPassword() + "',");
            changed = true;
        }

        querry.deleteCharAt(querry.length() - 1);
        querry.append(" WHERE users.id = '" + id + "';");
        if (changed) {
            jdbcTemplate.update(querry.toString());
        }
        return user;
    }
}