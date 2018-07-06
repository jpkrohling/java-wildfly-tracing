package io.opentracing.contrib.wildfly.extension;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DESCRIBE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

import io.opentracing.contrib.wildfly.TracingService;
import io.opentracing.contrib.wildfly.TracingSubsystemExtension;
import java.util.List;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.subsystem.test.AbstractSubsystemTest;
import org.jboss.as.subsystem.test.AdditionalInitialization;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceNotFoundException;
import org.junit.Assert;
import org.junit.Test;

/**
 * This test case is based on the SimpleSubsystemTestCase from the Wildfly test suite.
 *
 */
public class TracingSubsystemTestCase extends AbstractSubsystemTest {
  public TracingSubsystemTestCase() {
    super(TracingSubsystemExtension.SUBSYSTEM_NAME, new TracingSubsystemExtension());
  }

  /**
   * Tests that the xml is parsed into the correct operations
   */
  @Test
  public void testParseSubsystem() throws Exception {
    //Parse the subsystem xml into operations
    String subsystemXml = "<subsystem xmlns=\"" + TracingSubsystemExtension.NAMESPACE + "\"></subsystem>";
    List<ModelNode> operations = super.parse(subsystemXml);

    ///Check that we have the expected number of operations
    Assert.assertEquals(1, operations.size());

    //Check that each operation has the correct content
    ModelNode addSubsystem = operations.get(0);
    Assert.assertEquals(ADD, addSubsystem.get(OP).asString());
    PathAddress addr = PathAddress.pathAddress(addSubsystem.get(OP_ADDR));
    Assert.assertEquals(1, addr.size());
    PathElement element = addr.getElement(0);
    Assert.assertEquals(SUBSYSTEM, element.getKey());
    Assert.assertEquals(TracingSubsystemExtension.SUBSYSTEM_NAME, element.getValue());
  }

  /**
   * Test that the model created from the xml looks as expected, and that the services are installed
   */
  @Test(expected = ServiceNotFoundException.class)
  public void testInstallIntoController() throws Exception {
    //Parse the subsystem xml and install into the controller
    String subsystemXml = "<subsystem xmlns=\"" + TracingSubsystemExtension.NAMESPACE + "\"></subsystem>";
    KernelServices services = createKernelServicesBuilder(null)
        .setSubsystemXml(subsystemXml)
        .build();

    //Read the whole model and make sure it looks as expected
    ModelNode model = services.readWholeModel();
    Assert.assertTrue(model.get(SUBSYSTEM).hasDefined(TracingSubsystemExtension.SUBSYSTEM_NAME));

    //Test that the service was installed
    services.getContainer().getRequiredService(TracingService.NAME);

    //Check that all the resources were removed
    super.assertRemoveSubsystemResources(services);
    services.getContainer().getRequiredService(TracingService.NAME);
  }

  /**
   * Test that the model created from the xml looks as expected, and that the services are NOT installed
   */
  @Test(expected = ServiceNotFoundException.class)
  public void testInstallIntoControllerModelOnly() throws Exception {
    //Parse the subsystem xml and install into the controller
    String subsystemXml = "<subsystem xmlns=\"" + TracingSubsystemExtension.NAMESPACE + "\"></subsystem>";

    KernelServices services = createKernelServicesBuilder(AdditionalInitialization.MANAGEMENT)
        .setSubsystemXml(subsystemXml)
        .build();

    //Read the whole model and make sure it looks as expected
    ModelNode model = services.readWholeModel();
    Assert.assertTrue(model.get(SUBSYSTEM).hasDefined(TracingSubsystemExtension.SUBSYSTEM_NAME));

    //Test that the service was not installed
    Assert.assertNull(services.getContainer().getService(TracingService.NAME));

    //Check that all the resources were removed
    super.assertRemoveSubsystemResources(services);
    services.getContainer().getRequiredService(TracingService.NAME);
  }

  /**
   * Starts a controller with a given subsystem xml and then checks that a second
   * controller started with the xml marshalled from the first one results in the same model
   */
  @Test
  public void testParseAndMarshalModel() throws Exception {
    //Parse the subsystem xml and install into the first controller
    String subsystemXml = "<subsystem xmlns=\"" + TracingSubsystemExtension.NAMESPACE + "\"></subsystem>";

    KernelServices servicesA = createKernelServicesBuilder(null)
        .setSubsystemXml(subsystemXml)
        .build();

    //Get the model and the persisted xml from the first controller
    ModelNode modelA = servicesA.readWholeModel();
    String marshalled = servicesA.getPersistedSubsystemXml();

    //Install the persisted xml from the first controller into a second controller
    KernelServices servicesB = createKernelServicesBuilder(null)
        .setSubsystemXml(marshalled)
        .build();

    ModelNode modelB = servicesB.readWholeModel();

    //Make sure the models from the two controllers are identical
    super.compare(modelA, modelB);
  }

  /**
   * Starts a controller with the given subsystem xml and then checks that a second
   * controller started with the operations from its describe action results in the same model
   */
  @Test
  public void testDescribeHandler() throws Exception {
    //Parse the subsystem xml and install into the first controller
    String subsystemXml = "<subsystem xmlns=\"" + TracingSubsystemExtension.NAMESPACE + "\"></subsystem>";

    KernelServices servicesA = createKernelServicesBuilder(null)
        .setSubsystemXml(subsystemXml)
        .build();

    //Get the model and the describe operations from the first controller
    ModelNode modelA = servicesA.readWholeModel();
    ModelNode describeOp = new ModelNode();
    describeOp.get(OP).set(DESCRIBE);
    describeOp.get(OP_ADDR).set(
        PathAddress.pathAddress(
            PathElement.pathElement(
                SUBSYSTEM, TracingSubsystemExtension.SUBSYSTEM_NAME)
        ).toModelNode()
    );
    List<ModelNode> operations = checkResultAndGetContents(servicesA.executeOperation(describeOp)).asList();


    //Install the describe options from the first controller into a second controller
    KernelServices servicesB = createKernelServicesBuilder(null)
        .setBootOperations(operations)
        .build();

    ModelNode modelB = servicesB.readWholeModel();

    //Make sure the models from the two controllers are identical
    super.compare(modelA, modelB);
  }

  /**
   * Tests that we can trigger output of the model, i.e. that outputModel() works as it should
   */
  @Test
  public void testOutputModel() throws Exception {
    String subsystemXml = "<subsystem xmlns=\"" + TracingSubsystemExtension.NAMESPACE + "\"></subsystem>";

    ModelNode testModel = new ModelNode();
    testModel.get(SUBSYSTEM).get(TracingSubsystemExtension.SUBSYSTEM_NAME).setEmptyObject();
    String triggered = outputModel(testModel);

    KernelServices services = createKernelServicesBuilder(AdditionalInitialization.MANAGEMENT)
        .setSubsystemXml(subsystemXml)
        .build();
    //Get the model and the persisted xml from the controller
    services.readWholeModel();
    String marshalled = services.getPersistedSubsystemXml();

    Assert.assertEquals(marshalled, triggered);
    Assert.assertEquals(normalizeXML(marshalled), normalizeXML(triggered));
  }
}