package com.axelor.apps.event.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.event.service.EventRegistrationService;
import com.axelor.apps.event.service.EventRegistrationServiceImpl;

public class EventModule extends AxelorModule{

  @Override
  protected void configure() {
    bind(EventRegistrationService.class).to(EventRegistrationServiceImpl.class);
  }}
