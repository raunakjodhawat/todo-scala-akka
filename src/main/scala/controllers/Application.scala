package com.raunakjodhawat
package controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object Application extends App {
  implicit val system = ActorSystem("todo-actor-system")

  /*
  prefix: /api/todo

  GET /api/todo => get all todos
  GET /api/todo/(X) or /api/todo?id=X => get todo with id X
  PUT /api/todo/(X) or /api/todo?id=X => update todo
  POST /api/todo => create a new todo
  PUT /api/todo/changeStatus/(x) or /api/todo/changeStatus?id=X => change completion status
  DELETE /api/todo/(x) or /api/todo?id=X => delete a todo
   */
  val route: Route = pathPrefix("api" / "todo") {
    (pathPrefix("changeStatus") & put) {
      (path(IntNumber) | parameter(Symbol("id").as[Int])) { id =>
        complete(s"changeStatus for: $id")
      }
    } ~ get {
      (path(IntNumber) | parameter(Symbol("id").as[Int])) { id =>
        complete(s"get todo with a id: $id")
      } ~ pathEndOrSingleSlash {
        complete("get all todo")
      }
    } ~ put {
      (path(IntNumber) | parameter(Symbol("id").as[Int])) { id =>
        complete(s"update todo with id: $id")
      }
    } ~ post {
      complete("Create a new todo")
    } ~ delete {
      (path(IntNumber) | parameter(Symbol("id").as[Int])) { id =>
        complete(s"delete todo with id: $id")
      }
    }
  }

  Http().newServerAt("localhost", 8080).bind(route)

}
