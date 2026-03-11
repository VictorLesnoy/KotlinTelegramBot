package org.example

import java.io.File

data class Statistics(
    val learned: Int,
    val total: Int,
    val percent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer {

    private var question: Question? = null
    private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val learned = dictionary.filter { it.correctAnswersCount >= CORRECT_ANSWERS_REQUIRED }.size
        val total = dictionary.size
        val percent = if (total != 0) learned * 100 / total else 0
        return Statistics(learned, total, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < CORRECT_ANSWERS_REQUIRED }
        if (notLearnedList.isEmpty()) return null
        val variants = mutableListOf<Word>()
        variants += notLearnedList.shuffled()

        if (variants.size < NUMBER_OF_VARIANTS) {
            val learnedList = dictionary.filter { it.correctAnswersCount >= CORRECT_ANSWERS_REQUIRED }
                .shuffled()
                .take(NUMBER_OF_VARIANTS - variants.size)
            variants += learnedList
        }

        val questionWords = variants.take(NUMBER_OF_VARIANTS)
        val correctAnswer = questionWords.random()

        question = Question(
            variants = questionWords,
            correctAnswer = correctAnswer,
        )
        return question
    }

    fun checkAnswer(userAnswerIndex: Int?): Boolean {
        if (userAnswerIndex == null) {
            println("Пожалуйста, выберите вариант ответа числом.")
            return false
        }
        val currentQuestion = question ?: return false
        val correctAnswerId = currentQuestion.variants.indexOf(currentQuestion.correctAnswer)
        return if (userAnswerIndex == correctAnswerId) {
            currentQuestion.correctAnswer.correctAnswersCount++
            saveDictionary(dictionary)
            println("Правильно!")
            true
        } else {
            println("Неправильно!")
            false
        }
    }

    private fun loadDictionary(): List<Word> {
        val dictionary = mutableListOf<Word>()
        val wordsFile = File(FILE_NAME)
        try {
            if (!wordsFile.exists()) {
                println("Файл $FILE_NAME не найден, создаю новый.")
                wordsFile.createNewFile()
                return dictionary
            }
            wordsFile.forEachLine { line ->
                val splitLine = line.split("|")
                if (splitLine.size >= 3) {
                    val original = splitLine[0]
                    val translate = splitLine[1]
                    val count = splitLine[2].toIntOrNull() ?: 0
                    dictionary.add(Word(original, translate, count))
                } else {
                    println("Неверный формат строки: $line")
                }
            }
        } catch (e: Exception) {
            println("Ошибка при чтении файла: ${e.message}")
        }
        return dictionary
    }

    private fun saveDictionary(words: List<Word>) {
        val wordsFile = File(FILE_NAME)
        wordsFile.writeText("")
        for (word in words) {
            wordsFile.appendText("${word.original}|${word.translate}|${word.correctAnswersCount}\n")
        }
    }

}

const val FILE_NAME = "words.txt"