package com.mystockdata.composerservice

fun printMultiDimensionalList(list: List<List<String>>) {
    for (subArr in list) {
        var row = ""
        for ((index, str) in subArr.withIndex()) {
            row += if (index < subArr.size - 1) "$str,\t" else str
        }
        println(row)
    }
}