package com.axelor.apps.event.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.axelor.apps.event.db.Discount;
import com.axelor.apps.event.db.Event;
import com.axelor.apps.event.db.EventRegistration;
import com.axelor.apps.event.db.repo.EventRegistrationRepository;
import com.axelor.apps.event.db.repo.EventRepository;
import com.axelor.inject.Beans;
import com.google.inject.persist.Transactional;

public class EventRegistrationServiceImpl implements EventRegistrationService {

  @Override
  public void calculateAmount(EventRegistration registration, Event event) throws Exception {
    BigDecimal amount = BigDecimal.ZERO, temp = BigDecimal.ZERO;
    LocalDate registrationDate = registration.getRegistrationDate().toLocalDate();
    if (registrationDate != null
        && (registrationDate.isBefore(event.getRegistrationClose())
            || registrationDate.isEqual(event.getRegistrationClose()))
        && (registrationDate.isAfter(event.getRegistrationOpen())
            || registrationDate.isEqual(event.getRegistrationOpen()))) {
      if (event.getDiscount() != null && !event.getDiscount().isEmpty()) {
        for (Discount discount : event.getDiscount()) {
          System.err.println(event.getRegistrationClose().minusDays(discount.getBeforeDays()));
          if (registrationDate.isBefore(
                  event.getRegistrationClose().minusDays(discount.getBeforeDays()))
              || registrationDate.isEqual(
                  event.getRegistrationClose().minusDays(discount.getBeforeDays()))) {
            amount =
                discount
                    .getDiscountPercentage()
                    .multiply(event.getEventFee())
                    .divide(new BigDecimal(100));
            amount = event.getEventFee().subtract(amount);
            if (temp.compareTo(BigDecimal.ZERO) == 0) {
              temp = amount;
            } else if (temp.compareTo(amount) == -1) {
              amount = temp;
            }
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

  @Override
  @Transactional
  public void updateEvent(Event event, EventRegistration registration) {
    BigDecimal registrationAmount = registration.getAmount();
    BigDecimal eventTotalAmount = event.getAmountCollected();
    BigDecimal eventTotalDiscount = event.getTotalDiscount();

    if (registration.getId() == null) {
      event.setTotalEntry(event.getTotalEntry() + 1);
      eventTotalAmount = eventTotalAmount.add(registrationAmount);
      if (event.getEventFee().compareTo(registrationAmount) == 1)
        eventTotalDiscount =
            eventTotalDiscount.add(event.getEventFee().subtract(registrationAmount));
    } else {
      EventRegistration repoRegistration =
          Beans.get(EventRegistrationRepository.class).find(registration.getId());
      BigDecimal repoRegistrationAmount = repoRegistration.getAmount();
      if (repoRegistrationAmount.compareTo(registrationAmount) != 0) {
        eventTotalAmount =
            eventTotalAmount.add(registrationAmount).subtract(repoRegistrationAmount);

        if (repoRegistrationAmount.compareTo(registrationAmount) == -1) {

          eventTotalDiscount =
              eventTotalDiscount.subtract(registrationAmount.subtract(repoRegistrationAmount));
        } else {
          eventTotalDiscount.add(repoRegistrationAmount.subtract(registrationAmount));
        }
      }
    }
    event.setAmountCollected(eventTotalAmount);
    event.setTotalDiscount(eventTotalDiscount);
    Beans.get(EventRepository.class).save(event);
  }
}
