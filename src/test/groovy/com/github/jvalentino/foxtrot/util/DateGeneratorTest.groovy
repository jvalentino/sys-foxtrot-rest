package com.github.jvalentino.foxtrot.util

import com.github.jvalentino.foxtrot.util.DateGenerator
import spock.lang.Specification

class DateGeneratorTest extends Specification {

    def "Test DateGenerator"() {
        when:
        Date result = DateGenerator.date()

        then:
        result
    }
}
