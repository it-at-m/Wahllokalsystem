# Erstellen der Task List

Beim initialen Laden der WLS-GUI (Wahllokalsystem) muss für die Startseite eine Liste von Tasks erstellt werden.
Diese Liste zeigt an, wie viele Tasks noch geladen werden müssen und ob die zu ladenden Tasks erfolgreich abgeschlossen wurden oder nicht.
Zur Erstellung der Tasks werden Factories verwendet, die von einem `TaskFactoryInterface` erben.

Das initiale Erstellen der Tasks erfolgt nur einmal beim Start der Anwendung. Danach sind alle Tasks über den `TaskManager` abrufbar.

Das `TaskFactoryInterface` enthält eine `createTasks` Methode, die jede Factory implementieren und zugänglich machen muss.


Für die Umsetzung der Factories wurden Composables verwendet.

```ts
export function useFactory(): TaskFactoryInterface {
    createTasks(taskFactoryContext: TaskFactoryContext): Task[]
}
```

Der Ablauf, wie Tasks erstellt werden kann wie folgt abgebildet werden:

```mermaid
sequenceDiagram
    participant taskManagerStore
    participant taskService
    participant TaskFactoryInterface
    participant TaskFactoryXY1
    participant TaskFactoryXY2
    participant TaskFactoryXY3

    taskManagerStore ->> taskService: initTaskList()
    taskService -->> TaskFactoryXY1: createTasks()
    TaskFactoryXY1 -->> taskService: Tasks[]
    taskService -->> TaskFactoryXY2: createTasks()
    TaskFactoryXY2 -->> taskService: Tasks[]
    taskService -->> TaskFactoryXY3: createTasks()
    TaskFactoryXY3 -->> taskService: Tasks[]
    taskService ->> taskManagerStore: Tasks[]

```
