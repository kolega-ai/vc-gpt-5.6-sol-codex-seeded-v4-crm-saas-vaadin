package com.northstar.crm.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Component
public class SignedSessionTokenService {
    private final byte[] key;
    public SignedSessionTokenService(@Value("${app.session-signing-key}") String key){if(key.length()<32)throw new IllegalStateException("Signing key must be at least 32 characters");this.key=key.getBytes(StandardCharsets.UTF_8);}
    public String mint(String email){var payload=email.toLowerCase()+"|"+Instant.now().plusSeconds(60).getEpochSecond();return encode(payload)+"."+sign(payload);}
    public String verify(String token){
        try {var p=token.split("\\.");if(p.length!=2)throw new SecurityException("Invalid session token");var payload=new String(Base64.getUrlDecoder().decode(p[0]),StandardCharsets.UTF_8);if(!constantTime(sign(payload),p[1]))throw new SecurityException("Invalid session token");var values=payload.split("\\|");if(values.length!=2||Instant.now().getEpochSecond()>Long.parseLong(values[1]))throw new SecurityException("Expired session token");return values[0];}catch(IllegalArgumentException e){throw new SecurityException("Invalid session token");}
    }
    private String encode(String value){return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));}
    private String sign(String value){try{var mac=Mac.getInstance("HmacSHA256");mac.init(new SecretKeySpec(key,"HmacSHA256"));return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));}catch(Exception e){throw new IllegalStateException(e);}}
    private boolean constantTime(String a,String b){return java.security.MessageDigest.isEqual(a.getBytes(StandardCharsets.US_ASCII),b.getBytes(StandardCharsets.US_ASCII));}
}
