package com.axelor.apps.event.service;

import java.math.BigDecimal;

import com.axelor.apps.event.db.Event;
import com.axelor.apps.event.db.EventRegistration;
import com.axelor.apps.event.db.repo.EventRepository;
import com.axelor.meta.schema.actions.ActionRecord;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class EventServiceImpl implements EventService {

  @Inject EventRepository eventRepo;

  @Transactional
  @Override
  public void setEmailBoolean(Integer eventId) {
    Event event = eventRepo.find(eventId.longValue());
    for (EventRegistration registration : event.getEventRegistration()) {
      if (registration.getEmail() != null && registration.getEmailSent() != true) {
        registration.setEmailSent(true);
      }
    }
  }
  
}
