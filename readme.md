# Todo App using Akka Http with Scala

Persistence Actor based Akka Http to build a simple yet powerful application. All data is stored as snapshots in localDB, such that each run of the application builds on top of it's previous data.

## Uses
Following application uses concepts from Akka's (Http, Persistence, Actor Model, etc)

## Supported routes
```markdown
  GET /api/todo => get all todos
  GET /api/todo/(X) or /api/todo?id=X => get todo with id X
  PUT /api/todo/(X) or /api/todo?id=X => update todo
  POST /api/todo => create a new todo
  PUT /api/todo/changeStatus/(x) or /api/todo/changeStatus?id=X => change completion status
  DELETE /api/todo/(x) or /api/todo?id=X => delete a todo
```

See [Insomnia.json](./insomnia.json) for all requests/responses documentation.

### Disclaimer
Works with JDK 11. You might encounter some issues pertaining to levelDB with jdk 17+
