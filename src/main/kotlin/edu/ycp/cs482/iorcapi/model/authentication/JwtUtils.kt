package edu.ycp.cs482.iorcapi.model.authentication

import graphql.GraphQLException
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import org.springframework.data.mongodb.core.mapreduce.GroupBy.key


@Component
class JwtUtils {


    @Value("\${privatekey}")
    private val privatekey: String? = null

    fun createJwt(userid: String): String {

        val nowMillis = System.currentTimeMillis()
        val now = Date(nowMillis)
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.add(Calendar.DAY_OF_MONTH, 1) // add one day

        val compactJwt = Jwts.builder()
                .setSubject(userid)
                .setIssuedAt(now)
                .setExpiration(calendar.time)
                //.compressWith(CompressionCodecs.DEFLATE)
                .signWith(SignatureAlgorithm.HS512, privatekey)
                .compact()

        Jwts.parser().setSigningKey(privatekey).parseClaimsJws(compactJwt).body.subject.equals(userid) //verify the token
        return compactJwt
    }

    fun parseJWT(token: String): String { //should return user uuid or throw SignatureException
        try {
            val claims = Jwts.parser()
                    .setSigningKey(privatekey)
                    .parseClaimsJws(token)

            if(claims.body.expiration.after(Date(System.currentTimeMillis()))){
                return claims.body.subject
            }else{
                throw GraphQLException("Invalid Token!")
            }
        }catch (e: SignatureException) {
            throw GraphQLException("Invalid Token!")
        }
    }

   //
}