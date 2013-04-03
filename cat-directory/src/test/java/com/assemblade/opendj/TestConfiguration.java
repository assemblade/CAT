package com.assemblade.opendj;

import com.assemblade.opendj.model.AbstractConfiguration;
import com.assemblade.opendj.model.ConfigurationDecorator;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeType;
import org.opends.server.types.Entry;
import org.opends.server.types.Modification;
import org.opends.server.types.ObjectClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestConfiguration extends AbstractConfiguration implements Serializable {
    public String rootDn;
    public String javaClass;
    public String searchFilter;

    public transient Map<ObjectClass, String> objectClasses = new HashMap<ObjectClass, String>();
    public transient Map<AttributeType, List<Attribute>> userAttributes = new HashMap<AttributeType, List<Attribute>>();
    public transient Map<AttributeType, List<Attribute>> operationalAttributes = new HashMap<AttributeType, List<Attribute>>();
    public transient List<Modification> modifications = new ArrayList<Modification>();

    public Entry entry;

    public void addObjectClasses(String... objectClasses) {
        for (String objectClass : objectClasses) {
            this.objectClasses.put(DirectoryServer.getObjectClass(objectClass.toLowerCase()), objectClass);
        }
    }

    public void addUserAttribute(String name, String value) {
        LdapUtils.addSingleValueAttributeToMap(userAttributes, name, value);
    }

    public Map<ObjectClass, String> getObjectClasses() {
        return objectClasses;
    }

    public Map<AttributeType, List<Attribute>> getUserAttributes() {
        return userAttributes;
    }

    @Override
    public String getRootDn() {
        return rootDn;
    }

    @Override
    public String getJavaClass() {
        return javaClass;
    }

    @Override
    public String getSearchFilter() {
        return searchFilter;
    }

    @Override
    public ConfigurationDecorator getDecorator() {
        return new Decorator();
    }

    private class Decorator extends AbstractConfiguration.Decorator<TestConfiguration> {
        @Override
        public TestConfiguration newInstance() {
            return new TestConfiguration();
        }

        @Override
        public TestConfiguration decorate(Entry entry) {
            TestConfiguration configuration = new TestConfiguration();
            configuration.entry = entry;
            return configuration;
        }
    }

}
