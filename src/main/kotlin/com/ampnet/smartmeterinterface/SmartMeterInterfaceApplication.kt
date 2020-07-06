package com.ampnet.smartmeterinterface

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SmartMeterInterfaceApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<SmartMeterInterfaceApplication>(*args)
}
