package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(private val botToken: String) {
    private val baseUrl = "$BASE_URL$botToken/"

    fun getUpdates(updateId: Int): String {
        val url = "${baseUrl}getUpdates?offset=$updateId"
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder().uri(URI.create(url)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMessage(chatId: Long, text: String): String {
        val url = "${baseUrl}sendMessage"
        val jsonBody = """{"chat_id":$chatId,"text":"$text"}"""
        val client = HttpClient.newHttpClient()

        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMenu(chatId: Long, text: String): String {
        val url = "${baseUrl}sendMessage"
        val jsonBody = """{"chat_id":$chatId,"text":"$text"}"""

        val sendMenuBody = """
            {
            "chat_id": $chatId,
            "text": "Основное меню",
            "reply_markup": {
            "inline_keyboard": [
            [
            {
            "text": "Изучать слова",
            "callback_data": "dat1"
            },
            {
            "text": "Статистика",
            "callback_data": "dat2"
            },
            {
            "text": "Выход",
            "callback_data": "exit"
            },
            ]
            ]
            }
            }
        """.trimIndent()

        val client = HttpClient.newHttpClient()

        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}