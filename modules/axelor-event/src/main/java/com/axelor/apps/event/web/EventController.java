package com.axelor.apps.event.web;

import java.math.BigDecimal;

import com.axelor.apps.event.db.Event;
import com.axelor.apps.event.db.EventRegistration;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class EventController {

  public void validates(ActionRequest request, ActionResponse response) {
    Event event = request.getContext().asType(Event.class);
    String error ="";
    if (event.getStartDate() != null
        && event.getEndDate() != null
        && (event.getEndDate().isBefore(event.getStartDate()))) {
      error = "End Date Cannot be Before Start Date";
    }
    if (event.getRegistrationOpen() != null
        && event.getRegistrationClose() != null
        && (event.getRegistrationClose().isBefore(event.getRegistrationOpen()))) {
      error = "Closing date Cannot be Before of Opening Date";
    }
    if (event.getTotalEntry() == event.getCapacity() && event.getCapacity() != 0) {
      error = "No of Registration is more Than total capacity of Event";
    }
    if(!error.equals(""))
      response.setError(error);
  }

  public void calTotalAmount(ActionRequest request, ActionResponse response) {
    Event event = request.getContext().asType(Event.class);
    BigDecimal totalAmount = BigDecimal.ZERO;
    if (event.getEventRegistration() != null && !event.getEventRegistration().isEmpty()) {
      for (EventRegistration registration : event.getEventRegistration()) {
        totalAmount = totalAmount.add(registration.getAmount());
        if (registration.getEvent() == null) {
          registration.setEvent(event);
        }
      }
    }
    event.setTotalDiscount(
        event
            .getEventFee()
            .multiply(new BigDecimal(event.getEventRegistration().size()))
            .subtract(totalAmount));
    event.setAmountCollected(totalAmount);
    response.setValues(event);
  }
  
  public void setTotalEntries(ActionRequest request, ActionResponse response ) {
    Event event = request.getContext().asType(Event.class);
    if (event.getEventRegistration() != null && !event.getEventRegistration().isEmpty()) {
      event.setTotalEntry(event.getEventRegistration().size());
    }
    response.setValues(event);
  }
}
