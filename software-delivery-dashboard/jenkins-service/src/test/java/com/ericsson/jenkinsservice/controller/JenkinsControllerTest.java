package com.ericsson.jenkinsservice.controller;


import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ericsson.jenkinsservice.service.RawJenkinsService;
import com.offbytwo.jenkins.model.Job;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class JenkinsControllerTest {

  private static final Long longExample = 1234L;
  private static final Float floatExample = 1234.0F;
  private static final Integer intExample = 1234;

  private static final String JOB_NAME = "job";
  private static final String ALTERNATIVE_JOB_NAME = "job1";
  private static final String JOB_URL = "http://localhost:80803/job";
  private static final Job JOB = new Job(JOB_NAME, JOB_URL);

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RawJenkinsService rawJenkinsService;

  @BeforeEach
  void beforeEach() {
    Mockito.when(this.rawJenkinsService.getDeliveryTime(anyString()))
        .thenReturn(longExample);
    Mockito.when(this.rawJenkinsService.getRestoreTime(anyString()))
        .thenReturn(longExample);
    Mockito.when(this.rawJenkinsService.getNumberOfDeliveries(anyString()))
        .thenReturn(intExample);

    final Map<String, Job> exampleJobs = new HashMap<>();
    exampleJobs.put(JOB_NAME, JOB);
    exampleJobs.put(ALTERNATIVE_JOB_NAME, JOB);

    Mockito.when(this.rawJenkinsService.getAllJobs())
        .thenReturn(exampleJobs);

    List<Float> listExample = new ArrayList<>();
    listExample.add(floatExample);
    listExample.add(floatExample);
    Mockito.when(this.rawJenkinsService.getFailureSuccessRate(anyString()))
        .thenReturn(listExample);
  }

  @Test
  void whenGetDeliveryTimeRequest_thenCheckContentType() throws Exception {
    mockMvc.perform(get("/jenkins/job/{jobName}/deliveryTime", "job1"))
        .andExpect((status().isOk()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void whenGetDeliveryTimeRequest_thenCheckContent() throws Exception {
    mockMvc.perform(get("/jenkins/job/{jobName}/deliveryTime", "job1"))
        .andExpect((status().isOk()))
        .andExpect(content().json("1234"));
  }


  @Test
  void whenGetRestoreTimeRequest_thenCheckContentType() throws Exception {
    mockMvc.perform(get("/jenkins/job/{jobName}/restoreTime", "job1"))
        .andExpect((status().isOk()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void whenGetRestoreTimeRequest_thenCheckContent() throws Exception {
    mockMvc.perform(get("/jenkins/job/{jobName}/restoreTime", "job1"))
        .andExpect((status().isOk()))
        .andExpect(content().json("1234"));
  }

  @Test
  void whenGetNumberOfDeliveriesRequest_thenCheckContentType() throws Exception {
    mockMvc.perform(get("/jenkins/job/{jobName}/numberOfDeliveries", "job1"))
        .andExpect((status().isOk()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void whenGetNumberOfDeliverieseRequest_thenCheckContent() throws Exception {
    mockMvc.perform(get("/jenkins/job/{jobName}/numberOfDeliveries", "job1"))
        .andExpect((status().isOk()))
        .andExpect(content().json("1234"));
  }

  @Test
  void whenGetFailRateRequest_thenCheckContentType() throws Exception {
    mockMvc.perform(get("/jenkins/job/{jobName}/failureSuccessRate", "job1"))
        .andExpect((status().isOk()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void whenGetFailRateRequest_thenCheckContent() throws Exception {
    mockMvc.perform(get("/jenkins/job/{jobName}/failureSuccessRate", "job1"))
        .andExpect((status().isOk()))
        .andExpect(content().json("[1234.0,1234.0]"));
  }

  @Test
  void whenGetAllJobsRequest_thenCheckContentType() throws Exception {
    mockMvc.perform(get("/jenkins/jobs"))
        .andExpect((status().isOk()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void whenGetAllJobsRequest_thenCheckContent() throws Exception {
    mockMvc.perform(get("/jenkins/jobs"))
        .andExpect((status().isOk()))
        .andExpect(MockMvcResultMatchers
            .jsonPath("$.job")
            .isMap()
        )
        .andExpect(MockMvcResultMatchers
            .jsonPath("$.job1")
            .isMap());
  }

}
