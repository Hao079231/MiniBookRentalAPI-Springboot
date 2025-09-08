package com.ute.rental.controller;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class ABasicController {
  private Optional<Jwt> getCurrentToken(){
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
      return Optional.of((Jwt) authentication.getPrincipal());
    }
    return Optional.empty();
  }

  public Long getCurrentUser() {
    return getCurrentToken()
        .map(jwt -> {
          Object idClaim = jwt.getClaim("id");
          if (idClaim instanceof Number) {
            return ((Number) idClaim).longValue();
          }
          if (idClaim instanceof String) {
            try {
              return Long.parseLong((String) idClaim);
            } catch (NumberFormatException e) {
              return -1L; // giá trị mặc định
            }
          }
          return -1L;
        })
        .orElse(-1L);
  }


  public boolean isAdmin() {
    return getCurrentToken()
        .map(jwt -> {
          Object isAdminClaim = jwt.getClaim("isAdmin");
          if (isAdminClaim instanceof Boolean) {
            return (Boolean) isAdminClaim;
          }
          if (isAdminClaim instanceof String) {
            return Boolean.parseBoolean((String) isAdminClaim);
          }
          return false;
        })
        .orElse(false);
  }
}
