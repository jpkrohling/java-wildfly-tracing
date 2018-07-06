package io.opentracing.contrib.wildfly;

import org.jboss.logging.Logger;
import org.jboss.msc.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StopContext;

public class TracingService implements Service {
  private static final Logger log = Logger.getLogger(TracingService.class);

  public static final ServiceName NAME = ServiceName.of("opentracing", "tracer");

  @Override
  public void start(StartContext context) {
    log.info("Starting Tracing Service");
  }

  @Override
  public void stop(StopContext context) {
    log.info("Stopping Tracing Service");
  }

}