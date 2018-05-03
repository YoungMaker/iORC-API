package edu.ycp.cs482.iorcapi.model.authentication

import graphql.GraphQLException
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import org.springframework.data.mongodb.core.mapreduce.GroupBy.key
import javax.crypto.SecretKey


@Component
class JwtUtils {


    fun createJwt(userid: String, key: ByteArray): String {

        val nowMillis = System.currentTimeMillis()
        val now = Date(nowMillis)
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.add(Calendar.DAY_OF_MONTH, 1) // add one day

        val compactJwt = Jwts.builder()
                .setSubject(userid)
                .setIssuedAt(now)
                .setExpiration(calendar.time)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact()
        Jwts.parser().setSigningKey(key).parseClaimsJws(compactJwt).body.subject == userid //verify the token
        return compactJwt
    }

    fun parseJWT(token: String, key: ByteArray): String { //should return user uuid or throw SignatureException
        try {
            val claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
            return claims.body.subject
        }catch (e: ExpiredJwtException) {
            return e.claims.subject + "-expired"

        }catch (e: SignatureException) {
            throw GraphQLException("Invalid Token!")
        }
    }

   //
}