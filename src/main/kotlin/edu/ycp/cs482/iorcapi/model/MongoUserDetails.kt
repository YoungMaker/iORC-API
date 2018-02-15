package edu.ycp.cs482.iorcapi.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails

class MongoUserDetails(private val user : User) : UserDetails {

    private val authorities : MutableCollection<GrantedAuthority>


    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
       return authorities
    }

    init {
        this.authorities = AuthorityUtils.createAuthorityList(user.authorities)
    }

    override fun getUsername(): String {
        return user.username
    }

    override fun getPassword(): String {
       return user.password
    }


    //TODO: do we need this functionality in the future?
    override fun isEnabled(): Boolean {
       return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }


}