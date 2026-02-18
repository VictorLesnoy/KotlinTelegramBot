package org.example

import java.io.File
import java.io.IOException

fun main() {
    val file = File("words.txt")

    try {
        val lines = file.readLines()
        for (line in lines) {
            println(line)
        }
    } catch (e: IOException) {
        println("Не найден файл ${e.message}.")
    }
}