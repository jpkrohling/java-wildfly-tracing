package io.opentracing.contrib.wildfly;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.dmr.ModelNode;

class SubsystemAdd extends AbstractBoottimeAddStepHandler {
    static final SubsystemAdd INSTANCE = new SubsystemAdd();

    private SubsystemAdd() {
        super(SubsystemDefinition.ATTRIBUTES);
    }

    @Override
    protected void performBoottime(OperationContext context, ModelNode operation, ModelNode model) {
        TracingLogger.ROOT_LOGGER.activatingSubsystem();
        context.addStep(new AbstractDeploymentChainStep() {
            public void execute(DeploymentProcessorTarget processorTarget) {
                processorTarget.addDeploymentProcessor(
                    SubsystemExtension.SUBSYSTEM_NAME,
                    TracingDeploymentProcessor.PHASE,
                    TracingDeploymentProcessor.PRIORITY,
                    new TracingDeploymentProcessor()
                );
            }
        }, OperationContext.Stage.RUNTIME);
    }
}
