package com.ericsson.registrationservice.service;

/**
 * Mail service used to access the e-mail microservice.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
public interface MailService {

  void sendMail(final String addressTo, final String subject, final String text);

}
