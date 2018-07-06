package io.opentracing.contrib.wildfly;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.registry.Resource;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.dmr.ModelNode;

class TracingSubsystemAdd extends AbstractBoottimeAddStepHandler {

  static final TracingSubsystemAdd INSTANCE = new TracingSubsystemAdd();

  private TracingSubsystemAdd() {
  }

  /** {@inheritDoc} */
  @Override
  protected void populateModel(ModelNode operation, ModelNode model) {
    model.setEmptyObject();
  }

  /** {@inheritDoc} */
  @Override
  public void performBoottime(OperationContext context, ModelNode operation, Resource resource) {

    //Add deployment processors here
    //Remove this if you don't need to hook into the deployers, or you can add as many as you like
    //see SubDeploymentProcessor for explanation of the phases
    context.addStep(new AbstractDeploymentChainStep() {
      public void execute(DeploymentProcessorTarget processorTarget) {
        processorTarget.addDeploymentProcessor(TracingSubsystemExtension.SUBSYSTEM_NAME, TracingSubsystemDeploymentProcessor.PHASE, TracingSubsystemDeploymentProcessor.PRIORITY, new TracingSubsystemDeploymentProcessor());
      }
    }, OperationContext.Stage.RUNTIME);

    context.getServiceTarget().addService(TracingService.NAME).install();
  }
}