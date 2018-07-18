package io.opentracing.contrib.java.wildfly.initializer;

import static org.jboss.logging.Logger.Level.INFO;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode = "TRACING-INIT")
public interface TracerInitializerLogger extends BasicLogger {
  TracerInitializerLogger ROOT_LOGGER = Logger.getMessageLogger(TracerInitializerLogger.class, TracerInitializerLogger.class.getPackage().getName());

  @LogMessage(level = INFO)
  @Message(id = 1, value = "Initializing Jaeger tracer: %s")
  void initializing(String message);

  @LogMessage(level = INFO)
  @Message(id = 2, value = "A Tracer is already registered at the GlobalTracer. Skipping resolution.")
  void alreadyRegistered();

  @LogMessage(level = INFO)
  @Message(id = 3, value = "Could not get a valid OpenTracing Tracer from the classpath. Skipping.")
  void noTracersAvailable();

  @LogMessage(level = INFO)
  @Message(id = 4, value = "Registering %s as the OpenTracing Tracer")
  void registering(String message);
}
