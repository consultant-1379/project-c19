package com.ericsson.emailservice.service;

/**
 * Raw mail service, used to send mails using java mail sender directly.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
public interface RawMailService {

  /**
   * Method used to send mail by directly accessing the java mail sender.
   *
   * @param addressTo address to which e-mail is to be sent.
   * @param subject   subject of the e-mail to be sent.
   * @param text      text of the e-mail to be sent.
   * @return status indicating whether the e-mail was sent or not.
   */
  MailSendStatus sendMail(final String addressTo, final String subject, final String text);

  enum MailSendStatus {
    FAILURE,
    SENT
  }

}
