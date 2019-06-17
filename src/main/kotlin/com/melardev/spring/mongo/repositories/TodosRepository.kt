package com.melardev.spring.mongo.repositories

import com.melardev.spring.mongo.entities.Todo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TodosRepository : MongoRepository<Todo, String> {


    // this will not work, Spring data would still treat this
    // method as a query method and the name is not meaningful at all
    // @Query(fields = "{description: 0}", exists = true)

    // Get all documents, all fields except description
    // @Query(fields = "{description: 0}", value = "{id: {$exists:true}}")
    // Get all documents, specified fields only. Id:1 is optional, if skipped then it will be 1 anyways
    @Query(fields = "{id: 1, title: 1, completed:1, createdAt: 1, updatedAt:1}", value = "{id: {\$exists:true}}")
    fun findAllHqlSummary(): List<Todo>

    // This is treated as a query method!!! even using @Query, because we have only set fields arg, and not value
    @Query(fields = "{description:0}")
    fun findByIsCompletedFalse(): List<Todo>

    // This is not a query method, why? notice the value arg is set.
    @Query(fields = "{description:0}", value = "{isCompleted: false}")
    fun findByCompletedFalseHql(): List<Todo>

    // This is a Spring Data query method
    @Query(fields = "{description:0}")
    fun findByIsCompletedIsTrue(): List<Todo>

    @Query(fields = "{description:0}", value = "{isCompleted: true}")
    fun findByCompletedIsTrueHql(): List<Todo>

    fun findByIsCompleted(completed: Boolean): List<Todo>
    fun findByIsCompletedIs(completed: Boolean): List<Todo>

    @Query(fields = "{description:0}", value = "{isCompleted: ?0}")
    fun findByHqlIsCompleted(completed: Boolean): List<Todo>

    fun findByIsCompletedTrue(): List<Todo>

    fun findByIsCompletedIsFalse(): List<Todo>

    fun findByTitleContains(title: String): List<Todo>

    fun findByDescriptionContains(description: String): List<Todo>

    fun findByTitleAndDescription(title: String, description: String): Todo

    fun findByCreatedAtAfter(date: LocalDateTime): List<Todo>

    fun findByCreatedAtBefore(date: LocalDateTime): List<Todo>

}