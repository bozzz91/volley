package org.desu.volley.service.sms;

public interface SmsSender {
    String[] sendSms(String phones, String message, int translit, String time, String id, int format, String sender, String query);

    String[] getSmsCost(String phones, String message, int translit, int format, String sender, String query);

    String getBalance();

    String[] getStatus(int id, String phone, int all);
}
