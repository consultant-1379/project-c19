package com.ericsson.clientservice.service;

import java.util.List;


public interface JenkinsService {

  Integer getNumberOfDeliveries(final String jobName);
  Long getDeliveryTime(final String jobName);
  Long getRestoreTime(final String jobName);
  Object getJob(String jobName);
  List<Float> getFailureSuccessRate(String jobName);
  List<String> getJobs();
  
}
