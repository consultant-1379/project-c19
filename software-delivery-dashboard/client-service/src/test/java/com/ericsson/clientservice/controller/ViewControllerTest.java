package com.ericsson.clientservice.controller;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ericsson.clientservice.service.JenkinsService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
@SpringBootTest
class ViewControllerTest {

  private static final Long TESTTIME = 25111L;
  private static final int NUMDELIVERIES = 24;
  private static final List<Float> LISTFLOATS = List.of(70.00f, 25.00f);
  private static final List<String> LISTJOBS = List.of("This", "Is", "A", "List", "Of", "Jobs");

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private JenkinsService jenkinsService;

  @BeforeEach
  void setUp() {
    when(this.jenkinsService.getJobs())
        .thenReturn(LISTJOBS);
    when(this.jenkinsService.getDeliveryTime(anyString()))
        .thenReturn(TESTTIME);
    when(this.jenkinsService.getNumberOfDeliveries(anyString()))
        .thenReturn(NUMDELIVERIES);
    when(this.jenkinsService.getFailureSuccessRate(anyString()))
        .thenReturn(LISTFLOATS);
    when(this.jenkinsService.getRestoreTime(anyString()))
        .thenReturn(TESTTIME);

  }

  @WithMockUser(value = "spring")
  @Test
  void testGetIndex() throws Exception {
    this.mockMvc.perform(get("/"))
        .andExpect(status().isOk());
  }

  @WithMockUser(value = "spring")
  @Test
  void testGetAllJobs() throws Exception {
    this.mockMvc.perform(get("/"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("allJobs"))
        .andExpect(MockMvcResultMatchers.model().attribute("allJobs", LISTJOBS));
  }

  @WithMockUser(value = "spring")
  @Test
  void testGetJobByJobName() throws Exception {
    this.mockMvc.perform(get("/job/{jobName}", "jobName"))
        .andExpect(status().isOk());
  }

  @WithMockUser(value = "spring")
  @Test
  void testNumDeliveries() throws Exception {
    this.mockMvc.perform(get("/job/{jobName}", "jobName"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("numDeliveries"))
        .andExpect(MockMvcResultMatchers.model().attribute("numDeliveries", NUMDELIVERIES));
  }

  @WithMockUser(value = "spring")
  @Test
  void testDeliveryTime() throws Exception {
    this.mockMvc.perform(get("/job/{jobName}", "jobName"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("deliveryTime"))
        .andExpect(MockMvcResultMatchers.model().attribute("deliveryTime", TESTTIME));
  }

  @WithMockUser(value = "spring")
  @Test
  void testFailureRate() throws Exception {
    this.mockMvc.perform(get("/job/{jobName}", "jobName"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("failureRate"))
        .andExpect(MockMvcResultMatchers.model()
            .attribute("failureRate", String.format("%.2f", LISTFLOATS.get(0))));

  }

  @WithMockUser(value = "spring")
  @Test
  void testSuccessRate() throws Exception {
    this.mockMvc.perform(get("/job/{jobName}", "jobName"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("successRate"))
        .andExpect(MockMvcResultMatchers.model()
            .attribute("successRate", String.format("%.2f", LISTFLOATS.get(1))));
  }

  @WithMockUser(value = "spring")
  @Test
  void testGetRestoreTime() throws Exception {
    this.mockMvc.perform(get("/job/{jobName}", "jobName"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("restoreTime"))
        .andExpect(MockMvcResultMatchers.model().attribute("restoreTime", TESTTIME + " ns"));
  }

  @WithMockUser(value = "spring")
  @Test
  void testGetAbout() throws Exception {
    this.mockMvc.perform(get("/about"))
        .andExpect(status().isOk());
  }

  @WithMockUser(value = "spring")
  @Test
  void testGetLogin() throws Exception {
    this.mockMvc.perform(get("/login"))
        .andExpect(status().isOk());
  }

  @WithMockUser(value = "spring")
  @Test
  void testGetLoginError() throws Exception {
    this.mockMvc.perform(get("/login_error"))
        .andExpect(status().isOk());
  }

  @WithMockUser(value = "spring")
  @Test
  void testGetRegister() throws Exception {
    this.mockMvc.perform(get("/register"))
        .andExpect(status().isOk());
  }

}
