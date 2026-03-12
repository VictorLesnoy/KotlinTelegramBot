package org.example

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0
)

fun Question.asConsoleString(): String {
    val variants = this.variants
        .mapIndexed { index: Int, word: Word -> "${index + 1} - ${word.translate}" }
        .joinToString(separator = "\n")
    return this.correctAnswer.original + "\n" + variants + "\n0 - выйти в меню"
}

fun main() {

    val trainer = LearnWordsTrainer()

    while (true) {

        println(
            "Выберите один из пунктов меню.\n" +
                    "Меню:\n" +
                    "1 - Учить слова\n" +
                    "2 - Статистика\n" +
                    "0 - Выход\n"
        )

        when (readln().toIntOrNull()) {

            1 -> {
                while (true) {
                    val question = trainer.getNextQuestion()
                    if (question == null) {
                        println("Все слова выучены!")
                        break
                    } else {
                        println(question.asConsoleString())

                        val userAnswerInput = readln()
                        if (userAnswerInput == "0") break
                        val userAnswerIndex = userAnswerInput.toIntOrNull()?.minus(1)

                        when (trainer.checkAnswer(userAnswerIndex)) {
                            LearnWordsTrainer.CheckAnswerResult.CORRECT -> println("Верно!\n")
                            LearnWordsTrainer.CheckAnswerResult.INCORRECT -> {
                                println("Неправильно. ${question.correctAnswer.original} - это ${question.correctAnswer.translate}\n")
                            }

                            LearnWordsTrainer.CheckAnswerResult.INVALID_INPUT -> {
                            }
                        }
                    }
                }
            }

            2 -> {
                val statistics = trainer.getStatistics()
                println("Выучено ${statistics.learned} из ${statistics.total} слов | ${statistics.percent}%")
            }

            0 -> break
            else -> println("Введите число 1, 2 или 0")

        }
    }
}

const val CORRECT_ANSWERS_REQUIRED = 3
const val NUMBER_OF_VARIANTS = 4