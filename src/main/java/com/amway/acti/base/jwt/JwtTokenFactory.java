/**
 * Created by dk on 2018/2/22.
 */

package com.amway.acti.base.jwt;

import com.amway.acti.base.property.JwtProperties;
import com.amway.acti.base.util.Constants;
import com.amway.acti.model.AccessJwtToken;
import com.amway.acti.model.AccessJwtTokenRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtTokenFactory {

    @Autowired
    private JwtProperties jwtProperties;

    public AccessJwtToken createAccessJwtToken(AccessJwtTokenRequest accessJwtTokenRequest) {
        Claims claims = Jwts.claims().setSubject(accessJwtTokenRequest.getOpenId());
        LocalDateTime currentTime = LocalDateTime.now();
        String token = Jwts.builder()
            .setClaims(claims)
            .claim(Constants.UNIONID, accessJwtTokenRequest.getUnionId())
            .claim(Constants.OPENID, accessJwtTokenRequest.getOpenId())
            .claim(Constants.UID, accessJwtTokenRequest.getUid())
            .claim(Constants.IDENT, accessJwtTokenRequest.getIdent())
            .claim(Constants.EMAIL, accessJwtTokenRequest.getEmail())
            .claim(Constants.TPASSWORD, accessJwtTokenRequest.getTpassword())
            .claim(Constants.TIMESTAMP, System.currentTimeMillis())
            .setIssuer(jwtProperties.getTokenIssuer())
            .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
            .setExpiration(Date.from(currentTime
                .plusMinutes(jwtProperties.getTokenExpirationTime())
                .atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(SignatureAlgorithm.HS512, jwtProperties.getTokenSigningKey())
            .compact();
        return new AccessJwtToken(token);
    }

    public Jws<Claims> parseClaims(String token) {
        return Jwts.parser().setSigningKey(jwtProperties.getTokenSigningKey())
            .parseClaimsJws(token.replace(Constants.TOKEN_PREFIX, ""));
    }

}
