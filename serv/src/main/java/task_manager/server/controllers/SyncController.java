package task_manager.server.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import task_manager.server.models.User;
import task_manager.server.services.TaskService;
import task_manager.server.services.UserService;
import task_manager.server.utilities.ErrorCoder;
import task_manager.server.utils.TimestampUtils;
import task_manager.server.views.requests.SyncRequest;
import task_manager.server.views.requests.TaskJsonModel;
import task_manager.server.views.requests.UserForm;
import task_manager.server.views.responses.ErrorResponse;
import task_manager.server.views.responses.SyncResponse;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/service")
public class SyncController {
    private UserService userService;
    private TaskService taskService;

    @Autowired
    public SyncController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }


    private List<Integer> getListIds(List<TaskJsonModel> tasks) {
        List<Integer> ids = new ArrayList<>();
        for (TaskJsonModel task : tasks) {
            ids.add(task.getGlobalId());
        }
        return ids;
    }

    @PostMapping("/sync")
    public ResponseEntity<?> sync(@RequestBody SyncRequest body, HttpSession httpSession) {
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

        List<TaskJsonModel> tasks = body.getCurrentTasks();
        List<TaskJsonModel> updatedTasks = new ArrayList<>();
        List<TaskJsonModel> createdTasks = new ArrayList<>();
        for (TaskJsonModel task : tasks) {
            if (task.getGlobalId() == -1) {
                createdTasks.add(task);
            } else {
                updatedTasks.add(task);
            }
        }

        Timestamp currentTime = TimestampUtils.getNow();

        taskService.updateTasks(updatedTasks);
        createdTasks = taskService.createTasks(createdTasks);
        createdTasks.addAll(taskService.getUpdatedTask(body.getLastSyncTime(), currentUserId, getListIds(createdTasks)));

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SyncResponse(currentTime,createdTasks));
    }

}
