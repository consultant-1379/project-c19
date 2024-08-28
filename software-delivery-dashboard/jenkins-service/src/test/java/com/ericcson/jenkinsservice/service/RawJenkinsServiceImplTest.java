package com.ericcson.jenkinsservice.service;


import static org.mockito.ArgumentMatchers.anyString;

import com.ericsson.jenkinsservice.service.impl.RawJenkinsServiceImpl;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = RawJenkinsServiceImpl.class)
class RawJenkinsServiceImplTest {


  private String jenkinsServerUri;
  private String jenkinsServerUsername;
  private String jenkinsServerPassword;
  private JenkinsServer jenkinsServer;
  private RawJenkinsServiceImpl rawJenkinsService;
  private List<Build> buildList;
  private Build build1;
  private Build build2;
  private BuildWithDetails buildWithDetails;
  private JobWithDetails job1;
  private String jobName;

  @BeforeEach
  void beforeEach() throws IOException {

    this.jenkinsServerUri = "https://fem1s11-eiffel216.eiffel.gic.ericsson.se:8443/jenkins/";
    this.jenkinsServerUsername = "harry.foley@ericsson.com";
    this.jenkinsServerPassword = "Sp5t0303!!";
    this.job1 = Mockito.mock(JobWithDetails.class);
    this.jenkinsServer = Mockito.mock(JenkinsServer.class);
    this.build1 = Mockito.mock(Build.class);
    this.buildWithDetails = Mockito.mock(BuildWithDetails.class);

    this.jobName = "eric-oss-testrepo1_PreCodeReview";
    this.rawJenkinsService = new RawJenkinsServiceImpl(jenkinsServer, jenkinsServerUri,
        jenkinsServerUsername, jenkinsServerPassword);
    buildList = new ArrayList<>();
    buildList.add(build1);
    buildList.add(build2);
    Mockito.when(this.job1.getAllBuilds()).thenReturn(buildList);
    Mockito.when(this.jenkinsServer.getJob(anyString()))
        .thenReturn(job1);
    Mockito.when(this.job1.getLastSuccessfulBuild())
        .thenReturn(build1);
    Mockito.when(this.build1.details()).thenReturn(buildWithDetails);
    Mockito.when(this.buildWithDetails.getDuration()).thenReturn(1234L);
  }

  @Test
  void whenGetNumberOfDeliveries_checkIntegerReturned() throws IOException {
    Mockito.when(this.jenkinsServer.getJob(anyString()).getAllBuilds())
        .thenReturn(buildList);
    int numberOfDeliveries = rawJenkinsService.getNumberOfDeliveries(jobName);
    Assertions.assertEquals(2, numberOfDeliveries);
  }

  @Test
  void whenGetDeliveryTime_checkLongReturned() throws IOException {
    Long deliveryTime = rawJenkinsService.getDeliveryTime(jobName);
    Assertions.assertEquals(1234L, deliveryTime);
  }
  
}
