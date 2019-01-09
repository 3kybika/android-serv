package task_manager.server.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpSession

import task_manager.server.models.Task
import task_manager.server.services.TaskService
import task_manager.server.models.User
import task_manager.server.utilities.ErrorCoder
import task_manager.server.views.responses.ErrorResponse

/**
 * Created by Alex onыфв 19.02.2018.
 */

@RestController
//ToDo: реальный URL фронтенд-сервера
@CrossOrigin
@RequestMapping(path = arrayOf("/api/tasks"))
class TaskController @Autowired
constructor(private val taskService: TaskService) {

    @GetMapping("/tasks")
    fun currentUserTasks(@RequestBody(required = false) str: String, httpSession: HttpSession): ResponseEntity<*> {
        val currentUserId = httpSession.getAttribute("id") as Int
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ErrorResponse(ErrorCoder.USER_NOT_LOGINED))

        val tasksList: List<Task>

        try {
            tasksList = taskService.getTasksByAuthorId(currentUserId)
        } catch (e: EmptyResultDataAccessException) {
            httpSession.invalidate()
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse(ErrorCoder.USER_NOT_EXIST))
        }

        return ResponseEntity.ok(tasksList)
    }

    @GetMapping("/tasks/check")
    fun check(@RequestBody(required = false) str: String, httpSession: HttpSession): ResponseEntity<*> {
        val currentUserId = httpSession.getAttribute("id") as Int?
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ErrorResponse(ErrorCoder.USER_NOT_LOGINED))

        val tasksList: List<Task>

        try {
            tasksList = taskService.getTasksByAuthorId(currentUserId)
        } catch (e: EmptyResultDataAccessException) {
            httpSession.invalidate()
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse(ErrorCoder.USER_NOT_EXIST))
        }

        return ResponseEntity.ok(tasksList)
    }
}
