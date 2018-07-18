package io.opentracing.contrib.wildfly;

import static org.jboss.logging.Logger.Level.INFO;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode = "TRACING")
public interface TracingLogger extends BasicLogger {
  TracingLogger ROOT_LOGGER = Logger.getMessageLogger(TracingLogger.class, TracingLogger.class.getPackage().getName());

  @LogMessage(level = INFO)
  @Message(id = 1, value = "Activating Tracing Subsystem")
  void activatingSubsystem();

  @LogMessage(level = INFO)
  @Message(id = 2, value = "Tracing Subsystem is processing deployment")
  void processingDeployment();
}
