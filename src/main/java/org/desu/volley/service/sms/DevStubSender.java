package org.desu.volley.service.sms;

import org.desu.volley.config.Constants;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!" + Constants.SPRING_PROFILE_SMS)
public class DevStubSender implements SmsSender {

    @Override
    public String[] sendSms(String phones, String message, int translit, String time, String id, int format, String sender, String query) {
        return new String[] {"1", "2", "3", "4"};
    }

    @Override
    public String[] getSmsCost(String phones, String message, int translit, int format, String sender, String query) {
        return new String[] {"1", "2"};
    }

    @Override
    public String getBalance() {
        return "0";
    }

    @Override
    public String[] getStatus(int id, String phone, int all) {
        return new String[] {"1", "2", "3"};
    }
}
