package io.github.jonloucks.gradle.kit;


import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

final class Internal {
    private Internal() {
    }
  
    static String base64Encode(String text) {
        return new String(Base64.getEncoder().encode(text.getBytes(UTF_8)), UTF_8);
    }
    
    static String base64Decode(String text) {
        return new String(Base64.getDecoder().decode(text), UTF_8);
    }
}
