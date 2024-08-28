package com.ericsson.emailservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Basic mail data transfer object.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@Builder
@Data
public class MailDto {

  @JsonProperty("address_to")
  private String addressTo;

  private String subject;

  private String text;

}
