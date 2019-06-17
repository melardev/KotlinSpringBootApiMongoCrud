package com.melardev.spring.mongo.controllers

import com.melardev.spring.mongo.dtos.responses.ErrorResponse
import com.melardev.spring.mongo.entities.Todo
import com.melardev.spring.mongo.repositories.TodosRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.ZoneId
import java.util.*
import java.util.function.Consumer
import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/todos")
class TodosController(@Autowired
                      private val todosRepository: TodosRepository) {

    @GetMapping
    fun index(): Iterable<Todo> {
        return this.todosRepository.findAllHqlSummary()
    }

    @GetMapping("/pending")
    fun getPending(): Iterable<Todo> {
        return this.todosRepository.findByCompletedFalseHql()
    }

    @GetMapping("/completed")
    fun getCompleted(): List<Todo> {
        return todosRepository.findByCompletedIsTrueHql()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: String): ResponseEntity<*> {
        val todo = this.todosRepository.findById(id)

        return when {
            todo.isPresent -> ResponseEntity(todo.get(), HttpStatus.OK)
            else -> ResponseEntity(ErrorResponse("Not Found"), HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping
    fun create(@Valid @RequestBody todo: Todo): ResponseEntity<Todo> {
        return ResponseEntity(todosRepository.save(todo), HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: String,
               @RequestBody todoInput: Todo): ResponseEntity<*> {
        val optionalTodo = todosRepository.findById(id)
        return if (optionalTodo.isPresent()) {
            val todo = optionalTodo.get()
            todo.title = todoInput.title

            val description = todoInput.description
            if (description != null)
                todo.description = description

            todo.isCompleted = todoInput.isCompleted
            ResponseEntity.ok(todosRepository.save(optionalTodo.get()))
        } else {
            ResponseEntity<Any>(ErrorResponse("This todo does not exist"), HttpStatus.NOT_FOUND)
        }
    }


    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: String): ResponseEntity<*> {
        val todo = todosRepository.findById(id)
        return if (todo.isPresent) {
            todosRepository.delete(todo.get())
            ResponseEntity.noContent().build<Any>()
        } else {
            ResponseEntity<Any>(ErrorResponse("This todo does not exist"), HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping
    fun deleteAll(): ResponseEntity<*> {
        todosRepository.deleteAll()
        return ResponseEntity<Any>(HttpStatus.NO_CONTENT)
    }

    @GetMapping(value = ["/after/{date}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getByDateAfter(@PathVariable("date") @DateTimeFormat(pattern = "dd-MM-yyyy") date: Date): List<Todo> {
        val articlesIterable = todosRepository.findByCreatedAtAfter(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
        val articleList = ArrayList<Todo>()
        articlesIterable.forEach(Consumer<Todo> { articleList.add(it) })
        return articleList
    }

    @GetMapping(value = ["/before/{date}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getByDateBefore(@PathVariable("date") @DateTimeFormat(pattern = "dd-MM-yyyy") date: Date): List<Todo> {
        val articlesIterable = todosRepository.findByCreatedAtBefore(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
        val articleList = ArrayList<Todo>()
        articlesIterable.forEach(Consumer<Todo> { articleList.add(it) })
        return articleList
    }

}
