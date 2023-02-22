package com.github.jvalentino.foxtrot.dto

import com.github.jvalentino.foxtrot.entity.Doc
import groovy.transform.CompileDynamic

/**
 * Represents the content for the dashboard
 * @author john.valentino
 */
@CompileDynamic
class DashboardDto {

    List<Doc> documents = []

}
