package com.mystockdata.composerservice.csv

enum class MissingValueHandlingStrategy {
    IGNORE,
    LAST_VALUE,
    NEXT_MATCHING
}