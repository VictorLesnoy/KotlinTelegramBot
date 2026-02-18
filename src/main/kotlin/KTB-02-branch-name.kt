package org.example

import java.io.File
import java.io.IOException

data class Word(val original: String,
                val translate: String,
                val correctAnswersCount: Int = 0)

fun main() {
    val wordsFile: File = File("words.txt")

    try {
        val lines: List<String> = wordsFile.readLines()
        for (line in lines) {
            val dictionary = line.split("|")
            val word = Word(original = dictionary[0], translate = dictionary[1])
            println(dictionary)
        }
    } catch (e: IOException) {
        println("Не найден файл ${e.message}.")
    }

}