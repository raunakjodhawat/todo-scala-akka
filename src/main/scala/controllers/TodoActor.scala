package com.raunakjodhawat
package controllers

import akka.actor.ActorLogging
import models.Todo

import akka.persistence.{PersistentActor, RecoveryCompleted}

trait UpdateResponse {
  val updateSuccess: Boolean
}

trait ChangeStatusResponse {
  val changeStatusSuccess: Boolean
}
object TodoActor {
  case object GetAllTodos
  case class GetTodoById(id: Int)
  case class CreateTodo(todo: Todo)
  case object CreateTodoSuccess
  case class UpdateTodo(id: Int, todo: Todo)
  case object UpdateSuccess extends UpdateResponse {
    override val updateSuccess: Boolean = true
  }
  case object UpdateFailure extends UpdateResponse {
    override val updateSuccess: Boolean = false
  }

  case class DeleteTodo(id: Int)
  case object DeleteComplete
  case class ChangeStatus(id: Int)

  case object ChangeStatusSuccess extends ChangeStatusResponse {
    override val changeStatusSuccess: Boolean = true
  }

  case object ChangeStatusFailure extends ChangeStatusResponse {
    override val changeStatusSuccess: Boolean = false
  }

  case class PersistenceData(lastToDoId: Int, todos: Map[Int, Todo])
}

class TodoActor extends PersistentActor with ActorLogging {
  import TodoActor._
  var todos = Map[Int, Todo]()
  var lastTodoId = -1
  override def onPersistFailure(
      cause: Throwable,
      event: Any,
      seqNr: Long
  ): Unit = {
    log.error(s"Failed to persist data: ${cause.getMessage}")
    super.onPersistFailure(cause, event, seqNr)
  }
  override def receiveRecover: Receive = {
    case PersistenceData(id, persistedTodos) => {
      todos = persistedTodos
      lastTodoId = id
    }
    case RecoveryCompleted =>
      log.info("recovery completed")
  }

  override def receiveCommand: Receive = {
    case GetAllTodos =>
      sender() ! todos.values.toList
    case GetTodoById(id) =>
      sender() ! todos.get(id)
    case CreateTodo(todo) =>
      lastTodoId += 1
      todos = todos + (lastTodoId -> todo)
      persist(PersistenceData(lastTodoId, todos)) { _ =>
        sender() ! CreateTodoSuccess
      }
    case UpdateTodo(id, todo) =>
      val oldTodo = todos.get(id)
      oldTodo match {
        case Some(_) =>
          todos = todos + (id -> todo)
          persist(PersistenceData(lastTodoId, todos)) { _ =>
            sender() ! UpdateSuccess
          }
        case None =>
          sender() ! UpdateFailure
      }

    case DeleteTodo(id) =>
      todos = todos - id
      persist(PersistenceData(lastTodoId, todos)) { _ =>
        sender() ! DeleteComplete
      }

    case ChangeStatus(id) =>
      val todo = todos.get(id)
      todo match {
        case Some(t) =>
          todos = todos + (id -> Todo(t.title, t.description, !t.isComplete))
          persist(PersistenceData(lastTodoId, todos)) { _ =>
            sender() ! ChangeStatusSuccess
          }
        case None =>
          sender() ! ChangeStatusFailure
      }
  }

  override def persistenceId: String = "leveldb-persistence-actor"
}
