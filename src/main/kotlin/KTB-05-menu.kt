package org.example

import java.io.File
import java.io.IOException

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int = 0
)

fun main() {
    val wordsFile: File = File("words.txt")

    try {
        val lines: List<String> = wordsFile.readLines()
        for (line in lines) {
            val parts = line.split("|")
            if (parts.size >= 2) {
                val correctAnswers = parts.getOrNull(2)?.toIntOrNull() ?: 0
                val word = Word(original = parts[0], translate = parts[1], correctAnswersCount = correctAnswers)
                println(word)
            } else {
                println("Пропущена некорректная строка: $line")
            }
        }
    } catch (e: IOException) {
        println("Не найден файл ${e.message}.")
    }

    println(
        "Выберите один из пунктов меню.\n" +
                "Меню:\n" +
                "1 - Учить слова\n" +
                "2 - Статистика\n" +
                "0 - Выход\n"
    )
    while (true) {
        val input = readLine() ?: continue
        when (input) {
            "1" -> {
                println("Вы выбрали пункт 'Учить слова'")
                break
            }
            "2" -> {
                println("Вы выбрали пункт 'Статистика'")
                break
            }
            "0" -> break
            else -> println("Вы введи некорректный символ")
        }
    }

}