package io.opentracing.contrib.wildfly;

import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.deployment.module.ModuleDependency;
import org.jboss.as.server.deployment.module.ModuleSpecification;
import org.jboss.logging.Logger;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;

public class TracingSubsystemDeploymentProcessor implements DeploymentUnitProcessor {
  private static final Logger log = Logger.getLogger(TracingSubsystemDeploymentProcessor.class);

  private static final ModuleIdentifier OPENTRACING_API = ModuleIdentifier.create("org.keycloak.keycloak-jboss-adapter-core");
  private static final ModuleIdentifier OPENTRACING_TRACER_RESOLVER = ModuleIdentifier.create("org.keycloak.keycloak-jboss-adapter-core");
  private static final ModuleIdentifier OPENTRACING_CONTRIB_SERVLET = ModuleIdentifier.create("org.keycloak.keycloak-jboss-adapter-core");
  private static final ModuleIdentifier OPENTRACING_CONTRIB_CDI = ModuleIdentifier.create("org.keycloak.keycloak-jboss-adapter-core");
  private static final ModuleIdentifier OPENTRACING_CONTRIB_JAXRS = ModuleIdentifier.create("org.keycloak.keycloak-jboss-adapter-core");
  private static final ModuleIdentifier OPENTRACING_CONTRIB_EJB = ModuleIdentifier.create("org.keycloak.keycloak-jboss-adapter-core");
  private static final ModuleIdentifier OPENTRACING_CONTRIB_JMS = ModuleIdentifier.create("org.keycloak.keycloak-jboss-adapter-core");
  private static final ModuleIdentifier JAEGER_TRACER_RESOLVER = ModuleIdentifier.create("org.keycloak.keycloak-jboss-adapter-core");

  /**
   * See {@link Phase} for a description of the different phases
   */
  public static final Phase PHASE = Phase.DEPENDENCIES;

  /**
   * The relative order of this processor within the {@link #PHASE}.
   * The current number is large enough for it to happen after all
   * the standard deployment unit processors that come with JBoss AS.
   */
  public static final int PRIORITY = 0x4000;

  @Override
  public void deploy(DeploymentPhaseContext phaseContext) {
    log.info("Installing tracing components into deployment " + phaseContext.toString());

    final DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
    final ModuleSpecification moduleSpecification = deploymentUnit.getAttachment(Attachments.MODULE_SPECIFICATION);
    final ModuleLoader moduleLoader = Module.getBootModuleLoader();

    // add the dependencies
    addDependency(moduleSpecification, moduleLoader, OPENTRACING_API);
    addDependency(moduleSpecification, moduleLoader, OPENTRACING_TRACER_RESOLVER);
    addDependency(moduleSpecification, moduleLoader, OPENTRACING_CONTRIB_SERVLET);
    addDependency(moduleSpecification, moduleLoader, OPENTRACING_CONTRIB_CDI);
    addDependency(moduleSpecification, moduleLoader, OPENTRACING_CONTRIB_JAXRS);
    addDependency(moduleSpecification, moduleLoader, OPENTRACING_CONTRIB_EJB);
    addDependency(moduleSpecification, moduleLoader, OPENTRACING_CONTRIB_JMS);
    addDependency(moduleSpecification, moduleLoader, JAEGER_TRACER_RESOLVER);
  }

  private void addDependency(ModuleSpecification moduleSpecification, ModuleLoader moduleLoader, ModuleIdentifier module) {
    moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, module, false, false, false, false));
  }

  @Override
  public void undeploy(DeploymentUnit context) {
  }

}