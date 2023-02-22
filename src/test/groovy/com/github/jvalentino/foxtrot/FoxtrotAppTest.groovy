package com.github.jvalentino.foxtrot

import com.github.jvalentino.foxtrot.FoxtrotApp
import org.springframework.boot.SpringApplication
import spock.lang.Specification

class FoxtrotAppTest extends Specification {

    def setup() {
        GroovyMock(SpringApplication, global:true)
    }

    def "test main"() {
        when:
        FoxtrotApp.main(null)

        then:
        1 * SpringApplication.run(FoxtrotApp, null)
    }

}
