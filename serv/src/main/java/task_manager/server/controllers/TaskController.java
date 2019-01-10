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

import task_manager.server.models.Task;
import task_manager.server.views.requests.IdForm;
import task_manager.server.views.requests.LoginForm;
import task_manager.server.views.requests.UserForm;
import task_manager.server.services.TaskService;
import task_manager.server.models.User;
import task_manager.server.utilities.ErrorCoder;
import task_manager.server.views.requests.ChangeUserDataForm;
import task_manager.server.views.responses.ErrorResponse;
import task_manager.server.views.responses.SuccessResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 19.02.2018.
 */

@RestController
//ToDo: реальный URL фронтенд-сервера
@CrossOrigin
@RequestMapping(path = "/api/users")
public class TaskController {
    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> currentUser(@RequestBody(required = false) String str,HttpSession httpSession) {
        final Integer currentUserId = (Integer) httpSession.getAttribute("id");

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(ErrorCoder.USER_NOT_LOGINED));
        }

        List<Task> tasksList;

        try {
            tasksList = taskService.getTasksByAuthorId(currentUserId);
        } catch (EmptyResultDataAccessException e) {
            httpSession.invalidate();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(ErrorCoder.USER_NOT_EXIST));
        }

        return ResponseEntity.ok(tasksList);
    }
}
