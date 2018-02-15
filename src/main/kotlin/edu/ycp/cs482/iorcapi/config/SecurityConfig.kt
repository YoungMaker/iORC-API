package edu.ycp.cs482.iorcapi.config

import edu.ycp.cs482.iorcapi.api.OrcUserDetailsService
import edu.ycp.cs482.iorcapi.model.MongoUserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter


@Configuration
@EnableWebSecurity
class SecurityConfig(builder : AuthenticationManagerBuilder, userDetailService: OrcUserDetailsService ) : WebSecurityConfigurerAdapter() {

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication()
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/public").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .permitAll()
                .and()
                .logout()
                .permitAll()

        http.authorizeRequests()
                .antMatchers("/graphql").permitAll()

        http.csrf().disable() //TODO: PUT THIS BACK BEFORE PRODUCTION
    }

    init {
        builder.userDetailsService(userDetailService)
    }
}