package com.ericsson.jenkinsservice.service.impl;

import com.ericsson.jenkinsservice.service.RawJenkinsService;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawJenkinsServiceImpl implements RawJenkinsService {

  private JenkinsServer jenkinsServer;

  @Value("${custom.jenkins-server-uri}")
  private String jenkinsServerUri;

  @Value("${custom.jenkins-server-username}")
  private String jenkinsServerUsername;

  @Value("${custom.jenkins-server-password}")
  private String jenkinsServerPassword;

  public RawJenkinsServiceImpl(JenkinsServer jenkinsServer, String jenkinsServerUri,
      String jenkinsServerUsername, String jenkinsServerPassword) {
    this.jenkinsServer = jenkinsServer;
    this.jenkinsServerUri = jenkinsServerUri;
    this.jenkinsServerUsername = jenkinsServerUsername;
    this.jenkinsServerPassword = jenkinsServerPassword;
  }

  @PostConstruct
  public void init() {
    try {
      this.jenkinsServer = new JenkinsServer(
          new URI(this.jenkinsServerUri), this.jenkinsServerUsername, this.jenkinsServerPassword);
    } catch (URISyntaxException e) {
      log.error(e.getMessage());
    }
  }

  @Override
  public int getNumberOfDeliveries(final String jobName) {
    int numDeliveries = -1;
    try {
      numDeliveries = this.jenkinsServer.getJob(jobName).getAllBuilds().size();
    } catch (IOException e) {
      log.error(e.getMessage());
    }

    return numDeliveries;
  }

  @Override
  public long getDeliveryTime(final String jobName) {
    long deliveryTime = -1;
    try {
      deliveryTime = this.jenkinsServer.getJob(jobName).getLastSuccessfulBuild().details()
          .getDuration();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    return deliveryTime;
  }

  @Override
  public Map<String, Job> getAllJobs() {
    Map<String, Job> map = new HashMap<>();
    try {
      map = this.jenkinsServer.getJobs();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    return map;
  }

  @Override
  public Long getRestoreTime(final String jobName) {
    long restoreTimeNanoSeconds = -1;
    int failBuildNum = -1;
    List<Build> buildList = null;

    try {
      JobWithDetails jobWithDetails = this.jenkinsServer.getJob(jobName);
      failBuildNum = jobWithDetails.getLastFailedBuild().getNumber();

      if (failBuildNum < 0) {
        return -1L;
      }

      buildList = jobWithDetails.getAllBuilds();
      int failIndex = getIndex(failBuildNum, buildList);
      long failBuildTimeStamp = buildList.get(failIndex).details().getTimestamp();
      boolean notFound = true;
      while (notFound) {
        failIndex = failIndex - 1;
        if ("SUCCESS".equals(buildList.get(failIndex).details().getResult().toString())) {
          notFound = false;
        }
      }

      long successBuildTimeStamp = buildList.get(failIndex).details().getTimestamp();
      restoreTimeNanoSeconds = successBuildTimeStamp - failBuildTimeStamp;
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return restoreTimeNanoSeconds;
  }

  @Override
  public int getIndex(int buildNum, List<Build> buildList) {
    for (Build b : buildList) {
      if (b.getNumber() == buildNum) {
        return buildList.indexOf(b);
      }
    }
    return -1;
  }

  @Override
  public List<Float> getFailureSuccessRate(String jobName) {
    List<Float> failureSuccessRate = new ArrayList<>();
    try {
      JobWithDetails job = this.jenkinsServer.getJob(jobName);
      List<Build> buildList = job.getAllBuilds();
      float failureNum = 0;
      float successNum = 0;

      for (Build b : buildList) {
        if (b.details().getResult() == BuildResult.FAILURE) {
          failureNum++;
        }
        if (b.details().getResult() == BuildResult.SUCCESS) {
          successNum++;
        }
      }

      failureSuccessRate.add((failureNum / buildList.size()) * 100);
      failureSuccessRate.add((successNum / buildList.size()) * 100);

    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return failureSuccessRate;
  }
}
