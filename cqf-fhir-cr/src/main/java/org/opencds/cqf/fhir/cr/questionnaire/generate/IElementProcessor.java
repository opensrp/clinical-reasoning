package org.opencds.cqf.fhir.cr.questionnaire.generate;

import static org.opencds.cqf.fhir.cr.common.ItemValueTransformer.transformValueToItem;

import java.util.Collections;
import org.hl7.fhir.instance.model.api.IBaseBackboneElement;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.opencds.cqf.fhir.api.Repository;
import org.opencds.cqf.fhir.cr.common.IOperationRequest;
import org.opencds.cqf.fhir.utility.CqfExpression;

public interface IElementProcessor {
    IBaseBackboneElement processElement(
            GenerateRequest request,
            ICompositeType element,
            String elementType,
            String childLinkId,
            CqfExpression caseFeature,
            Boolean isGroup);

    public static IElementProcessor createProcessor(Repository repository) {
        switch (repository.fhirContext().getVersion().getVersion()) {
            case R4:
                return new org.opencds.cqf.fhir.cr.questionnaire.generate.r4.ElementProcessor(repository);
            case R5:
                return new org.opencds.cqf.fhir.cr.questionnaire.generate.r5.ElementProcessor(repository);
            default:
                return null;
        }
    }

    public static Object createInitial(IOperationRequest request, IBaseDatatype value) {
        switch (request.getFhirVersion()) {
            case R4:
                return Collections.singletonList(
                        new org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemInitialComponent()
                                .setValue(transformValueToItem((org.hl7.fhir.r4.model.Type) value)));
            case R5:
                return Collections.singletonList(
                        new org.hl7.fhir.r5.model.Questionnaire.QuestionnaireItemInitialComponent()
                                .setValue(transformValueToItem((org.hl7.fhir.r5.model.DataType) value)));
            default:
                return null;
        }
    }
}
