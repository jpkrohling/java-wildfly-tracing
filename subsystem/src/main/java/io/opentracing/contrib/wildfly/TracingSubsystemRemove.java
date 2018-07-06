package io.opentracing.contrib.wildfly;

import org.jboss.as.controller.AbstractRemoveStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;

class TracingSubsystemRemove extends AbstractRemoveStepHandler {
  static final TracingSubsystemRemove INSTANCE = new TracingSubsystemRemove();

  private TracingSubsystemRemove() {
  }

  @Override
  protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model)
      throws OperationFailedException {
    super.performRuntime(context, operation, model);
    context.removeService(TracingService.NAME);
  }
}