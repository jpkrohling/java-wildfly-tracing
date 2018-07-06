package io.opentracing.contrib.wildfly.extension;

import io.opentracing.contrib.wildfly.SubsystemExtension;
import java.io.IOException;
import java.util.Properties;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;

/**
 * This test case is based on the SimpleSubsystemTestCase from the Wildfly test suite.
 *
 */
public class Subsystem_1_0_ParsingTestCase extends AbstractSubsystemBaseTest {
  public Subsystem_1_0_ParsingTestCase() {
    super(SubsystemExtension.SUBSYSTEM_NAME, new SubsystemExtension());
  }

  @Override
  protected String getSubsystemXml() throws IOException {
    return readResource("tracing_1_0.xml");
  }

  @Override
  protected String getSubsystemXsdPath() throws IOException {
    return "schema/tracing-extension_1_0.xsd";
  }

  @Override
  protected String[] getSubsystemTemplatePaths() throws IOException {
    return new String[] {
        "/subsystem-templates/tracing.xml"
    };
  }

  protected Properties getResolvedProperties() {
    return System.getProperties();
  }
}