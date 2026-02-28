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

/*fun learn(dictionary: MutableList<Word>): Boolean {
    val notLearnedWords = dictionary.filter { it.correctAnswersCount < CORRECT_ANSWERS_REQUIRED }
    if (notLearnedWords.isEmpty()) {
        println("Все слова выучены!")
        return false
    }

    val randomWord = notLearnedWords.random()
    val correctAnswer = randomWord.translate
    val variants = mutableSetOf<String>()

    val candidates = notLearnedWords.filter { it.translate != correctAnswer }.map { it.translate }.toMutableList()
    while (variants.size < 3 && candidates.isNotEmpty()) {
        variants.add(candidates.removeAt(0))
    }

    val allCandidates =
        dictionary.map { it.translate }.filter { it != correctAnswer && it !in variants }.toMutableList()
    while (variants.size < 3 && allCandidates.isNotEmpty()) {
        variants.add(allCandidates.removeAt(0))
    }

    variants.add(correctAnswer)
    val shuffledVariants = variants.shuffled()

    println("Выберите правильный перевод для слова: '${randomWord.original}' (0 - выход)")
    shuffledVariants.forEachIndexed { index, variant ->
        println("${index + 1}: $variant")
    }

    val input = readlnOrNull()?.toIntOrNull()
    if (input == 0) {
        println("Выход из режима обучения.")
        return false
    }
    if (input == null || input !in 1..shuffledVariants.size) {
        println("Неверный ввод, попробуйте снова.")
        return true
    }

    if (shuffledVariants[input - 1] == correctAnswer) {
        println("Верно!")
        val index = dictionary.indexOf(randomWord)
        dictionary[index] = randomWord.copy(correctAnswersCount = randomWord.correctAnswersCount + 1)
    } else {
        println("Неправильно. Правильный ответ: $correctAnswer")
    }

    return true
}*/

fun main() {

    val dictionary = loadDictionary("words.txt").toMutableList()
    val words = dictionary.size

    println("Слов в словаре: ${dictionary.size}")
    dictionary.forEach { println("${it.original} → ${it.translate}, count: ${it.correctAnswersCount}") }

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
                while (true) { // явный while(true) читается чище и понятнее `while (learn(dictionary)) { }`
                    val notLearnedWords = dictionary.filter { it.correctAnswersCount < CORRECT_ANSWERS_REQUIRED } // формируем список невыученных слов

                    if (notLearnedWords.isEmpty()) {
                        println("Все слова выучены!")
                        break // выходим из цикла если все слова выучены
                    }

                    val questionWords = if (notLearnedWords.size >= 4) { // формируем список вариантов, если невыученных слов достаточно то просто список перемешиваем и берем 4
                        notLearnedWords.shuffled().take(4)
                    } else {
                        val additionalWords = dictionary // если невыученных слов меньше 4, то добираем из общего словаря
                            .filter { it !in notLearnedWords }
                            .shuffled()
                            .take(4 - notLearnedWords.size)

                        (notLearnedWords + additionalWords).shuffled()
                    }

                    val correctWord = questionWords.random() // получаем слово, которое будем спрашивать
                    val variants = questionWords.map { it.translate }.shuffled() // формируем список ответов на русском

                    // тут вывод variants

                    // в следующем уроке обработаем результаты ответа
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