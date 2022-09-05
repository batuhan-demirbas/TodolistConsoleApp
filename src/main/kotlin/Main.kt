import kotlinx.datetime.*

fun main() {
    while (true) {
        println("Input an action (add, print, edit, delete, end):")
        when (readln().lowercase()) {
            "add" -> Tasks.add()
            "print" -> Tasks.print()
            "edit" -> Tasks.edit()
            "delete" -> Tasks.delete()
            "end" -> break
            else -> println("The input action is invalid")
        }
    }
    println("Tasklist exiting!")
}

class Task(
    var list: List<String>,
    var priority: String,
    var dataTime: LocalDateTime
) {
    var dueTag: String = newDueTag()
    fun newDueTag(): String {
        val i = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date.until(dataTime.date, DateTimeUnit.DAY)
        return if (i < 0) "\u001B[101m \u001B[0m"
        else if (i > 0) "\u001B[102m \u001B[0m"
        else "\u001B[103m \u001B[0m"
    }

    override fun toString(): String = buildString {
        println(" ${dataTime.toString().replace("T", " | ")} | $priority | $dueTag |${list[0].padEnd(44, ' ')}|")
        list.forEach { if (it != list[0]) append("|    |            |       |   |   |${it.padEnd(44, ' ')}|\n") }

        //append(dataTime.toString().replace('T', ' ') + " $priority $dueTag\n")
        //list.forEach { append("   $it\n") }
    }
}

object Tasks {
    private val list = mutableListOf<Task>()

    fun add() {
        val priority = askPriority()
        val dataTime = askDate().askTime()
        val inputTasks = askTasks()

        if (inputTasks.isEmpty()) println("The task is blank")
        else list.add(Task(inputTasks, priority, dataTime))
    }

    fun print(): Boolean {
        val line = "+----+------------+-------+---+---+--------------------------------------------+"
        val header = "| N  |    Date    | Time  | P | D |                   Task                     |"
        if (list.isEmpty()) println("No tasks have been input").also { return true }
        println(line)
        println(header)
        println(line)
        for (i in list.indices) {
            val i1 = i + 1
            val task = list[i]
            print(
                (if (i < 9) "| $i1  |"
                else "| $i1 |")
            )
            print(task)
            println(line)
        }
        return false
    } // return true if list IS empty

    fun edit() {
        if (print()) return

        val task = list[askTaskIndex()]

        while (true) {
            println("Input a field to edit (priority, date, time, task):")
            when (readln().lowercase()) {
                "priority" -> task.priority = askPriority()
                "task" -> task.list = askTasks()
                "date" -> with(task) {
                    dataTime = askDate()
                        .atTime(dataTime.hour, dataTime.minute)
                    dueTag = newDueTag()
                }

                "time" -> with(task) {
                    dataTime = dataTime.date.askTime()
                    dueTag = newDueTag()
                }

                else -> {
                    println("Invalid field")
                    continue
                }
            }
            println("The task is changed")
            return
        }
    }

    fun delete() {
        if (print()) return
        list.removeAt(askTaskIndex()).also { println("The task is deleted") }
    }

    private fun askTasks() = buildList {
        println("Input a new task (enter a blank line to end):")
        while (true) {
            when (val input = readln().trim()) {
                "" -> break
                else -> if (input.length <= 44) {
                    add(input)
                } else {
                    add(input.slice(0..43))
                    add(input.slice(44..input.lastIndex))
                }
            }
        }
    }

    private fun askTaskIndex(): Int {
        println("Input the task number (1-${list.size}):")
        return try {
            val input = readln().toInt()
            require(input in IntRange(1, list.size))
            input - 1
        } catch (e: Exception) {
            println("Invalid task number")
            askTaskIndex()
        }
    }

    private fun askPriority(): String {
        var priority: String? = null
        do {
            println("Input the task priority (C, H, N, L):")

//            val input = readln().uppercase()

            priority = when (readln().uppercase()) {
                "C" -> "\u001B[101m \u001B[0m"
                "H" -> "\u001B[103m \u001B[0m"
                "N" -> "\u001B[102m \u001B[0m"
                "L" -> "\u001B[104m \u001B[0m"
                else -> continue
            }

//            if (input in arrayOf("C", "H", "N", "L"))
//                priority = input.first()

        } while (priority == null)
        return priority
    }

    private fun askDate(): LocalDate {
        println("Input the date (yyyy-mm-dd):")
        return try {
            val (y, m, d) = readln().split('-').map { it.toInt() }
            LocalDate(y, m, d)
        } catch (e: Exception) {
            println("The input date is invalid")
            askDate()
        }
    }

    private fun LocalDate.askTime(): LocalDateTime {
        println("Input the time (hh:mm):")
        return try {
            val (h, m) = readln().split(':').map { it.toInt() }
            this.atTime(h, m)
        } catch (e: Exception) {
            println("The input time is invalid")
            askTime()
        }
    }
}