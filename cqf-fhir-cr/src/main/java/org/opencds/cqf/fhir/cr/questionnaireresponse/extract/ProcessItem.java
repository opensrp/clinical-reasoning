package org.opencds.cqf.fhir.cr.questionnaireresponse.extract;

import java.util.List;
import java.util.Map;
import org.hl7.fhir.instance.model.api.IBaseBackboneElement;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.IBaseExtension;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.opencds.cqf.fhir.utility.Constants;

public class ProcessItem {
    public void processItem(
            ExtractRequest request,
            IBaseBackboneElement answerItem,
            IBaseBackboneElement questionnaireItem,
            Map<String, List<IBaseCoding>> questionnaireCodeMap,
            List<IBaseResource> resources,
            IBaseReference subject) {
        if (questionnaireCodeMap == null || questionnaireCodeMap.isEmpty()) {
            throw new IllegalArgumentException(
                    "Unable to retrieve Questionnaire code map for Observation based extraction");
        }
        var categoryExt =
                request.getExtensionByUrl(answerItem, Constants.SDC_QUESTIONNAIRE_OBSERVATION_EXTRACT_CATEGORY);
        var answers = request.resolvePathList(answerItem, "answer", IBaseBackboneElement.class);
        if (!answers.isEmpty()) {
            answers.forEach(answer -> {
                var answerItems = request.getItems(answer);
                if (!answerItems.isEmpty()) {
                    answerItems.forEach(answerChild -> processItem(
                            request, answerChild, questionnaireItem, questionnaireCodeMap, resources, subject));
                } else {
                    var linkId = request.resolvePathString(answerItem, "linkId");
                    if (questionnaireCodeMap.get(linkId) != null
                            && !questionnaireCodeMap.get(linkId).isEmpty()) {
                        resources.add(createObservationFromItemAnswer(
                                request,
                                answer,
                                questionnaireItem,
                                linkId,
                                subject,
                                questionnaireCodeMap,
                                categoryExt));
                    }
                }
            });
        }
    }

    private IBaseResource createObservationFromItemAnswer(
            ExtractRequest request,
            IBaseBackboneElement answer,
            IBaseBackboneElement questionnaireItem,
            String linkId,
            IBaseReference subject,
            Map<String, List<IBaseCoding>> questionnaireCodeMap,
            IBaseExtension<?, ?> categoryExt) {
        // Observation-based extraction -
        // http://build.fhir.org/ig/HL7/sdc/extraction.html#observation-based-extraction
        switch (request.getFhirVersion()) {
            case R4:
                return new org.opencds.cqf.fhir.cr.questionnaireresponse.extract.r4.ObservationResolver()
                        .resolve(
                                request, answer, questionnaireItem, linkId, subject, questionnaireCodeMap, categoryExt);
            case R5:
                return new org.opencds.cqf.fhir.cr.questionnaireresponse.extract.r5.ObservationResolver()
                        .resolve(
                                request, answer, questionnaireItem, linkId, subject, questionnaireCodeMap, categoryExt);

            default:
                return null;
        }
    }
}
