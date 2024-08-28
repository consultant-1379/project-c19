package com.ericsson.emailservice.dto;

import com.ericsson.emailservice.service.RawMailService.MailSendStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Basic mail response data transfer object.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MailResponseDto {

  @JsonProperty("mail_send_status")
  private MailSendStatus mailSendStatus;

}
