package com.github.jvalentino.foxtrot.rest

import com.github.jvalentino.foxtrot.dto.ResultDto
import com.github.jvalentino.foxtrot.entity.AuthUser
import com.github.jvalentino.foxtrot.service.DocService
import com.github.jvalentino.foxtrot.service.UserService
import com.github.jvalentino.foxtrot.util.DateGenerator
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * Used for file upload
 */
@Controller
@Slf4j
@RestController
@CompileDynamic
class UploadRest {

    @Autowired
    DocService docService

    @Autowired
    UserService userService

    @PostMapping('/upload-file')
    ResultDto upload(@RequestParam('file') MultipartFile file) {
        AuthUser user = userService.currentLoggedInUser()
        docService.uploadNewDoc(user, file, DateGenerator.date())

        new ResultDto()
    }

}
