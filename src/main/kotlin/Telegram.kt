package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val BASE_URL = "https://api.telegram.org/bot"

fun main(args: Array<String>) {

    val updateIdRegex = Regex("\"update_id\":(\\d+)")
    val textRegex = Regex("\"text\":\"(.+?)\"")
    val chatIdRegex = Regex("\"chat\":\\{\"id\":(-?\\d+)")

    val botToken = args[0]
    var updateId = 0

    val botService = TelegramBotService(botToken)

    val botInfo: String = getBotInfo(botToken)
    println("Информация о боте (getMe):")
    println(botInfo)

    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(2000)
        val updates: String = botService.getUpdates(updateId)
        println(updates)

        val ids = updateIdRegex.findAll(updates)
            .mapNotNull { it.groupValues.getOrNull(1)?.toIntOrNull() }
            .toList()
        if (ids.isNotEmpty()) updateId = (ids.maxOrNull() ?: updateId) + 1

        val chatIdMatch = chatIdRegex.find(updates)
        val textMatch = textRegex.find(updates)

        if (chatIdMatch != null && textMatch != null) {
            val chatId = chatIdMatch.groupValues[1].toLong()
            val text = textMatch.groupValues[1]
            println("Получено сообщение: '$text' от chat_id=$chatId")

            if (text == "Hello") {
                botService.sendMessage(chatId, text)
                println("Ответ отправлен: '$text'")
            }

            if (text == "menu") {
                botService.sendMenu(chatId, text)
                println("Отправлено главное меню")
            }
        }
    }
}

fun getBotInfo(botToken: String): String {
    val urlGetMe = "$BASE_URL$botToken/getMe"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetMe)).build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    return response.body()
}