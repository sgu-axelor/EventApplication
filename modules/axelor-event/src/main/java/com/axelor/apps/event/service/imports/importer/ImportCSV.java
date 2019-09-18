package com.axelor.apps.event.service.imports.importer;

import java.util.Map;

import com.axelor.data.csv.CSVImporter;

public class ImportCSV {

  public void process(String bind, String data,Map<String,Object> context) {

    CSVImporter importer = new CSVImporter(bind, data);
    try {
      importer.setContext(context);
      importer.run();
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
