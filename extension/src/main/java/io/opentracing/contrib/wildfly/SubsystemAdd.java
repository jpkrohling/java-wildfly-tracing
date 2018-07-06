package io.opentracing.contrib.wildfly;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;

class SubsystemAdd extends AbstractBoottimeAddStepHandler {
    static final SubsystemAdd INSTANCE = new SubsystemAdd();

    private SubsystemAdd() {
        super(SubsystemDefinition.ATTRIBUTES);
    }

    @Override
    protected void performBoottime(OperationContext context, ModelNode operation, ModelNode model) throws OperationFailedException {
        TracingLogger.ROOT_LOGGER.activatingSubsystem();
        super.performBoottime(context, operation, model);
    }
}
