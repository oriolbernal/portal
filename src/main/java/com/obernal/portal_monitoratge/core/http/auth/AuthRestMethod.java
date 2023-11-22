package com.obernal.portal_monitoratge.core.http.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public interface AuthRestMethod {

     String getAuthorization();

     static String base64encode(String text) {
          return "Basic " + Base64.getEncoder().encodeToString(
                  text.getBytes(StandardCharsets.UTF_8)
          );
     }
}
