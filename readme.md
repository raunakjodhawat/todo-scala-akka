# Todo App using Akka Http with Scala

Actor based Akka Http to build a simple yet powerful application

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