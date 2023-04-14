package com.raunakjodhawat
package controllers

import akka.actor.{Actor, ActorLogging}
import models.Todo

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
}

class TodoActor extends Actor with ActorLogging {
  import TodoActor._
  var todos = Map[Int, Todo]()
  var lastTodoId = 0
  override def receive: Receive = {
    case GetAllTodos =>
      sender() ! todos.values.toList
    case GetTodoById(id) =>
      sender() ! todos.get(id)
    case CreateTodo(todo) =>
      todos = todos + (lastTodoId -> todo)
      sender() ! CreateTodoSuccess
      lastTodoId += 1
    case UpdateTodo(id, todo) =>
      val oldTodo = todos.get(id)
      oldTodo match {
        case Some(t) =>
          todos = todos + (id -> todo)
          sender() ! UpdateSuccess
        case None =>
          sender() ! UpdateFailure
      }

    case DeleteTodo(id) =>
      todos = todos - id
      sender() ! DeleteComplete

    case ChangeStatus(id) =>
      val todo = todos.get(id)
      todo match {
        case Some(t) => {
          todos = todos + (id -> Todo(t.title, t.description, !t.isComplete))
          sender() ! ChangeStatusSuccess
        }
        case None =>
          sender() ! ChangeStatusFailure
      }
  }
}
