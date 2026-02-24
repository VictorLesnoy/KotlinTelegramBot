package org.example

import java.io.File
import java.io.IOException

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int = 0
)

fun loadDictionary(filename: String): List<Word> {
    val dictionary = mutableListOf<Word>()
    try {
        val lines = File(filename).readLines()
        for (line in lines) {
            val parts = line.split("|")
            if (parts.size >= 2) {
                val correctAnswers = parts.getOrNull(2)?.toIntOrNull() ?: 0
                val word = Word(parts[0], parts[1], correctAnswers)
                dictionary.add(word)
            } else {
                println("Пропущена некорректная строка: $line")
            }
        }
    } catch (e: IOException) {
        println("Ошибка при чтении файла: ${e.message}")
    }
    return dictionary
}

fun main() {

    val dictionary = loadDictionary("words.txt")
    val words = dictionary.size
    val learnWordsCount = dictionary.count { it.correctAnswersCount >= THREE }
    val percentLearnedWords = {
        if (words > 0) {
            learnWordsCount * 100 / words
        }
    }
    val learnedWords = dictionary.filter { it.correctAnswersCount >= THREE }

    println(
        "Выберите один из пунктов меню.\n" +
                "Меню:\n" +
                "1 - Учить слова\n" +
                "2 - Статистика\n" +
                "0 - Выход\n"
    )

    while (true) {
        val input = readlnOrNull() ?: continue
        when (input) {

            "1" -> {
                println("Вы выбрали пункт 'Учить слова'")
            }

            "2" -> {
                println("Вы выбрали пункт 'Статистика'")
                println("Выучено $learnWordsCount из $words слов | $percentLearnedWords%")
                if (learnedWords.isNotEmpty()) {
                    println("\nСписок выученных слов:")
                    learnedWords.forEach { word ->
                        println("  ${word.original} → ${word.translate}")
                    }
                } else {
                    println("Пока нет выученных слов.")
                }
            }

            "0" -> return
            else -> println("Введите число 1, 2 или 0")

        }
    }

}

const val THREE = 3