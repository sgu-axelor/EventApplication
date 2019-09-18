package com.axelor.apps.event.web;

import java.math.BigDecimal;
import java.util.Map;

import com.axelor.apps.event.db.Event;
import com.axelor.apps.event.db.EventRegistration;
import com.axelor.apps.event.service.EventRegistrationService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class EventRegistrationController {
  
  EventRegistrationService eventRegistrationService;

  @Inject
  public EventRegistrationController(EventRegistrationService eventRegistrationService) {
    super();
    this.eventRegistrationService = eventRegistrationService;
  }

  public void defaults(ActionRequest request, ActionResponse response) {
    EventRegistration registration = request.getContext().asType(EventRegistration.class);
    if (request.getContext().getParent() != null) {
      Event event = request.getContext().getParent().asType(Event.class);
      registration.setEvent(event);
    }
    response.setValues(registration);
  }

  public void calculateAmount(ActionRequest request, ActionResponse response) {
    EventRegistration registration = request.getContext().asType(EventRegistration.class);
    Event event = null;
    
    if (request.getContext().getParent() != null)
      event = request.getContext().getParent().asType(Event.class);
    else event = registration.getEvent();
    
    if (registration.getRegistrationDate() == null) {
      registration.setAmount(BigDecimal.ZERO);
    } else {
      try {
        eventRegistrationService.calculateAmounts(registration, event);
      } catch (Exception e) {
        response.addError("registrationDate", e.getMessage());
      }
    }
    response.setValues(registration);
  }

  public void saveEventRegistration(ActionRequest request, ActionResponse response) {
    EventRegistration registration = request.getContext().asType(EventRegistration.class);
    eventRegistrationService.saveEventRegistration(registration);
    response.setValues(registration);
  }

  /*  public void checkRegistration(Object bean, Map<String, Object> values) {
  	EventRegistration registration = (EventRegistration) bean;
  }*/
}
