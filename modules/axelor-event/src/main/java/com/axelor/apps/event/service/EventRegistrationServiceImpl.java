package com.axelor.apps.event.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import com.axelor.apps.event.db.Discount;
import com.axelor.apps.event.db.Event;
import com.axelor.apps.event.db.EventRegistration;
import com.axelor.apps.event.db.repo.EventRegistrationRepository;
import com.axelor.apps.event.db.repo.EventRepository;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.google.inject.persist.Transactional;

public class EventRegistrationServiceImpl implements EventRegistrationService {

  @Override
  public void calculateAmounts(EventRegistration registration, Event event) throws Exception {
    BigDecimal amount = BigDecimal.ZERO, temp = BigDecimal.ZERO;
    LocalDate registrationDate = registration.getRegistrationDate().toLocalDate();
    if (registrationDate != null
        && (registrationDate.isBefore(event.getRegistrationClose())
            || registrationDate.isEqual(event.getRegistrationClose()))
        && (registrationDate.isAfter(event.getRegistrationOpen())
            || registrationDate.isEqual(event.getRegistrationOpen()))) {
      if (event.getDiscount() != null && !event.getDiscount().isEmpty()) {
        for (Discount discount : event.getDiscount()) {
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
      throw new Exception(I18n.get("Registration Date is not in the Registration Period."));
    }

    registration.setAmount(amount);
  }

  @Override
  @Transactional
  public void saveEventRegistration(EventRegistration registration) {
    Event event = registration.getEvent();

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

  public EventRegistration importChecks(Object bean, Map<?, ?> context) {
    EventRegistration registration = (EventRegistration) bean;
    Event event = registration.getEvent();
    try {
      if (registration.getAmount().compareTo(BigDecimal.ZERO)==0 ) {
        this.calculateAmounts(registration, event);
      }
      if (event.getTotalEntry() < event.getCapacity()) {
        this.saveEventRegistration(registration);
      } else {
        throw new Exception(I18n.get("Booking Closed Event is Housefull"));
      }
    } catch (Exception e) {
      System.err.println("Error in loading Registration of :"+registration.getName()+": "+e.getMessage()); 
      registration = null;
    }
    return registration;
  }
}
