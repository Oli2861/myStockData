package com.mystockdata.composerservice

fun <T>printMultiDimensionalList(list: List<List<T>>) {
    for (subArr in list) {
        var row = ""
        for ((index, t) in subArr.withIndex()) {
            row += if (index < subArr.size - 1) "$t,\t" else t
        }
        println(row)
    }
}