package org.example

import java.io.File

fun main() {
    val file = File("words.txt")
    val lines = file.readLines()

    for (line in lines) {
        println(line)
    }
}