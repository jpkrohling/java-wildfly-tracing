package io.opentracing.contrib.wildfly;

import io.opentracing.contrib.java.wildfly.initializer.TracerInitializer;
import java.util.ArrayList;
import java.util.List;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.moduleservice.ServiceModuleLoader;
import org.jboss.as.web.common.WarMetaData;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.metadata.web.spec.FiltersMetaData;
import org.jboss.metadata.web.spec.ListenerMetaData;
import org.jboss.modules.ModuleLoadException;

public class TracingDeploymentProcessor implements DeploymentUnitProcessor {
  public static final Phase PHASE = Phase.DEPENDENCIES;
  public static final int PRIORITY = 0x4000;

  @Override
  public void deploy(DeploymentPhaseContext deploymentPhaseContext) {
    TracingLogger.ROOT_LOGGER.processingDeployment();
    DeploymentUnit deploymentUnit = deploymentPhaseContext.getDeploymentUnit();

    ServiceModuleLoader loader = deploymentUnit.getAttachment(Attachments.SERVICE_MODULE_LOADER);
    try {
      loader.loadModule("io.opentracing.contrib.initializer");
      loader.loadModule("io.smallrye.opentracing");
    } catch (ModuleLoadException e) {
      e.printStackTrace();
    }

    WarMetaData warMetaData = deploymentUnit.getAttachment(WarMetaData.ATTACHMENT_KEY);
    JBossWebMetaData jbossWebMetaData = warMetaData.getMergedJBossWebMetaData();

    ListenerMetaData listenerMetaData = new ListenerMetaData();
    listenerMetaData.setListenerClass(TracerInitializer.class.getName());

    List<ListenerMetaData> listeners = jbossWebMetaData.getListeners();
    if (null == listeners) {
      listeners = new ArrayList<>();
    }
    listeners.add(listenerMetaData);
    jbossWebMetaData.setListeners(listeners);

  }

  @Override
  public void undeploy(DeploymentUnit deploymentUnit) {

  }
}
