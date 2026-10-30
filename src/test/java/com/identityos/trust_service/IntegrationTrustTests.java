package com.identityos.trust_service;
import com.identityos.trust_service.repository.TrustSignalRepository;
import com.identityos.trust_service.service.TrustScoringService;
import com.identityos.trust_service.dto.TrustEvaluationRequest;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class IntegrationTrustTests {
 @Test void integrationEvaluationUsesScopedSignalsAndIgnoresArbitraryWeights() {
  var repo=mock(TrustSignalRepository.class);
  var signal=new HashMap<String,Object>();signal.put("signal_type","ARBITRARY");signal.put("weight",1000);
  when(repo.integrationSignals("s1","org1","app1")).thenReturn(List.of(signal));
  var service=new TrustScoringService(repo);
  var result=service.evaluateIntegration(new TrustEvaluationRequest("USER","s1","org1","app1",null,Map.of()));
  assertEquals(40,result.finalScore());verify(repo).integrationSignals("s1","org1","app1");
  verify(repo,never()).findSignals(anyString(),anyString(),anyString(),anyInt());
 }
}
