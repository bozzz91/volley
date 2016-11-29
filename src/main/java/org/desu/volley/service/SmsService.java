package org.desu.volley.service;

import org.desu.volley.domain.Sms;
import org.desu.volley.repository.SmsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Sms.
 */
@Service
@Transactional
public class SmsService {

    private final Logger log = LoggerFactory.getLogger(SmsService.class);

    @Inject
    private SmsRepository smsRepository;

    private Smsc sms = new Smsc();

    @Async
    public void sendSms(String to, String text) {
        log.info("sendSms.enter; to {}", to);
        String[] ret = sms.send_sms(to, text, 0, "", "1", 0, "", "");
        log.info("sendSms.exit; sms send to {} with message {}, returning {}", to, text, ret);
    }

    /**
     * Save a sms.
     *
     * @param sms the entity to save
     * @return the persisted entity
     */
    public Sms save(Sms sms) {
        log.debug("Request to save Sms : {}", sms);
        Sms result = smsRepository.save(sms);
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
