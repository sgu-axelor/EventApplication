package com.axelor.apps.event.web;

import java.math.BigDecimal;

import com.axelor.apps.event.db.Discount;
import com.axelor.apps.event.db.Event;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class DiscountController {

  public void setDiscountAmount(ActionRequest request, ActionResponse response) {
    Discount discount = request.getContext().asType(Discount.class);
    if (request.getContext().getParent() != null) {
      Event event = request.getContext().getParent().asType(Event.class);
      discount.setDiscountAmount(
          event
              .getEventFee()
              .multiply(discount.getDiscountPercentage())
              .divide(BigDecimal.TEN)
              .divide(BigDecimal.TEN));
      response.setValues(discount);
    }
  }

  public void setBeforeDay(ActionRequest request, ActionResponse response) {
    Discount discount = request.getContext().asType(Discount.class);
    if (request.getContext().getParent() != null) {
      Event event = request.getContext().getParent().asType(Event.class);
      System.err.println(event.getRegistrationOpen().plusDays(discount.getBeforeDays()));
      if (event
              .getRegistrationOpen()
              .plusDays(discount.getBeforeDays())
              .isAfter(event.getRegistrationClose())
          || event
              .getRegistrationOpen()
              .plusDays(discount.getBeforeDays())
              .isEqual(event.getRegistrationClose())) {
        response.addError(
            "beforeDays",
            "Before Days is exceeds the Differenence of Registration Open and Close Date");
      }
    }
  }
}
