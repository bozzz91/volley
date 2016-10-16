package org.desu.volley.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    private final Logger log = LoggerFactory.getLogger(SmsService.class);

    private Smsc sms = new Smsc();

    @Async
    public void sendSms(String to, String text) {
        log.info("sendSms.enter; to {}", to);
        String[] ret = sms.send_sms(to, text, 0, "", "1", 0, "", "");
        log.info("sendSms.exit; sms send to {} with message {}, returning {}", to, text, ret);
    }
}
