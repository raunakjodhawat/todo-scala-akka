package com.raunakjodhawat
package models

import spray.json.DefaultJsonProtocol

case class Todo(
    title: String,
    description: String,
    isComplete: Boolean = false
)

trait TodoJsonSerializer extends DefaultJsonProtocol {
  implicit val todoJson = jsonFormat3(Todo)
}
