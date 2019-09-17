package com.axelor.apps.event.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.event.service.EventRegistrationService;
import com.axelor.apps.event.service.EventRegistrationServiceImpl;
import com.axelor.apps.event.service.EventService;
import com.axelor.apps.event.service.EventServiceImpl;

public class EventModule extends AxelorModule{

  @Override
  protected void configure() {
    bind(EventRegistrationService.class).to(EventRegistrationServiceImpl.class);
    bind(EventService.class).to(EventServiceImpl.class);
  }}
