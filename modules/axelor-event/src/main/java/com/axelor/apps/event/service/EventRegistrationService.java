package com.axelor.apps.event.service;

import com.axelor.apps.event.db.Event;
import com.axelor.apps.event.db.EventRegistration;

public interface EventRegistrationService {
  void calculateAmounts(EventRegistration registration, Event event) throws Exception;

  void saveEventRegistration(EventRegistration registration);
}
