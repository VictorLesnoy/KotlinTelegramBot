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

fun saveDictionary(dictionary: List<Word>, filename: String) {
    try {
        val fileContent = dictionary.joinToString("\n") {
            "${it.original}|${it.translate}|${it.correctAnswersCount}"
        }
        File(filename).writeText(fileContent)
    } catch (e: IOException) {
        println("Ошибка записи в файл: ${e.message}")
    }
}

fun main() {

    val dictionary = loadDictionary("words.txt").toMutableList()
    val words = dictionary.size

    println("Слов в словаре: ${dictionary.size}")
    dictionary.forEach { println("${it.original} → ${it.translate}, count: ${it.correctAnswersCount}") }

    while (true) {

        println(
            "Выберите один из пунктов меню.\n" +
                    "Меню:\n" +
                    "1 - Учить слова\n" +
                    "2 - Статистика\n" +
                    "0 - Выход\n"
        )

        val menuOption = readlnOrNull() ?: continue

        when (menuOption) {

            "1" -> {
                while (true) {
                    val notLearnedWords = dictionary.filter { it.correctAnswersCount < CORRECT_ANSWERS_REQUIRED }

                    if (notLearnedWords.isEmpty()) {
                        println("Все слова выучены!")
                        break
                    }

                    val questionWords = if (notLearnedWords.size >= NUMBER_OF_VARIANTS) {
                        notLearnedWords.shuffled().take(NUMBER_OF_VARIANTS)
                    } else {
                        val additionalWords = dictionary
                            .filter { it !in notLearnedWords }
                            .shuffled()
                            .take(4 - notLearnedWords.size)

                        (notLearnedWords + additionalWords).shuffled()
                    }

                    val correctWord = questionWords.random()
                    val variants = questionWords.map { it.translate }.toMutableList()
                    val correctAnswer = correctWord.translate

                    if (variants.size < 4) {
                        val others = dictionary.map { it.translate }.filter { it !in variants }.shuffled()
                        for (word in others) {
                            if (variants.size >= 4) break
                            variants.add(word)
                        }
                    }

                    val shuffledVariants = variants.shuffled()

                    println("\nВыберите правильный перевод для слова: '${correctWord.original}'")
                    shuffledVariants.forEachIndexed { index, variant ->
                        println("${index + 1}: $variant")
                    }
                    println("-----------\n0 - Меню")

                    val input = readlnOrNull()?.toIntOrNull()
                    if (input == 0) break
                    if (input == null || input !in 1..shuffledVariants.size) {
                        println("Неверный ввод, попробуйте снова.")
                        continue
                    }

                    if (shuffledVariants[input - 1] == correctAnswer) {
                        println("Верно!")
                        val idx = dictionary.indexOfFirst { it.original == correctWord.original }
                        if (idx != -1 && dictionary[idx].correctAnswersCount < CORRECT_ANSWERS_REQUIRED) {
                            dictionary[idx] =
                                dictionary[idx].copy(correctAnswersCount = dictionary[idx].correctAnswersCount + 1)
                            saveDictionary(dictionary, "words.txt")
                        }
                    } else {
                        println("Неправильно. Правильный ответ: $correctAnswer")
                    }

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
const val NUMBER_OF_VARIANTS = 4