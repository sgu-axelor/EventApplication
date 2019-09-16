package com.axelor.apps.event.service.imports.importer;

import com.axelor.data.csv.CSVImporter;

public class ImportCSV {

  protected void process(String bind, String data) {

    CSVImporter importer = new CSVImporter(bind, data);
    try {
    importer.addListener(null);
    importer.setContext(null);
    importer.run();
    }catch (Exception e) {
      System.err.println(e.getMessage() ); 
    }
  }
}
