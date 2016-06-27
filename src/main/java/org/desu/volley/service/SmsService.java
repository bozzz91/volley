package org.desu.volley.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SmsService {
    private final Logger log = LoggerFactory.getLogger(SmsService.class);

    public void sendSms(String to, String text) {
        log.error("not implemented yet");
    }
}
