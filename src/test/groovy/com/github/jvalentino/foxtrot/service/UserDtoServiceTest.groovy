package com.github.jvalentino.foxtrot.service

import com.github.jvalentino.foxtrot.dto.LoginDto
import com.github.jvalentino.foxtrot.dto.ResultDto
import com.github.jvalentino.foxtrot.entity.AuthUser
import com.github.jvalentino.foxtrot.dto.UserDto
import com.github.jvalentino.foxtrot.repo.AuthUserRepo
import com.github.jvalentino.foxtrot.service.UserService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification
import spock.lang.Subject

import javax.servlet.http.HttpSession

class UserDtoServiceTest extends Specification {

    @Subject
    UserService subject

    def setup() {
        subject = new UserService()
        subject.with {
            instance = Mock(UserService)
            authUserRepo = Mock(AuthUserRepo)
        }

        GroovyMock(SecurityContextHolder, global:true)
    }

    def "Test init when already users"() {
        when:
        subject.init()

        then:
        1 * subject.authUserRepo.findAdminUser('admin') >> [new AuthUser()]
        0 * subject.instance.saveNewUser('admin', 'admin', 'admin', _)
    }

    def "Test init when no users"() {
        when:
        subject.init()

        then:
        1 * subject.authUserRepo.findAdminUser('admin') >> []
        1 * subject.instance.saveNewUser(_, _, _, _) >> { String email, String firstName, String lastName, String plaintextPassword ->
            assert email == 'admin'
            assert firstName == 'admin'
            assert lastName == 'admin'
            assert plaintextPassword != null
            new AuthUser()
        }
    }

    def "test login"() {
        given:
        UserDto user = new UserDto(email:'alpha', password:'bravo')
        AuthenticationManager authenticationManager = GroovyMock()
        SecurityContext context = GroovyMock()
        HttpSession session = GroovyMock()

        when:
        LoginDto result = subject.login(user, authenticationManager, session)

        then:
        1 * session.getId() >> 'abc';
        1 * authenticationManager.authenticate(_) >> { UsernamePasswordAuthenticationToken token ->
            assert token.getPrincipal() == 'alpha'
            assert token.getCredentials() == 'bravo'
            token
        }
        1 * SecurityContextHolder.getContext() >> context
        1 * context.setAuthentication(_) >> { Authentication auth ->
            assert auth.getPrincipal() == 'alpha'
        }

        and:
        result.success
        result.sessionId == 'abc'
        result.sessionIdBase64 == 'YWJj'
    }

    def "test login invalid"() {
        given:
        UserDto user = new UserDto(email:'alpha', password:'bravo')
        AuthenticationManager authenticationManager = GroovyMock()
        SecurityContext context = GroovyMock()
        HttpSession session = GroovyMock()

        when:
        ResultDto result = subject.login(user, authenticationManager, session)

        then:
        1 * authenticationManager.authenticate(_) >> { UsernamePasswordAuthenticationToken token ->
            throw new Exception('foo')
        }
        0 * SecurityContextHolder.getContext()
        0 * context.setAuthentication(_)

        and:
        result.success == false
    }

    def "test saveNewUser"() {
        given:
        String email = 'a'
        String firstName = 'b'
        String lastName = 'c'
        String plaintextPassword = 'd'

        when:
        AuthUser result = subject.saveNewUser(email, firstName, lastName, plaintextPassword)

        then:
        1 * subject.instance.generateSalt() >> '$1$aaa'
        1 * subject.authUserRepo.save(_) >> { AuthUser user ->
            assert user.email == 'a'
            assert user.firstName == 'b'
            assert user.lastName == 'c'
            assert user.salt == '$1$aaa'
            assert user.password == '$1$aaa$.8XK7wl5ryfI3lNEHsEyu/'
            user
        }

        and:
        result.email == 'a'

    }

    def "test isValidUser when no email match"() {
        given:
        String email = 'a'
        String password = 'd'

        when:
        AuthUser result = subject.isValidUser(email, password)

        then:
        1 * subject.authUserRepo.findAdminUser(email) >> []

        and:
        result == null
    }

    def "test isValidUser when password match"() {
        given:
        String email = 'a'
        String password = 'd'

        and:
        AuthUser user = new AuthUser(salt:'$1$aaa', password:'$1$aaa$.8XK7wl5ryfI3lNEHsEyu/')

        when:
        AuthUser result = subject.isValidUser(email, password)

        then:
        1 * subject.authUserRepo.findAdminUser(email) >> [user]

        and:
        result == user
    }

    def "test isValidUser when no password match"() {
        given:
        String email = 'a'
        String password = 'foobar'

        and:
        AuthUser user = new AuthUser(salt:'$1$aaa', password:'$1$aaa$.8XK7wl5ryfI3lNEHsEyu/')

        when:
        AuthUser result = subject.isValidUser(email, password)

        then:
        1 * subject.authUserRepo.findAdminUser(email) >> [user]

        and:
        result == null
    }

}
