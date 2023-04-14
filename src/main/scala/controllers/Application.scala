package com.raunakjodhawat
package controllers

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import models.{Todo, TodoJsonSerializer}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.pattern.ask
import akka.util.Timeout
import com.raunakjodhawat.controllers
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object Application extends App with TodoJsonSerializer with SprayJsonSupport {
  import TodoActor._
  import system.dispatcher
  implicit val system = ActorSystem("todo-actor-system")
  implicit val timeout = Timeout(2.seconds)

  val todoActor = system.actorOf(Props[TodoActor], "todo-actor")
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
        complete(
          (todoActor ? ChangeStatus(id)).mapTo[ChangeStatusResponse].map {
            changeStatusResponse =>
              if (changeStatusResponse.changeStatusSuccess) {
                StatusCodes.OK
              } else {
                StatusCodes.BadRequest
              }
          }
        )
      }
    } ~ get {
      (path(IntNumber) | parameter(Symbol("id").as[Int])) { id =>
        complete((todoActor ? GetTodoById(id)).mapTo[Option[Todo]])
      } ~ pathEndOrSingleSlash {
        complete((todoActor ? GetAllTodos).mapTo[List[Todo]])
      }
    } ~ put {
      (path(IntNumber) | parameter(Symbol("id").as[Int])) { id =>
        entity(as[Todo]) { todo =>
          complete(
            (todoActor ? UpdateTodo(id, todo)).mapTo[UpdateResponse].map {
              updateResponse =>
                if (updateResponse.updateSuccess) {
                  StatusCodes.OK
                } else {
                  StatusCodes.BadRequest
                }
            }
          )
        }
      }
    } ~ post {
      entity(as[Todo]) { todo =>
        {
          complete((todoActor ? CreateTodo(todo)).map(_ => StatusCodes.Created))
        }
      }
    } ~ delete {
      (path(IntNumber) | parameter(Symbol("id").as[Int])) { id =>
        complete((todoActor ? DeleteTodo(id)).map(_ => StatusCodes.OK))
      }
    }
  }

  Http().newServerAt("localhost", 8080).bind(route)

}
