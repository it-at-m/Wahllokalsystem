package de.muenchen.oss.wahllokalsystem.authservice.rest;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("authserver")
@RequiredArgsConstructor
@Data
public class AuthServerController {

  @Value("${service.config.oauth2.logoutUri}")
  private String logoutUri;

  @GetMapping("logouturl")
  public ResolvedUrlDTO getLogoutUrl() {
    return new ResolvedUrlDTO(logoutUri);
  }
}
