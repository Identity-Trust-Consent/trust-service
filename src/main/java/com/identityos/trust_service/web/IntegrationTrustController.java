package com.identityos.trust_service.web;
import com.identityos.trust_service.dto.TrustEvaluationRequest;
import com.identityos.trust_service.service.TrustScoringService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@RestController
@RequestMapping("/api/v1/trust/integration")
public class IntegrationTrustController {
  public record Input(@NotBlank @Size(max=255) String subjectId) {}
  public record Signal(@NotBlank String subjectId,@NotBlank String organizationId,@NotBlank String applicationId,
                       @NotBlank String signalType,@NotBlank String outcome) {}
  private final TrustScoringService service;
  public IntegrationTrustController(TrustScoringService service) { this.service=service; }
  @PostMapping("/evaluate") public Object evaluate(@AuthenticationPrincipal Jwt caller,@Valid @RequestBody Input request) {
    String app=caller.getClaimAsString("application_id"),org=caller.getClaimAsString("organization_id");
    if(app==null || org==null || !("identityos-"+app+"-service").equals(caller.getClaimAsString("azp")))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Registered service client required");
    return service.evaluateIntegration(new TrustEvaluationRequest("USER",request.subjectId(),org,app,null,Map.of()));
  }
  @PostMapping("/signals") public Object signal(@Valid @RequestBody Signal request) {
    if(!java.util.Set.of("EMAIL_VERIFIED","MOBILE_VERIFIED","MFA_ENABLED","STRONG_AUTH_METHOD","LOGIN_SUCCESS","LOGIN_FAILED",
        "OTP_VERIFIED","OTP_FAILED","KNOWN_DEVICE_LOGIN","NEW_DEVICE_LOGIN","KNOWN_LOCATION_LOGIN","SUSPICIOUS_IP_DETECTED",
        "IMPOSSIBLE_TRAVEL","BOT_DETECTED","PROFILE_COMPLETED").contains(request.signalType())
        || !java.util.Set.of("SUCCESS","FAILED").contains(request.outcome()))
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Unsupported trusted signal");
    return service.recordSignal(new com.identityos.trust_service.dto.TrustSignalRequest("USER",request.subjectId(),request.organizationId(),
      request.applicationId(),null,request.signalType(),request.outcome(),null,"identity-os-integration",Map.of(),java.time.OffsetDateTime.now()));
  }
}
