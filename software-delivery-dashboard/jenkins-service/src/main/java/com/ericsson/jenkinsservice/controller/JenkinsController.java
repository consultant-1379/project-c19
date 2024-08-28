package com.ericsson.jenkinsservice.controller;

import com.ericsson.jenkinsservice.service.RawJenkinsService;
import com.offbytwo.jenkins.model.Job;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/jenkins")
public class JenkinsController {

  private final RawJenkinsService rawJenkinsService;

  @GetMapping("/job/{jobName}/numberOfDeliveries")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Integer> getNumberOfDeliveries(
      @PathVariable final String jobName,
      @RequestParam(required = false) final ZonedDateTime startDateTime,
      @RequestParam(required = false) final ZonedDateTime endDateTime) {

    return ResponseEntity.ok(this.rawJenkinsService.getNumberOfDeliveries(jobName));
  }

  @GetMapping("/job/{jobName}/deliveryTime")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Long> getDeliveryTime(
      @PathVariable final String jobName,
      @RequestParam(required = false) final ZonedDateTime startDateTime,
      @RequestParam(required = false) final ZonedDateTime endDateTime) {

    return ResponseEntity.ok(this.rawJenkinsService.getDeliveryTime(jobName));
  }

  @GetMapping("/job/{jobName}/failureSuccessRate")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<Float>> getFailureRate(
      @PathVariable final String jobName,
      @RequestParam(required = false) final ZonedDateTime startDateTime,
      @RequestParam(required = false) final ZonedDateTime endDateTime) {

    return ResponseEntity.ok(this.rawJenkinsService.getFailureSuccessRate(jobName));
  }

  @GetMapping("/job/{jobName}/restoreTime")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Long> getRestoreTime(
      @PathVariable final String jobName,
      @RequestParam(required = false) final ZonedDateTime startDateTime,
      @RequestParam(required = false) final ZonedDateTime endDateTime) {

    return ResponseEntity.ok(this.rawJenkinsService.getRestoreTime(jobName));
  }

  @GetMapping("/jobs")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Map<String, Job>> getAllJobs() {
    return ResponseEntity.ok(this.rawJenkinsService.getAllJobs());
  }

}
