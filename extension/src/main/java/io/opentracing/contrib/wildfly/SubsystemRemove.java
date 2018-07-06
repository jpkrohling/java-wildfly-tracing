package io.opentracing.contrib.wildfly;

import org.jboss.as.controller.AbstractRemoveStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;

class SubsystemRemove extends AbstractRemoveStepHandler {
    private static final Logger log = Logger.getLogger(SubsystemAdd.class);
    static final SubsystemRemove INSTANCE = new SubsystemRemove();

    private SubsystemRemove() {
    }

    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model) {
        log.info("Removing tracing subsystem");
    }
}
