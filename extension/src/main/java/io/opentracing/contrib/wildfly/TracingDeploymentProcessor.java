package io.opentracing.contrib.wildfly;

import io.opentracing.contrib.java.wildfly.initializer.TracerInitializer;
import io.opentracing.contrib.jaxrs2.server.ServerTracingDynamicFeature;
import io.opentracing.contrib.jaxrs2.server.SpanFinishingFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.deployment.module.ModuleDependency;
import org.jboss.as.server.deployment.module.ModuleSpecification;
import org.jboss.as.web.common.WarMetaData;
import org.jboss.metadata.javaee.spec.ParamValueMetaData;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.metadata.web.spec.FilterMappingMetaData;
import org.jboss.metadata.web.spec.FilterMetaData;
import org.jboss.metadata.web.spec.FiltersMetaData;
import org.jboss.metadata.web.spec.ListenerMetaData;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;

public class TracingDeploymentProcessor implements DeploymentUnitProcessor {
  public static final ModuleIdentifier TRACING_INITIALIZER = ModuleIdentifier.create("io.opentracing.contrib.initializer");

  public static final Phase PHASE = Phase.DEPENDENCIES;
  public static final int PRIORITY = 0x4000;

  @Override
  public void deploy(DeploymentPhaseContext deploymentPhaseContext) {
    TracingLogger.ROOT_LOGGER.processingDeployment();
    DeploymentUnit deploymentUnit = deploymentPhaseContext.getDeploymentUnit();

    addDependencies(deploymentUnit);

    configureServiceName(deploymentUnit);
    addListeners(deploymentUnit);
    addJaxRsIntegration(deploymentUnit);
  }

  @Override
  public void undeploy(DeploymentUnit deploymentUnit) {
  }

  private void addDependencies(DeploymentUnit deploymentUnit) {
    final ModuleSpecification moduleSpecification = deploymentUnit.getAttachment(Attachments.MODULE_SPECIFICATION);
    final ModuleLoader moduleLoader = Module.getBootModuleLoader();

    moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, TRACING_INITIALIZER, false, false, true, false));
  }

  private void configureServiceName(DeploymentUnit deploymentUnit) {
    String serviceName = System.getProperty("JAEGER_SERVICE_NAME");
    if (null == serviceName || serviceName.isEmpty()) {
      serviceName = System.getenv("JAEGER_SERVICE_NAME");
    }

    if (null == serviceName || serviceName.isEmpty()) {
      System.setProperty("JAEGER_SERVICE_NAME", deploymentUnit.getServiceName().getSimpleName());
    }

    // TODO: these are useful for development, but shouldn't be there on the final revision!
    System.setProperty("JAEGER_REPORTER_LOG_SPANS", "true");
    System.setProperty("JAEGER_SAMPLER_TYPE", "const");
    System.setProperty("JAEGER_SAMPLER_PARAM", "1");
  }

  private void addListeners(DeploymentUnit deploymentUnit) {
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

  private void addJaxRsIntegration(DeploymentUnit deploymentUnit) {
    WarMetaData warMetaData = deploymentUnit.getAttachment(WarMetaData.ATTACHMENT_KEY);
    JBossWebMetaData jbossWebMetaData = warMetaData.getMergedJBossWebMetaData();

    // Filters
    FilterMetaData filterMetaData = new FilterMetaData();
    filterMetaData.setFilterClass(SpanFinishingFilter.class.getName());
    filterMetaData.setAsyncSupported(true);
    filterMetaData.setName(SpanFinishingFilter.class.getTypeName());

    FiltersMetaData filters = jbossWebMetaData.getFilters();
    if (null == filters) {
      filters = new FiltersMetaData();
    }
    filters.add(filterMetaData);
    jbossWebMetaData.setFilters(filters);

    // Filter mappings
    FilterMappingMetaData filterMapping = new FilterMappingMetaData();
    filterMapping.setFilterName(SpanFinishingFilter.class.getTypeName());
    filterMapping.setUrlPatterns(Collections.singletonList("/*"));

    List<FilterMappingMetaData> filterMappings = jbossWebMetaData.getFilterMappings();
    if (null == filterMappings) {
      filterMappings = new ArrayList<>();
    }
    filterMappings.add(filterMapping);
    jbossWebMetaData.setFilterMappings(filterMappings);

    // RESTeasy Dynamic Feature
    ParamValueMetaData restEasyProvider = new ParamValueMetaData();
    restEasyProvider.setParamName("resteasy.providers");
    restEasyProvider.setParamValue(ServerTracingDynamicFeature.class.getName());

    List<ParamValueMetaData> contextParams = jbossWebMetaData.getContextParams();
    if (null == contextParams) {
      contextParams = new ArrayList<>();
    }
    contextParams.add(restEasyProvider);
    jbossWebMetaData.setContextParams(contextParams);
  }
}
