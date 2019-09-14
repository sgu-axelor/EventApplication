package com.axelor.apps.event.web;

import java.math.BigDecimal;

import com.axelor.apps.event.db.Event;
import com.axelor.apps.event.db.EventRegistration;
import com.axelor.apps.event.service.EventRegistrationService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class EventRegistrationController {

  public void defaults(ActionRequest request, ActionResponse response) {
    EventRegistration registration = request.getContext().asType(EventRegistration.class);
    if(request.getContext().getParent()!=null) {
      Event event = request.getContext().getParent().asType(Event.class);
      response.setAttr("event", "hidden", true);
      registration.setEvent(event);
    }
    response.setValues(registration);
  }
  
  public void calculateAmount(ActionRequest request, ActionResponse response ) {
    EventRegistration registration = request.getContext().asType(EventRegistration.class);
    Event event =request.getContext().getParent().asType(Event.class);
    if(event == null) {
      event = registration.getEvent();
    }
      if (registration.getRegistrationDate()==null || event.getDiscount()==null) {
        registration.setAmount(BigDecimal.ZERO);
    }
    else {
      Beans.get(EventRegistrationService.class).calculateAmount(registration,event);
    }
    response.setValues(registration);
  }
}
