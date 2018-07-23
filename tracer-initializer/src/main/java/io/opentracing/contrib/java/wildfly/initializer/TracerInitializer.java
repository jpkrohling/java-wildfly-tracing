package io.opentracing.contrib.java.wildfly.initializer;

import io.opentracing.Tracer;
import io.opentracing.contrib.tracerresolver.TracerResolver;
import io.opentracing.util.GlobalTracer;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class TracerInitializer implements ServletContextListener {
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    if (GlobalTracer.isRegistered()) {
      TracerInitializerLogger.ROOT_LOGGER.alreadyRegistered();
      return;
    }

    Tracer tracer = TracerResolver.resolveTracer();
    if (null == tracer) {
      TracerInitializerLogger.ROOT_LOGGER.noTracersAvailable();
      return;
    }

    TracerInitializerLogger.ROOT_LOGGER.registering(tracer.getClass().getName());
    GlobalTracer.register(tracer);

    TracerInitializerLogger.ROOT_LOGGER.initializing(tracer.toString());
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
  }
}
