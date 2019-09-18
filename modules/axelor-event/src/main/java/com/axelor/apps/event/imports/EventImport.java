package com.axelor.apps.event.imports;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import com.axelor.apps.event.service.imports.importer.ImportCSV;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.google.inject.Inject;

public class EventImport {

  @Inject private MetaFileRepository metaFileRepo;

  public void importRegistrations(
      Integer _eventId, Map<String, Object> dataFile, Map<String, Object> bindFile) {
    MetaFile metaDataFile = metaFileRepo.find(((Integer) dataFile.get("id")).longValue());
    MetaFile metaBindingFile = metaFileRepo.find(((Integer) bindFile.get("id")).longValue());
    
    Map<String, Object> context = new HashMap<String,Object>();
    context.put("id", _eventId);
    Beans.get(ImportCSV.class)
    .process(
        MetaFiles.getPath(metaBindingFile).toFile().getAbsolutePath(), "/home/axelor/data/attachments/EventApp",context);
  }
}
