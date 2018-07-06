package io.opentracing.contrib.wildfly;

import java.util.Arrays;
import java.util.Collection;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.registry.ManagementResourceRegistration;

public class SubsystemDefinition extends PersistentResourceDefinition {
    static AttributeDefinition[] ATTRIBUTES = {};

    protected SubsystemDefinition() {
        super(
            SubsystemExtension.SUBSYSTEM_PATH,
            SubsystemExtension.getResourceDescriptionResolver(SubsystemExtension.SUBSYSTEM_NAME),
            SubsystemAdd.INSTANCE,
            SubsystemRemove.INSTANCE
        );
    }

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Arrays.asList(ATTRIBUTES);
    }

    @Override
    public void registerOperations(ManagementResourceRegistration resourceRegistration) {
        super.registerOperations(resourceRegistration);
    }
}
