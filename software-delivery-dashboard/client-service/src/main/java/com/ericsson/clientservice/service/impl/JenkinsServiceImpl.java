package com.ericsson.clientservice.service.impl;

import com.ericsson.clientservice.service.JenkinsService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class JenkinsServiceImpl implements JenkinsService {

  private final WebClient jenkinsServiceWebClient;

  @Value("${custom.job-path}")
  private String jobPath;

  @Override
  public Integer getNumberOfDeliveries(final String jobName) {
    int numberOfDeliveries = -1;
    try {
      numberOfDeliveries = jenkinsServiceWebClient
          .get()
          .uri(jobPath + jobName + "/numberOfDeliveries")
          .retrieve()
          .bodyToMono(Integer.class)
          .block();
    } catch (NullPointerException | WebClientResponseException ex) {
      log.error(ex.getMessage());
    }

    return numberOfDeliveries;
  }

  @Override
  public Long getDeliveryTime(final String jobName) {
    long deliveryTime = -1L;
    try {
      deliveryTime = jenkinsServiceWebClient
          .get()
          .uri(jobPath + jobName + "/deliveryTime")
          .retrieve()
          .bodyToMono(Long.class)
          .block();
    } catch (NullPointerException | WebClientResponseException ex) {
      log.error(ex.getMessage());
    }
    return deliveryTime;
  }

  @Override
  public Long getRestoreTime(String jobName) {
    Long restoreTime = -1L;
    try {
      restoreTime = jenkinsServiceWebClient
          .get()
          .uri(jobPath + jobName + "/restoreTime")
          .retrieve()
          .bodyToMono(Long.class)
          .block();
    } catch (NullPointerException | WebClientResponseException ex) {
      log.error(ex.getMessage());
    }
    return restoreTime;
  }

  @Override
  public List<Float> getFailureSuccessRate(String jobName) {
    List<Float> failureSuccessRate = new ArrayList<>();
    try {
      failureSuccessRate = jenkinsServiceWebClient
          .get()
          .uri(jobPath + jobName + "/failureSuccessRate")
          .retrieve()
          .bodyToMono(List.class)
          .block();
    } catch (NullPointerException | WebClientResponseException ex) {
      log.error(ex.getMessage());
    }
    return failureSuccessRate;
  }

  @Override
  public Object getJob(String jobName) {
    return null;
  }

  @Override
  public List getJobs() {
    ParameterizedTypeReference<Map<String, Map<String, Object>>> parameterizedTypeReference = new ParameterizedTypeReference<>() {
    };
    Map<String, Map<String, Object>> map = jenkinsServiceWebClient
        .get()
        .uri("/jobs/")
        .retrieve()
        .onRawStatus(i -> {
          log.info("Status is ={}", i);
          return true;
        }, clientResponse -> {
          log.info("");
          return Mono.empty();
        })
        .bodyToMono(parameterizedTypeReference)
        .block();

    assert map != null : "Map is empty";
    return new ArrayList<>(map.keySet());
  }

}
