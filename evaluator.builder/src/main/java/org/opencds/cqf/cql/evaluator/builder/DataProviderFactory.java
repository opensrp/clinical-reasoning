package org.opencds.cqf.cql.evaluator.builder;

import org.hl7.fhir.instance.model.api.IBaseBundle;

public interface DataProviderFactory {
    public DataProviderComponents create(EndpointInfo endpointInfo);
    public DataProviderComponents  create(IBaseBundle dataBundle);
}