package com.axelor.apps.event.web;

import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.axelor.app.AppSettings;
import com.axelor.apps.event.db.Event;
import com.axelor.apps.event.db.EventRegistration;
import com.axelor.apps.event.db.repo.EventRegistrationRepository;
import com.axelor.apps.event.db.repo.EventRepository;
import com.axelor.apps.event.service.EventService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class EventController {

  EventRegistrationRepository registrationRepo;

  @Inject
  public EventController(EventRegistrationRepository registrationRepo) {
    this.registrationRepo = registrationRepo;
  }

  public void validates(ActionRequest request, ActionResponse response) {
    Event event = request.getContext().asType(Event.class);
    try {
      if (event.getStartDate() != null && event.getEndDate().isBefore(event.getStartDate())) {
        throw new Exception(I18n.get("End Date Cannot be Before Start Date"));
      }
      if (event.getRegistrationOpen() != null
          && event.getRegistrationClose().isBefore(event.getRegistrationOpen())) {
        throw new Exception(I18n.get("Closing date Cannot be Before of Opening Date"));
      }
      if (event.getRegistrationClose().isAfter(event.getStartDate().toLocalDate())) {
        throw new Exception(
            I18n.get(
                "Closing Date of Registration can never be After the Start Date of the Event"));
      }
      if (event.getCapacity() == 0 || event.getTotalEntry() > event.getCapacity()) {
        throw new Exception(I18n.get("No. of Registration is more Than total capacity of Event"));
      }
    } catch (Exception e) {
      response.setError(e.getMessage());
    }
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
      event.setTotalDiscount(
          event
              .getEventFee()
              .multiply(new BigDecimal(event.getEventRegistration().size()))
              .subtract(totalAmount));
    }
    event.setAmountCollected(totalAmount);
    response.setValues(event);
  }

  public void setTotalEntries(ActionRequest request, ActionResponse response) {
    Event event = request.getContext().asType(Event.class);
    if (event.getEventRegistration() != null && !event.getEventRegistration().isEmpty()) {
      event.setTotalEntry(event.getEventRegistration().size());
    }
    response.setValues(event);
  }

  @Transactional
  public void importRegistrations(ActionRequest request, ActionResponse response) {
    @SuppressWarnings("unchecked")
    Map<String, Object> dataFile = (Map<String, Object>) request.getContext().get("dataFile");
    Event event =
        Beans.get(EventRepository.class)
            .find(((Integer) request.getContext().get("_id")).longValue());
    try {
      if (dataFile != null) {
        String fileName = (String) dataFile.get("fileName");
        if (fileName.contains(".csv")) {
          File data =
              new File("/home/axelor/data/attachments/EventApp/" + dataFile.get("filePath"));
          CSVReader csvReader = new CSVReaderBuilder(new FileReader(data)).withSkipLines(1).build();

          String[] nextRecord;
          while ((nextRecord = csvReader.readNext()) != null) {
            EventRegistration registration = new EventRegistration();
            registration.setEvent(event);
            registration.setName(nextRecord[0]);
            registration.setEmail(nextRecord[1]);

            event.getEventRegistration().add(registration);
            registrationRepo.save(registration);
          }
        } else {
          throw new Exception(I18n.get("Only '.csv' file should be uploaded"));
        }
      }
    } catch (Exception e) {
      response.setError(e.getMessage());
    }
  }
  
  public void setEmailBoolean(ActionRequest request, ActionResponse response) {
    Map<String, Object> context = request.getContext();
    Beans.get(EventService.class).setEmailBoolean((Integer) context.get("_event")); 
  }
}
