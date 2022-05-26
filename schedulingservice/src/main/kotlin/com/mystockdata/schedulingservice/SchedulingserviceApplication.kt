package com.mystockdata.schedulingservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class SchedulingserviceApplication{

}

fun main(args: Array<String>) {
    runApplication<SchedulingserviceApplication>(*args)
}
