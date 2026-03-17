package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val BASE_URL = "https://api.telegram.org/bot/"

private val UPDATE_ID_REGEX = Regex("\"update_id\":(\\d+)")
private val TEXT_REGEX = Regex("\"text\":\"(.+?)\"")

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0

    val botInfo: String = getBotInfo(botToken)
    println("Информация о боте (getMe):")
    println(botInfo)

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
        println(updates)

        val ids = UPDATE_ID_REGEX.findAll(updates)
            .mapNotNull { it.groupValues.getOrNull(1)?.toIntOrNull() }
            .toList()
        if (ids.isNotEmpty()) updateId = (ids.maxOrNull() ?: updateId) + 1

        val textMatch = TEXT_REGEX.find(updates)
        if (textMatch != null) {
            println(textMatch.groupValues[1])
        }
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdates = "$BASE_URL$botToken/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}

fun getBotInfo(botToken: String): String {
    val urlGetMe = "$BASE_URL$botToken/getMe"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetMe)).build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}