package com.skliaruk

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.io.FileInputStream
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties
import kotlin.math.roundToInt

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String
)

@Serializable
data class Project(val id: Int, val name: String)

@Serializable
data class Task(val id: Int, val name: String)

fun getCurrentTimeFormatted(): String {
    val currentTime = ZonedDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

    // Format the ZonedDateTime to the desired string
    val formattedDate = currentTime.format(formatter)
    return formattedDate
}

@Serializable
data class TimeReport(
    val project: Int,
    val task: Int,
    val amount: Int,
    val comment: String,
    val start: String = getCurrentTimeFormatted()
)

class PlanMillCLI : CliktCommand() {
    private val projectId by option("--projectId", help = "Project id").int().required()
    private val hours by option("--hours", help = "Number of hours").float().required()
    private val description by option("--desc", help = "Description of the work").required()

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true

            })
        }
    }
    private lateinit var apiUrl: String
    private lateinit var tokenUrl: String
    private lateinit var authUrl: String
    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private lateinit var accessToken: String

    // Get an access token
    @OptIn(InternalAPI::class)
    private suspend fun getToken(): TokenResponse {
        val response = client.post(
            tokenUrl
        ) {
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            body = FormDataContent(Parameters.build {
                append("grant_type", "client_credentials")
                append("authorization_uri", authUrl)
                append("token_uri", tokenUrl)
                append("client_id", clientId)
                append("client_secret", clientSecret)
            })
        }
        return response.body<TokenResponse>()
    }


    private suspend fun getProjects(name: String): List<Project> {
        try {
            val response = client.get("$apiUrl/projects") {
                url {
                    parameters.append("name", name)
                }
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
            if (response.status == HttpStatusCode.NoContent) {
                println("Project with name: $name not found")
                return emptyList()
            }

            return response.body<List<Project>>()
        } catch (e: Exception) {
            println("Error fetching projects ${e.message}")
        }

        return emptyList()
    }

    private suspend fun getTasks(projectId: Int): List<Task> {
        return client.get("$apiUrl/projects/$projectId/tasks") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
            }
        }.body<List<Task>>()
    }

    private suspend fun postTimeReport(report: TimeReport) {
        try {
            client.post("$apiUrl/timereports") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                contentType(ContentType.Application.Json)
                setBody(report)
            }
            println("Time report posted successfully.")
        } catch (e: Exception) {
            println("Error posting time report: ${e.message}")
        }
    }


    override fun run() = runBlocking {
        val properties = Properties()

        FileInputStream("config.properties").use { properties.load(it) }

        clientId = properties.getProperty("clientId")
        clientSecret = properties.getProperty("clientSecret")
        apiUrl = properties.getProperty("apiUrl")
        tokenUrl = properties.getProperty("tokenUrl")
        authUrl = properties.getProperty("authUrl")
        accessToken = getToken().accessToken


        val tasks = getTasks(projectId)
        println("Select a task:")
        tasks.forEachIndexed { index, task -> println("${index + 1}. ${task.name}") }
        val taskIndex = readln().toIntOrNull()?.minus(1) ?: throw IllegalArgumentException("Invalid task selection")
        val task = tasks[taskIndex]

        val report = TimeReport(
            project = projectId,
            task = task.id,
            amount = (hours * 60).roundToInt(),
            comment = description,
        )

        postTimeReport(report)
    }
}

fun main(args: Array<String>) = PlanMillCLI().main(args)
