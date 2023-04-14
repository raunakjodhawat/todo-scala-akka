package com.raunakjodhawat
package controllers

import akka.actor.{Actor, ActorLogging}
import com.sun.tools.javac.comp.Todo

object TodoActor {
  case object GetAllTodos
  case class GetTodoById(id: Int)
  case class CreateTodo(todo: Todo)
  case object CreateTodoSuccess
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
  }
}
