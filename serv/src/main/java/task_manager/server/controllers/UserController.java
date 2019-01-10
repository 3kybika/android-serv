package task_manager.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpSession;

import task_manager.server.views.requests.IdForm;
import task_manager.server.views.requests.LoginForm;
import task_manager.server.views.requests.UserForm;
import task_manager.server.services.UserService;
import task_manager.server.models.User;
import task_manager.server.utilities.ErrorCoder;
import task_manager.server.views.requests.ChangeUserDataForm;
import task_manager.server.views.responses.ErrorResponse;
import task_manager.server.views.responses.SuccessResponse;

/**
 * Created by Alex on 19.02.2018.
 */

@RestController
//ToDo: реальный URL фронтенд-сервера
@CrossOrigin
@RequestMapping(path = "/api/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserForm body, HttpSession httpSession) {
        final String login = body.getLogin();
        final String email = body.getEmail();
        final String password = body.getPassword();

        if (
            StringUtils.isEmpty(login)
            || StringUtils.isEmpty(email)
            || StringUtils.isEmpty(password)
        ) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ErrorCoder.EMPTY_FIELDS));
        }

        if (httpSession.getAttribute("id") != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ErrorCoder.ALREADY_LOGGED));
        }
        int id = 0;
        final User user = new User(login, email, password);
        try {
            id = userService.addUser(user);
        } catch (DuplicateKeyException exception)  {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ErrorCoder.USER_DUPLICATE));
        } catch (DataIntegrityViolationException exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ErrorCoder.NOT_VALID_INFO));
        }
        user.setId(id);
        httpSession.setAttribute("id", id);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody LoginForm body, HttpSession httpSession) {
        final String email = body.getEmail();
        final String password = body.getPassword();

        if (
            StringUtils.isEmpty(email)
            || StringUtils.isEmpty(password)
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ErrorCoder.EMPTY_FIELDS));
        }

        if (httpSession.getAttribute("id") != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ErrorCoder.ALREADY_LOGGED));
        }
        final User currentUser;
        try {
           currentUser = userService.getUserByEmail(email);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(ErrorCoder.USER_NOT_EXIST));
        }

        if (!currentUser.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(ErrorCoder.UNCORRECT_PASSWORD));
        }

        httpSession.setAttribute("id", currentUser.getId());
        return ResponseEntity.ok(currentUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logOut(HttpSession httpSession) {

        if (httpSession.getAttribute("id") == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCoder.USER_NOT_LOGINED));
        }

        httpSession.invalidate();

        return ResponseEntity.ok(new SuccessResponse("User is successfully log out!"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser(@RequestBody(required = false) String str,HttpSession httpSession) {
        final Integer currentUserId = (Integer) httpSession.getAttribute("id");

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(ErrorCoder.USER_NOT_LOGINED));
        }

        User currentUser = null;
        try {
            currentUser = userService.getUserById(currentUserId);
        } catch (EmptyResultDataAccessException e) {
            httpSession.invalidate();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(ErrorCoder.USER_NOT_EXIST));
        }

        return ResponseEntity.ok(currentUser);
    }

    @PostMapping("/me")
    public ResponseEntity<?> changeUserData(@RequestBody ChangeUserDataForm body, HttpSession httpSession) {
        final Integer currentUserId = (Integer) httpSession.getAttribute("id");

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(ErrorCoder.USER_NOT_LOGINED));
        }

        User currentUser = null;
        try {
            currentUser = userService.getUserById(currentUserId);
        } catch (EmptyResultDataAccessException e) {
            httpSession.invalidate();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ErrorCoder.USER_NOT_EXIST));
        }

        final String newLogin = body.getLogin();
        final String  password = body.getPassword();
        final String newPassword = body.getNewPassword();
        final String newEmail = body.getEmail();

        if (
            StringUtils.isEmpty(newLogin)
            && StringUtils.isEmpty(newPassword)
            && StringUtils.isEmpty(newEmail)
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(ErrorCoder.EMPTY_FIELDS));
        }

        if(password != currentUser.getPassword()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(ErrorCoder.UNCORRECT_PASSWORD));
        }

        User newUserData = new User(newLogin, newEmail, newPassword);
        userService.updateUser(newUserData, currentUserId);

        return ResponseEntity.ok(currentUser);
    }


}





