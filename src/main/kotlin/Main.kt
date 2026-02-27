package org.example

import java.io.File
import java.io.IOException
import java.util.Dictionary

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

fun learn(dictionary: MutableList<Word>) {
    val notLearnedWords = dictionary.filter { it.correctAnswersCount < CORRECT_ANSWERS_REQUIRED }
    val randomWord = notLearnedWords.random()
    val correctAnswer = randomWord.translate

    val variants = mutableSetOf(correctAnswer)
    while (variants.size < 4 && variants.size < dictionary.size) {
        variants.add(dictionary.random().translate)
    }
    val shuffledVariants = variants.shuffled()

    println("Выберите правильный перевод для слова: '${randomWord.original}'")
    shuffledVariants.forEachIndexed { index, variant ->
        println("${index + 1}: $variant")
    }

    val input = readlnOrNull()?.toIntOrNull()
    if (input == null || input !in 1..shuffledVariants.size) {
        println("Неверный ввод, попробуйте снова.")
        return
    }

    if (shuffledVariants[input - 1] == correctAnswer) {
        println("Верно!")
        val index = dictionary.indexOf(randomWord)
        val updatedWord = randomWord.copy(correctAnswersCount = randomWord.correctAnswersCount + 1)
        dictionary[index] = updatedWord
    } else {
        println("Неправильно. Правильный ответ: $correctAnswer")
    }
}

fun main() {

    val dictionary = loadDictionary("words.txt").toMutableList()
    val words = dictionary.size
    val notLearnedList: MutableList<String> = mutableListOf()

    while (true) {
        val notLearnedWords = dictionary.filter { it.correctAnswersCount < CORRECT_ANSWERS_REQUIRED }

            println(
                "Выберите один из пунктов меню.\n" +
                        "Меню:\n" +
                        "1 - Учить слова\n" +
                        "2 - Статистика\n" +
                        "0 - Выход\n"
            )

                val input = readlnOrNull() ?: continue

                when (input) {

                    "1" -> {
                        println("Вы выбрали пункт 'Учить слова'")
                        if (notLearnedWords.isEmpty()) {
                            println("Все слова выучены, нечего учить.")
                        } else {
                            learn(dictionary)
                        }
                    }

                    "2" -> {
                        println("Вы выбрали пункт 'Статистика'")
                        val learnWordsCount = dictionary.count { it.correctAnswersCount >= CORRECT_ANSWERS_REQUIRED }
                        val percentLearnedWords =
                            if (words > 0) {
                                learnWordsCount * 100 / words
                            } else 0

                        val learnedWords = dictionary.filter { it.correctAnswersCount >= CORRECT_ANSWERS_REQUIRED }

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

const val CORRECT_ANSWERS_REQUIRED = 3