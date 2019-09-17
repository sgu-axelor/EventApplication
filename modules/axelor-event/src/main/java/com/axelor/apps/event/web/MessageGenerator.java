package com.axelor.apps.event.web;

import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import com.axelor.apps.base.db.Wizard;
import com.axelor.apps.message.db.Message;
import com.axelor.apps.message.db.Template;
import com.axelor.apps.message.db.repo.MessageRepository;
import com.axelor.apps.message.db.repo.TemplateRepository;
import com.axelor.apps.message.exception.IExceptionMessage;
import com.axelor.apps.message.web.GenerateMessageController;
import com.axelor.db.Model;
import com.axelor.db.Query;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class MessageGenerator {

  @Inject private TemplateRepository templateRepo;

  public void callMessageWizard(ActionRequest request, ActionResponse response) {

    Model context = request.getContext().asType(Model.class);
    String model = request.getModel();
    String[] decomposeModel = model.split("\\.");
    String simpleModel = decomposeModel[decomposeModel.length - 1];

    Query<? extends Template> templateQuery =
        templateRepo.all().filter("self.metaModel.fullName = ?1 AND self.isSystem != true", model);
    try {
      long templateNumber = templateQuery.count();
      Map<String, Object> view = new HashedMap();
      if (templateNumber == 0) {
           view = ActionView.define(I18n.get(IExceptionMessage.MESSAGE_3))
               .model(Message.class.getName())
               .add("form", "message-form")
               .param("forceEdit", "true")
               .context("_mediaTypeSelect", MessageRepository.MEDIA_TYPE_EMAIL)
               .context("_templateContextModel", model)
               .context("_objectId", context.getId().toString())
               .map();
      } else if (templateNumber > 1) {
        view = ActionView.define(I18n.get(IExceptionMessage.MESSAGE_2))
                .model(Wizard.class.getName())
                .add("form", "generate-message-wizard-form")
                .param("show-confirm", "false")
                .context("_objectId", context.getId().toString())
                .context("_templateContextModel", model)
                .context("_tag", simpleModel)
                .map();
      } else {
        view =
            Beans.get(GenerateMessageController.class)
                .generateMessage(context.getId(), model, simpleModel, templateQuery.fetchOne());
      }
      Map<String, Object> viewContext = (Map<String, Object>) view.get("context");
      viewContext.put("_event", request.getContext().get("id"));
      response.setView(view);
    } catch (Exception e) {
      response.setError(e.getMessage());
    }
  }
}
