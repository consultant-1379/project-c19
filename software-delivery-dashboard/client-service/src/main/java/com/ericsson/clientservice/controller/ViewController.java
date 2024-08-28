package com.ericsson.clientservice.controller;

import com.ericsson.clientservice.service.JenkinsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class ViewController {

  private final JenkinsService jenkinsService;

  @GetMapping
  public String getIndex(Model model) {
    model.addAttribute("allJobs", this.jenkinsService.getJobs());
    return "index";
  }

  @GetMapping("/job/{jobName}")
  public String getJobByJobName(@PathVariable final String jobName, final Model model) {
    model.addAttribute("jobName", jobName);
    model.addAttribute("numDeliveries", this.jenkinsService.getNumberOfDeliveries(jobName));
    model.addAttribute("deliveryTime", this.jenkinsService.getDeliveryTime(jobName));
    List<Float> temp = this.jenkinsService.getFailureSuccessRate(jobName);
    model.addAttribute("failureRate", String.format("%.2f", temp.get(0)));
    model.addAttribute("successRate", String.format("%.2f", temp.get(1)));
    long restoreTime = this.jenkinsService.getRestoreTime(jobName);
    if (restoreTime == -1L) {
      model.addAttribute("restoreTime", "No available failed builds");
    } else {
      model.addAttribute("restoreTime", restoreTime + " ns");
    }
    return "job_details";
  }

  @GetMapping("/about")
  public String getAbout() {
    return "about";
  }

  @GetMapping("/login")
  public String getLogin() {
    return "login";
  }

  @GetMapping("/login_error")
  public String getLoginError() {
    return "login_error";
  }

  @GetMapping("/register")
  public String getRegister() {
    return "register";
  }

}
