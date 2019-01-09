package task_manager.server.utilities

/**
 * Created by Alex on 25.02.2018.
 */
enum class ErrorCoder private constructor(val code: Int, val msg: String) {
    NOT_VALID_EMAIL(0, "Email is not valid!"),
    NOT_VALID_LOGIN(1, "Login is not valid!"),
    NOT_VALID_PWD(2, "Password is not valid!"),
    NOT_VALID_INFO(3, "Users data is uncorrect!"),
    USER_DUPLICATE(4, "User with such login is already exists!!"),
    EMPTY_FIELDS(5, "Fields are empty!"),
    UNCORRECT_PASSWORD(6, "Uncorrect login or password!"),
    USER_NOT_EXIST(6, "Uncorrect login or password!"),
    USER_NOT_LOGINED(7, "You are not logined!"),
    ALREADY_LOGGED(8, "You are already logined!")
}