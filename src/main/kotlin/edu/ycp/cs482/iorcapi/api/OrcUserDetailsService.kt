package edu.ycp.cs482.iorcapi.api

import edu.ycp.cs482.iorcapi.model.MongoUserDetails
import edu.ycp.cs482.iorcapi.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.security.core.userdetails.UsernameNotFoundException


//TODO: pasword hashing? Creating user accounts?
@Component
class OrcUserDetailsService : UserDetailsService {

    @Autowired
    lateinit var userRepository : UserRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findByUsername(username)
        return if (user == null) {
            throw UsernameNotFoundException(username)
        } else {
            MongoUserDetails(user)
        }
    }

}