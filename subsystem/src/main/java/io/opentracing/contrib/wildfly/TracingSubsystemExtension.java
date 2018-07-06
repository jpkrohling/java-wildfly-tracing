package io.opentracing.contrib.wildfly;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

import java.util.List;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.jboss.as.controller.Extension;
import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.ModelVersion;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.SubsystemRegistration;
import org.jboss.as.controller.descriptions.NonResolvingResourceDescriptionResolver;
import org.jboss.as.controller.operations.common.GenericSubsystemDescribeHandler;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.parsing.ParseUtils;
import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLElementWriter;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

public class TracingSubsystemExtension implements Extension {
  public static final String NAMESPACE = "urn:io.opentracing.contrib:wildfly:1.0";
  public static final String SUBSYSTEM_NAME = "opentracing";

  /** The parser used for parsing our subsystem */
  private final SubsystemParser parser = new SubsystemParser();

  @Override
  public void initializeParsers(ExtensionParsingContext context) {
    context.setSubsystemXmlMapping(SUBSYSTEM_NAME, NAMESPACE, parser);
  }

  @Override
  public void initialize(ExtensionContext context) {
    final SubsystemRegistration subsystem = context.registerSubsystem(SUBSYSTEM_NAME, ModelVersion.create(1));
    final ManagementResourceRegistration registration = subsystem.registerSubsystemModel(new SimpleResourceDefinition(
        PathElement.pathElement(SUBSYSTEM, SUBSYSTEM_NAME),
        new NonResolvingResourceDescriptionResolver(),
        TracingSubsystemAdd.INSTANCE,
        TracingSubsystemRemove.INSTANCE
    ));
    //We always need to add a 'describe' operation
    registration.registerOperationHandler(GenericSubsystemDescribeHandler.DEFINITION, GenericSubsystemDescribeHandler.INSTANCE);
    subsystem.registerXMLElementWriter(parser);
  }

  /**
   * The subsystem parser, which uses stax to read and write to and from xml
   */
  private static class SubsystemParser implements XMLStreamConstants, XMLElementReader<List<ModelNode>>,
      XMLElementWriter<SubsystemMarshallingContext> {

    /** {@inheritDoc} */
    @Override
    public void writeContent(XMLExtendedStreamWriter writer, SubsystemMarshallingContext context)
        throws XMLStreamException {
      context.startSubsystemElement(TracingSubsystemExtension.NAMESPACE, false);
      writer.writeEndElement();
    }

    /** {@inheritDoc} */
    @Override
    public void readElement(XMLExtendedStreamReader reader, List<ModelNode> list) throws XMLStreamException {
      // Require no content
      ParseUtils.requireNoContent(reader);
      list.add(Util.createAddOperation(PathAddress.pathAddress(PathElement.pathElement(SUBSYSTEM, SUBSYSTEM_NAME))));
    }
  }


}
