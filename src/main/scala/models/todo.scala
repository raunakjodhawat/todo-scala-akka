package com.raunakjodhawat
package models

import java.util.Date

object Status extends Enumeration {
  type ToDOStatus = Value

  val completed = Value("Completed")
  val pending = Value("Pending")
}
case class todo(
    title: String,
    description: String,
    createdAt: Date = new Date,
    updatedAt: Date,
    status: Status.ToDOStatus = Status.pending
)
