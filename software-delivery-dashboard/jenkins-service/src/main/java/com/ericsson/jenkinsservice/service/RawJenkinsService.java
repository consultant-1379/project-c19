package com.ericsson.jenkinsservice.service;

import com.offbytwo.jenkins.model.Job;
import java.util.Map;
import com.offbytwo.jenkins.model.Build;

import java.util.List;

public interface RawJenkinsService {

  int getNumberOfDeliveries(final String jobName);
  long getDeliveryTime(final String jobName);
  Map<String, Job> getAllJobs();
  Long getRestoreTime(final String jobName);
  List<Float> getFailureSuccessRate(String jobName);
  int getIndex(int buildNum, List<Build> buildList);


  }
