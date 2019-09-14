package com.axelor.apps.event.web;

import java.math.BigDecimal;

import com.axelor.apps.event.db.Discount;
import com.axelor.apps.event.db.Event;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class DiscountController {
  
  public void setDiscountAmount(ActionRequest request , ActionResponse response ) {
    Discount discount = request.getContext().asType(Discount.class);
    Event event = request.getContext().getParent().asType(Event.class);
    discount.setDiscountAmount(event.getEventFee().multiply(discount.getDiscountPercentage()).divide(BigDecimal.TEN).divide(BigDecimal.TEN));
    response.setValues(discount);
  }
}
