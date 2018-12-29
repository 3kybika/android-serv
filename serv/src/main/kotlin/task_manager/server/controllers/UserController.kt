package task_manager.server.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.util.StringUtils
import javax.servlet.http.HttpSession

import task_manager.server.views.requests.LoginForm
import task_manager.server.views.requests.UserForm
import task_manager.server.services.UserService
import task_manager.server.models.User
import task_manager.server.utilities.ErrorCoder
import task_manager.server.views.requests.ChangeUserDataForm
import task_manager.server.views.responses.ErrorResponse
import task_manager.server.views.responses.SuccessResponse

/**
 * Created by Alex on 19.02.2018.
 */

@RestController
//ToDo: реальный URL фронтенд-сервера
@CrossOrigin
@RequestMapping(path = arrayOf("/api/users"))
class UserController @Autowired
constructor(private val userService: UserService) {


    @PostMapping("/signup")
    fun signUp(@RequestBody body: UserForm, httpSession: HttpSession): ResponseEntity<*> {
        val login = body.login
        val email = body.email
        val password = body.password

        if (StringUtils.isEmpty(login)
                || StringUtils.isEmpty(email)
                || StringUtils.isEmpty(password)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse(ErrorCoder.EMPTY_FIELDS))
        }

        if (httpSession.getAttribute("id") != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponse(ErrorCoder.ALREADY_LOGGED))
        }
        var id = 0
        val user = User(login, email, password)
        try {
            id = userService.addUser(user)
        } catch (exception: DuplicateKeyException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponse(ErrorCoder.USER_DUPLICATE))
        } catch (exception: DataIntegrityViolationException) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse(ErrorCoder.NOT_VALID_INFO))
        }

        user.id = id
        httpSession.setAttribute("id", id)

        return ResponseEntity.ok(user)
    }

    @PostMapping("/signin")
    fun signIn(@RequestBody body: LoginForm, httpSession: HttpSession): ResponseEntity<*> {
        val email = body.email
        val password = body.password

        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse(ErrorCoder.EMPTY_FIELDS))
        }

        if (httpSession.getAttribute("id") != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponse(ErrorCoder.ALREADY_LOGGED))
        }
        val currentUser: User?
        try {
            currentUser = userService.getUserByEmail(email)
        } catch (e: EmptyResultDataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse(ErrorCoder.USER_NOT_EXIST))
        }

        if (currentUser!!.password != password) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse(ErrorCoder.UNCORRECT_PASSWORD))
        }

        httpSession.setAttribute("id", currentUser.id)
        return ResponseEntity.ok(currentUser)
    }

    @PostMapping("/logout")
    fun logOut(httpSession: HttpSession): ResponseEntity<*> {

        if (httpSession.getAttribute("id") == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse(ErrorCoder.USER_NOT_LOGINED))
        }

        httpSession.invalidate()

        return ResponseEntity.ok(SuccessResponse("User is successfully log out!"))
    }

    @GetMapping("/me")
    fun currentUser(@RequestBody(required = false) str: String, httpSession: HttpSession): ResponseEntity<*> {
        val currentUserId = httpSession.getAttribute("id") as Int
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ErrorResponse(ErrorCoder.USER_NOT_LOGINED))

        var currentUser: User? = null
        try {
            currentUser = userService.getUserById(currentUserId)
        } catch (e: EmptyResultDataAccessException) {
            httpSession.invalidate()
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse(ErrorCoder.USER_NOT_EXIST))
        }

        return ResponseEntity.ok(currentUser)
    }

    @PostMapping("/me")
    fun changeUserData(@RequestBody body: ChangeUserDataForm, httpSession: HttpSession): ResponseEntity<*> {
        val currentUserId = httpSession.getAttribute("id") as Int?
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ErrorResponse(ErrorCoder.USER_NOT_LOGINED))

        val currentUser: User
        try {
            currentUser = userService.getUserById(currentUserId)
        } catch (e: EmptyResultDataAccessException) {
            httpSession.invalidate()
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse(ErrorCoder.USER_NOT_EXIST))
        }

        val newLogin = body.login
        val password = body.password
        val newPassword = body.newPassword
        val newEmail = body.email

        if (StringUtils.isEmpty(newLogin)
                && StringUtils.isEmpty(newPassword)
                && StringUtils.isEmpty(newEmail)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse(ErrorCoder.EMPTY_FIELDS))
        }

        if (password !== currentUser.password) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse(ErrorCoder.UNCORRECT_PASSWORD))
        }

        val newUserData = User(newLogin, newEmail, newPassword)
        userService.updateUser(newUserData, currentUserId)

        return ResponseEntity.ok(currentUser)
    }
}
