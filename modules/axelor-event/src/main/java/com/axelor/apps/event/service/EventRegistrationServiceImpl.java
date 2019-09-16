package com.axelor.apps.event.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.axelor.apps.event.db.Discount;
import com.axelor.apps.event.db.Event;
import com.axelor.apps.event.db.EventRegistration;

public class EventRegistrationServiceImpl implements EventRegistrationService {

  @Override
  public void calculateAmount(EventRegistration registration, Event event) throws Exception {
    BigDecimal amount = BigDecimal.ZERO;
    LocalDateTime registrationDate = registration.getRegistrationDate();
    if (registrationDate != null
        && (registrationDate.toLocalDate().isBefore(event.getRegistrationClose())
            || registrationDate.toLocalDate().isEqual(event.getRegistrationClose()))
        && (registrationDate.toLocalDate().isAfter(event.getRegistrationOpen())
            || registrationDate.toLocalDate().isEqual(event.getRegistrationOpen()))) {
      if (event.getDiscount() != null && !event.getDiscount().isEmpty()) {
        for (Discount discount : event.getDiscount()) {
          if (registrationDate
                  .toLocalDate()
                  .isBefore(event.getRegistrationClose().minusDays(discount.getBeforeDays()))
              || registrationDate
                  .toLocalDate()
                  .isEqual(event.getRegistrationClose().minusDays(discount.getBeforeDays()))) {
            amount =
                discount
                    .getDiscountPercentage()
                    .multiply(event.getEventFee())
                    .divide(new BigDecimal(100));
            amount = event.getEventFee().subtract(amount);
            break;
          }
        }
      }
      if (amount.compareTo(BigDecimal.ZERO) == 0) {
        amount = event.getEventFee();
      }
    } else {
      throw new Exception("Registration Date is not in the Registration Period.");
    }

    registration.setAmount(amount);
  }
}
