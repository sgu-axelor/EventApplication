package com.axelor.apps.event.service;

import com.axelor.apps.event.db.Event;
import com.axelor.apps.event.db.EventRegistration;

public interface EventRegistrationService {
  void calculateAmount(EventRegistration registration, Event event) throws Exception;
}
