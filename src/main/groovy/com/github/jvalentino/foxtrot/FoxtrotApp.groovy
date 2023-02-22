package com.github.jvalentino.foxtrot

import groovy.transform.CompileDynamic
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Main application
 */
@SpringBootApplication
@CompileDynamic
class FoxtrotApp {

    static void main(String[] args) {
        SpringApplication.run(FoxtrotApp, args)
    }

}
