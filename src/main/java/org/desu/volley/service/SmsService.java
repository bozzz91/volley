package org.desu.volley.service;

import org.apache.commons.lang.StringUtils;
import org.desu.volley.domain.Sms;
import org.desu.volley.domain.User;
import org.desu.volley.repository.SmsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Sms.
 */
@Service
@Transactional
public class SmsService {

    private final Logger log = LoggerFactory.getLogger(SmsService.class);

    @Inject
    private SmsRepository smsRepository;
    @Inject
    private Executor executor;

    private Smsc smsc = new Smsc();

    private void sendSms(Sms sms) {
        executor.execute(() -> {
            log.info("sendSms.enter; to {} people", sms.getRecipients().size());
            try {
                Thread.sleep(1000L);
                String phones = sms.getRecipients().stream()
                    .map(User::getPhone)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(","));
                String[] result = smsc.send_sms(phones, sms.getText(), 0, "", sms.getId()+"", 0, "LightLine", "");
                if (result.length == 2) {
                    //send failed
                    log.warn("sendSms; sms send to {} with text {} failed, result {}", phones, sms.getText(), result);
                    sms.setState(result[1]);
                } else {
                    sms.setState("0");
                    log.info("sendSms; sms send to {} with text {} success, result {}", phones, sms.getText(), result);
                }
            } catch (Exception e) {
                sms.setState("-1");
                log.error("Error while send", e);
            }
            save(sms, false);
        });
    }

    /**
     * Save a sms.
     *
     * @param sms the entity to save
     * @return the persisted entity
     */
    public Sms save(Sms sms, boolean send) {
        log.debug("Request to save Sms : {}", sms);
        Sms result = smsRepository.save(sms);
        if (send) {
            sendSms(result);
        }
        return result;
    }

    /**
     *  Get all the sms.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Sms> findAll(Pageable pageable) {
        log.debug("Request to get all Sms");
        Page<Sms> result = smsRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one sms by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Sms findOne(Long id) {
        log.debug("Request to get Sms : {}", id);
        Sms sms = smsRepository.findOneWithEagerRelationships(id);
        sms.getRecipients().size();
        return sms;
    }

    /**
     *  Delete the  sms by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Sms : {}", id);
        smsRepository.delete(id);
    }
}
