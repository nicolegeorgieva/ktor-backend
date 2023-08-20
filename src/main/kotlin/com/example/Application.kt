package com.example

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun main() {
    val port = try {
        System.getenv("PORT").toInt()
    } catch (e: Exception) {
        8080
    }

    embeddedServer(Netty, port = port, module = Application::module)
        .start(wait = true)
}

val toDoList = mutableListOf<String>()

@Serializable
data class ToDoResponse(
    val toDo: List<String>
)

@Serializable
data class AddToDoRequest(
    val task: String
)

@Serializable
data class RemoveToDoRequest(
    val item: Int
)

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    configureRouting()
}

@Serializable
data class Person(
    val firstName: String,
    val lastName: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val isLogged: Boolean
)

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/version") {
            call.respondText("Version 01")
        }

        get("/person") {
            val person1 = Person(firstName = "Iliyan", lastName = "Germanov")
            call.respond(person1)
        }

        post("/login") {
            val request = call.receive<LoginRequest>()

            if (request.email.isNotBlank() && request.password.isNotBlank()) {
                call.respond(LoginResponse(true))
            } else {
                call.respond(LoginResponse(false))
            }
        }

        get("/todo") {
            call.respond(ToDoResponse(toDoList))
        }

        post("/add-todo") {
            val request = call.receive<AddToDoRequest>()

            if (request.task.isNotBlank()) {
                toDoList.add(request.task.trim())
            }

            call.respond(ToDoResponse(toDoList))
        }

        post("/remove-todo") {
            val removeRequest = call.receive<RemoveToDoRequest>()

            toDoList.removeAt(removeRequest.item)

            call.respond(ToDoResponse(toDoList))
        }
    }
}

