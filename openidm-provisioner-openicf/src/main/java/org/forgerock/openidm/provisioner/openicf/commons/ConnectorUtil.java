/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright © 2011 ForgeRock AS. All rights reserved.
 * 
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * $Id$
 */
package org.forgerock.openidm.provisioner.openicf.commons;

import org.forgerock.json.fluent.JsonNode;
import org.forgerock.json.fluent.JsonNodeException;
import org.forgerock.json.schema.validator.Constants;
import org.forgerock.json.schema.validator.exceptions.SchemaException;
import org.forgerock.openidm.provisioner.openicf.ConnectorReference;
import org.forgerock.openidm.provisioner.openicf.impl.ConnectorObjectOptions;
import org.identityconnectors.common.Base64;
import org.identityconnectors.common.pooling.ObjectPoolConfiguration;
import org.identityconnectors.common.script.Script;
import org.identityconnectors.common.script.ScriptBuilder;
import org.identityconnectors.common.security.GuardedByteArray;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.api.*;
import org.identityconnectors.framework.api.operations.*;
import org.identityconnectors.framework.common.objects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.forgerock.json.schema.validator.Constants.*;

/**
 * Sample Class Doc
 *
 * @author $author$
 * @version $Revision$ $Date$
 */
public class ConnectorUtil {
    private final static Logger TRACE = LoggerFactory.getLogger(ConnectorUtil.class);

    private static final String OPENICF_BUNDLENAME = "bundleName";
    private static final String OPENICF_BUNDLEVERSION = "bundleVersion";
    private static final String OPENICF_CONNECTOR_NAME = "connectorName";
    private static final String OPENICF_CONNECTOR_HOST_REF = "connectorHostRef";
    private static final String OPENICF_HOST = "host";
    private static final String OPENICF_PORT = "port";
    private static final String OPENICF_KEY = "key";
    private static final String OPENICF_USE_SSL = "useSSL";
    private static final String OPENICF_TRUST_MANAGERS = "trustManagers";
    private static final String OPENICF_TIMEOUT = "timeout";
    private static final String OPENICF_MAX_OBJECTS = "maxObjects";
    private static final String OPENICF_MAX_IDLE = "maxIdle";
    private static final String OPENICF_MAX_WAIT = "maxWait";
    private static final String OPENICF_MIN_EVICTABLE_IDLE_TIME_MILLIS = "minEvictableIdleTimeMillis";
    private static final String OPENICF_MIN_IDLE = "minIdle";
    private static final String OPENICF_POOL_CONFIG_OPTION = "poolConfigOption";
    private static final String OPENICF_OPERATION_TIMEOUT = "operationTimeout";
    private static final String OPENICF_CONFIGURATION_PROPERTIES = "configurationProperties";
    public static final String OPENICF_FLAGS = "flags";
    public static final String OPENICF_OBJECT_CLASS = "nativeType";
    //public static final String PROPERTY_IS_CONTAINER = "isContainer";
    public static final String OPENICF_NATIVE_NAME = "nativeName";
    public static final String OPENICF_NATIVE_TYPE = "nativeType";

    public static final String OPENICF_REMOTE_CONNECTOR_SERVERS = "remoteConnectorServers";

    public static final String JAVA_TYPE_BIGDECIMAL = "JAVA_TYPE_BIGDECIMAL";
    public static final String JAVA_TYPE_BIGINTEGER = "JAVA_TYPE_BIGINTEGER";
    public static final String JAVA_TYPE_PRIMITIVE_BOOLEAN = "JAVA_TYPE_PRIMITIVE_BOOLEAN";
    public static final String JAVA_TYPE_BYTE_ARRAY = "JAVA_TYPE_BYTE_ARRAY";
    public static final String JAVA_TYPE_CHAR = "JAVA_TYPE_CHAR";
    public static final String JAVA_TYPE_CHARACTER = "JAVA_TYPE_CHARACTER";
    public static final String JAVA_TYPE_DATE = "JAVA_TYPE_DATE";
    public static final String JAVA_TYPE_PRIMITIVE_DOUBLE = "JAVA_TYPE_PRIMITIVE_DOUBLE";
    public static final String JAVA_TYPE_DOUBLE = "JAVA_TYPE_DOUBLE";
    public static final String JAVA_TYPE_FILE = "JAVA_TYPE_FILE";
    public static final String JAVA_TYPE_PRIMITIVE_FLOAT = "JAVA_TYPE_PRIMITIVE_FLOAT";
    public static final String JAVA_TYPE_FLOAT = "JAVA_TYPE_FLOAT";
    public static final String JAVA_TYPE_GUARDEDBYTEARRAY = "JAVA_TYPE_GUARDEDBYTEARRAY";
    public static final String JAVA_TYPE_GUARDEDSTRING = "JAVA_TYPE_GUARDEDSTRING";
    public static final String JAVA_TYPE_INT = "JAVA_TYPE_INT";
    public static final String JAVA_TYPE_PRIMITIVE_LONG = "JAVA_TYPE_PRIMITIVE_LONG";
    public static final String JAVA_TYPE_LONG = "JAVA_TYPE_LONG";
    public static final String JAVA_TYPE_NAME = "JAVA_TYPE_NAME";
    public static final String JAVA_TYPE_OBJECTCLASS = "JAVA_TYPE_OBJECTCLASS";
    public static final String JAVA_TYPE_QUALIFIEDUID = "JAVA_TYPE_QUALIFIEDUID";
    public static final String JAVA_TYPE_SCRIPT = "JAVA_TYPE_SCRIPT";
    public static final String JAVA_TYPE_UID = "JAVA_TYPE_UID";
    public static final String JAVA_TYPE_URI = "JAVA_TYPE_URI";


    private static final Map<OperationType, Class<? extends APIOperation>> operationMap = new HashMap<OperationType, Class<? extends APIOperation>>(12);
    private static final Map<String, Class> typeMap = new HashMap<String, Class>(43);
    public static final String OPENICF_CONNECTOR_REF = "connectorRef";
    public static final String OPENICF_OBJECT_TYPES = "objectTypes";
    public static final String OPENICF_OPERATION_OPTIONS = "operationOptions";

    static {
        operationMap.put(OperationType.CREATE, CreateApiOp.class);
        operationMap.put(OperationType.UPDATE, UpdateApiOp.class);
        operationMap.put(OperationType.DELETE, DeleteApiOp.class);
        operationMap.put(OperationType.TEST, TestApiOp.class);
        operationMap.put(OperationType.SCRIPT_ON_CONNECTOR, ScriptOnConnectorApiOp.class);
        operationMap.put(OperationType.SCRIPT_ON_RESOURCE, ScriptOnResourceApiOp.class);
        operationMap.put(OperationType.GET, GetApiOp.class);
        operationMap.put(OperationType.AUTHENTICATE, AuthenticationApiOp.class);
        operationMap.put(OperationType.SEARCH, SearchApiOp.class);
        operationMap.put(OperationType.VALIDATE, ValidateApiOp.class);
        operationMap.put(OperationType.SYNC, SyncApiOp.class);
        operationMap.put(OperationType.SCHEMA, SchemaApiOp.class);

        typeMap.put(Constants.TYPE_ANY, Object.class);
        //typeMap.put(Constants.TYPE_NULL, null);
        typeMap.put(Constants.TYPE_ARRAY, List.class);
        typeMap.put(Constants.TYPE_BOOLEAN, Boolean.class);
        typeMap.put(Constants.TYPE_INTEGER, Integer.class);
        typeMap.put(Constants.TYPE_NUMBER, Number.class);
        typeMap.put(Constants.TYPE_OBJECT, Map.class);
        typeMap.put(Constants.TYPE_STRING, String.class);
        typeMap.put(JAVA_TYPE_BIGDECIMAL, BigDecimal.class);
        typeMap.put(JAVA_TYPE_BIGINTEGER, BigInteger.class);
        typeMap.put(JAVA_TYPE_PRIMITIVE_BOOLEAN, boolean.class);
        typeMap.put(JAVA_TYPE_BYTE_ARRAY, byte[].class);
        typeMap.put(JAVA_TYPE_CHAR, char.class);
        typeMap.put(JAVA_TYPE_CHARACTER, Character.class);
        typeMap.put(JAVA_TYPE_DATE, Date.class);
        typeMap.put(JAVA_TYPE_PRIMITIVE_DOUBLE, double.class);
        typeMap.put(JAVA_TYPE_DOUBLE, Double.class);
        typeMap.put(JAVA_TYPE_FILE, File.class);
        typeMap.put(JAVA_TYPE_PRIMITIVE_FLOAT, float.class);
        typeMap.put(JAVA_TYPE_FLOAT, Float.class);
        typeMap.put(JAVA_TYPE_GUARDEDBYTEARRAY, GuardedByteArray.class);
        typeMap.put(JAVA_TYPE_GUARDEDSTRING, GuardedString.class);
        typeMap.put(JAVA_TYPE_INT, int.class);
        typeMap.put(JAVA_TYPE_PRIMITIVE_LONG, long.class);
        typeMap.put(JAVA_TYPE_LONG, Long.class);
        typeMap.put(JAVA_TYPE_NAME, Name.class);
        typeMap.put(JAVA_TYPE_OBJECTCLASS, ObjectClass.class);
        typeMap.put(JAVA_TYPE_QUALIFIEDUID, QualifiedUid.class);
        typeMap.put(JAVA_TYPE_SCRIPT, Script.class);
        typeMap.put(JAVA_TYPE_UID, Uid.class);
        typeMap.put(JAVA_TYPE_URI, URI.class);
    }


    //Util Methods

    /**
     * Get the Java Class for the {@code type} value in the schema.
     * <p/>
     * The Default mapping:
     * <table>
     * <tr><td>any</td><td>{@link Object}</td></tr>
     * <tr><td>JAVA_TYPE_BIGDECIMAL</td><td>{@link BigDecimal}</td></tr>
     * <tr><td>JAVA_TYPE_BIGINTEGER</td><td>{@link BigInteger}</td></tr>
     * <tr><td>JAVA_TYPE_PRIMITIVE_BOOLEAN</td><td>{@link boolean}</td></tr>
     * <tr><td>boolean</td><td>{@link Boolean}</td></tr>
     * <tr><td>JAVA_TYPE_BYTE_ARRAY</td><td>{@link byte[]}</td></tr>
     * <tr><td>JAVA_TYPE_CHAR</td><td>{@link char}</td></tr>
     * <tr><td>JAVA_TYPE_CHARACTER</td><td>{@link Character}</td></tr>
     * <tr><td>JAVA_TYPE_DATE</td><td>{@link Date}</td></tr>
     * <tr><td>JAVA_TYPE_PRIMITIVE_DOUBLE</td><td>{@link double}</td></tr>
     * <tr><td>JAVA_TYPE_DOUBLE</td><td>{@link Double}</td></tr>
     * <tr><td>JAVA_TYPE_FILE</td><td>{@link File}</td></tr>
     * <tr><td>JAVA_TYPE_PRIMITIVE_FLOAT</td><td>{@link float}</td></tr>
     * <tr><td>JAVA_TYPE_FLOAT</td><td>{@link Float}</td></tr>
     * <tr><td>JAVA_TYPE_GUARDEDBYTEARRAY</td><td>{@link GuardedByteArray}</td></tr>
     * <tr><td>JAVA_TYPE_GUARDEDSTRING</td><td>{@link GuardedString}</td></tr>
     * <tr><td>JAVA_TYPE_INT</td><td>{@link int}</td></tr>
     * <tr><td>integer</td><td>{@link Integer}</td></tr>
     * <tr><td>array</td><td>{@link List}</td></tr>
     * <tr><td>JAVA_TYPE_PRIMITIVE_LONG</td><td>{@link long}</td></tr>
     * <tr><td>JAVA_TYPE_LONG</td><td>{@link Long}</td></tr>
     * <tr><td>JAVA_TYPE_NAME</td><td>{@link Name}</td></tr>
     * <tr><td>null</td><td>{@code null}</td></tr>
     * <tr><td>number</td><td>{@link Number}</td></tr>
     * <tr><td>object</td><td>{@link Map}</td></tr>
     * <tr><td>JAVA_TYPE_OBJECTCLASS</td><td>{@link ObjectClass}</td></tr>
     * <tr><td>JAVA_TYPE_QUALIFIEDUID</td><td>{@link QualifiedUid}</td></tr>
     * <tr><td>JAVA_TYPE_SCRIPT</td><td>{@link Script}</td></tr>
     * <tr><td>string</td><td>{@link String}</td></tr>
     * <tr><td>JAVA_TYPE_UID</td><td>{@link Uid }</td></tr>
     * <tr><td>JAVA_TYPE_URI</td><td>{@link URI}</td></tr>
     * </table>
     *
     * @return class if it has mapped to a type or null if not.
     */
    public static Class findClassForName(String name) {
        return typeMap.get(name);
    }

    /**
     * @param clazz
     * @return
     */
    public static String findNameForClass(Class clazz) {
        for (Map.Entry<String, Class> entry : typeMap.entrySet()) {
            if (entry.getValue().equals(clazz)) {
                return entry.getKey();
            }
        }
        return null;
    }


    public static String findJSONTypeForClass(Class clazz) {
        if ((Integer.class.isAssignableFrom(clazz)) || (int.class == clazz)) {
            return Constants.TYPE_INTEGER;
        } else if ((Number.class.isAssignableFrom(clazz)) || (double.class == clazz) || (float.class == clazz) || (long.class == clazz)) {
            return Constants.TYPE_NUMBER;
        } else if ((Boolean.class.isAssignableFrom(clazz)) || (boolean.class == clazz)) {
            return Constants.TYPE_BOOLEAN;
        } else if (List.class.isAssignableFrom(clazz)) {
            return Constants.TYPE_ARRAY;
        } else if (Map.class.isAssignableFrom(clazz)) {
            return Constants.TYPE_OBJECT;
        } else {
            return Constants.TYPE_STRING;
        }
    }


    /**
     * Convert the {@link ObjectPoolConfiguration} to simple Map.
     * <p/>
     *
     * @param info
     * @return
     */
    public static Map<String, Object> getObjectPoolConfiguration(ObjectPoolConfiguration info) {
        Map<String, Object> poolConfigOption = new LinkedHashMap<String, Object>(5);
        poolConfigOption.put(OPENICF_MAX_OBJECTS, info.getMaxObjects());
        poolConfigOption.put(OPENICF_MAX_IDLE, info.getMaxIdle());
        poolConfigOption.put(OPENICF_MAX_WAIT, info.getMaxWait());
        poolConfigOption.put(OPENICF_MIN_EVICTABLE_IDLE_TIME_MILLIS, info.getMinEvictableIdleTimeMillis());
        poolConfigOption.put(OPENICF_MIN_IDLE, info.getMinIdle());
        return poolConfigOption;
    }

    /**
     * @param source
     * @return
     * @throws UnsupportedOperationException when the property value can not be converted to String.
     */
    public static void configureObjectPoolConfiguration(JsonNode source, ObjectPoolConfiguration target) throws JsonNodeException {
        Map<String, Object> poolConfiguration = source.asMap();
        if (null != poolConfiguration.get(OPENICF_MAX_OBJECTS)) {
            target.setMaxObjects(coercedTypeCasting(poolConfiguration.get(OPENICF_MAX_OBJECTS), int.class));
        }
        if (null != poolConfiguration.get(OPENICF_MAX_IDLE)) {
            target.setMaxIdle(coercedTypeCasting(poolConfiguration.get(OPENICF_MAX_IDLE), int.class));
        }
        if (null != poolConfiguration.get(OPENICF_MAX_WAIT)) {
            target.setMaxWait(coercedTypeCasting(poolConfiguration.get(OPENICF_MAX_WAIT), long.class));
        }
        if (null != poolConfiguration.get(OPENICF_MIN_EVICTABLE_IDLE_TIME_MILLIS)) {
            target.setMinEvictableIdleTimeMillis(coercedTypeCasting(poolConfiguration.get(OPENICF_MIN_EVICTABLE_IDLE_TIME_MILLIS), long.class));
        }
        if (null != poolConfiguration.get(OPENICF_MIN_IDLE)) {
            target.setMinIdle(coercedTypeCasting(poolConfiguration.get(OPENICF_MIN_IDLE), int.class));
        }
    }


    public static Map<String, Object> getTimeout(APIConfiguration configuration) {
        Map<String, Object> result = new LinkedHashMap<String, Object>(12);
        for (Map.Entry<OperationType, Class<? extends APIOperation>> e : operationMap.entrySet()) {
            result.put(e.getKey().name(), configuration.getTimeout(e.getValue()));
        }
        return result;
    }

    public static void configureTimeout(JsonNode source, APIConfiguration target) throws JsonNodeException {
        for (Map.Entry<OperationType, Class<? extends APIOperation>> e : operationMap.entrySet()) {
            JsonNode value = source.get(e.getKey().name());
            try {
                target.setTimeout(e.getValue(), coercedTypeCasting(value.asNumber(), int.class));
            } catch (IllegalArgumentException e1) {
                TRACE.error("Type casting exception of {} from {} to int", new Object[]{value.getValue(), value.getValue().getClass().getCanonicalName()}, e);
            }
        }
    }


    /**
     * @param source
     * @param target
     * @throws IllegalArgumentException
     */
    public static void setConfigurationProperties(ConfigurationProperties source, Map<String, Object> target) {
        for (String propertyName : source.getPropertyNames()) {
            ConfigurationProperty configurationProperty = source.getProperty(propertyName);
            target.put(propertyName, convertFromConfigurationProperty(configurationProperty));
        }
    }


    private static Object convertFromConfigurationProperty(ConfigurationProperty configurationProperty) {
        Object sourceValue = configurationProperty.getValue();
        if (sourceValue == null) {
            return null;
        }
        boolean isArray = sourceValue.getClass().isArray();
        Class sourceType = isArray ? sourceValue.getClass().getComponentType() : sourceValue.getClass();
        Object result = null;
        if (isArray) {
            if (sourceType == byte.class) {
                result = new String((byte[]) sourceValue);
            } else if (sourceType == char.class) {
                result = new String((char[]) sourceValue);
            } else if (sourceType == Character.class) {
                Character[] characterArray = (Character[]) sourceValue;
                char[] charArray = new char[characterArray.length];
                for (int i = 0; i < characterArray.length; i++) {
                    charArray[i] = characterArray[i];
                }
                result = new String(charArray);
            } else {
                int length = Array.getLength(sourceValue);
                List values = new ArrayList(length);
                for (int i = 0; i < length; i++) {
                    Object item = Array.get(sourceValue, i);
                    Object newValue = coercedTypeCasting(item, Object.class);
                    values.add(newValue);
                }
                result = values;
            }
        } else {
            result = coercedTypeCasting(sourceValue, Object.class);
        }
        return result;
    }


    public static void configureConfigurationProperties(JsonNode source, ConfigurationProperties target) throws JsonNodeException {
        source.required();
        if (null != target) {
            List<String> configPropNames = target.getPropertyNames();
            for (Map.Entry<String, Object> e : source.asMap().entrySet()) {
                if (!configPropNames.contains(e.getKey())) {
                    /*
                    * The connector's Configuration does not define this property.
                    */
                    continue;
                }
                ConfigurationProperty property = target.getProperty(e.getKey());
                Class targetType = property.getType();
                Object propertyValue = null;
                if (targetType.isArray()) {
                    Class targetBaseType = targetType.getComponentType();
                    if (targetBaseType == byte.class || targetBaseType == char.class) {
                        propertyValue = coercedTypeCasting(e.getValue(), targetType);
                    } else if (e.getValue() instanceof List) {
                        List v = (List) e.getValue();
                        propertyValue = Array.newInstance(targetBaseType, v.size());
                        for (int i = 0; i < v.size(); i++) {
                            Array.set(propertyValue, i, coercedTypeCasting(v.get(i), targetBaseType));
                        }
                    } else {
                        propertyValue = Array.newInstance(targetBaseType, 1);
                        Array.set(propertyValue, 0, coercedTypeCasting(e.getValue(), targetBaseType));
                    }
                } else {
                    propertyValue = coercedTypeCasting(e.getValue(), targetType);
                }
                property.setValue(propertyValue);
            }
        }
    }


    public static void configureDefaultAPIConfiguration(JsonNode source, APIConfiguration target) throws JsonNodeException {
        JsonNode poolConfigOption = source.get(OPENICF_POOL_CONFIG_OPTION);
        if (poolConfigOption.isMap()) {
            configureObjectPoolConfiguration(poolConfigOption, target.getConnectorPoolConfiguration());
        }
        JsonNode operationTimeout = source.get(OPENICF_OPERATION_TIMEOUT);
        if (operationTimeout.isMap()) {
            configureTimeout(operationTimeout, target);
        }
        JsonNode configurationProperties = source.get(OPENICF_CONFIGURATION_PROPERTIES);
        configureConfigurationProperties(configurationProperties, target.getConfigurationProperties());
    }

    public static void createSystemConfigurationFromAPIConfiguration(APIConfiguration source, Map<String, Object> target) {
        target.put(OPENICF_POOL_CONFIG_OPTION, getObjectPoolConfiguration(source.getConnectorPoolConfiguration()));
        target.put(OPENICF_OPERATION_TIMEOUT, getTimeout(source));
        Map<String, Object> configurationProperties = new LinkedHashMap<String, Object>();
        target.put(OPENICF_CONFIGURATION_PROPERTIES, configurationProperties);
        setConfigurationProperties(source.getConfigurationProperties(), configurationProperties);
    }


    /**
     * Convert the {@link ConnectorKey} into a Map.
     * <p/>
     * The connector key is saved in a JSON object and this method converts it to simple Map.
     *
     * @param info
     * @return
     */
    public static Map<String, Object> getConnectorKey(ConnectorKey info) {
        Map<String, Object> result = new HashMap<String, Object>(6);
        result.put(OPENICF_BUNDLENAME, info.getBundleName());
        result.put(OPENICF_BUNDLEVERSION, info.getBundleVersion());
        result.put(OPENICF_CONNECTOR_NAME, info.getConnectorName());
        return result;
    }

    /**
     * Create a new {@link ConnectorKey} instance form the {@code configuration} object.
     * <p/>
     * The Configuration object MUST contain the three required String properties.
     * <ul>
     * <li>bundleName</li>
     * <li>bundleVersion</li>
     * <li>connectorName</li>
     * </ul>
     *
     * @param configuration
     * @return new instance of {@link ConnectorKey}
     * @throws IllegalArgumentException when one of the three required parameter is null.
     * @throws IOException              when the property value can not be converted to String.
     */
    public static ConnectorKey getConnectorKey(JsonNode configuration) throws JsonNodeException {
        String bundleName = configuration.get(OPENICF_BUNDLENAME).asString();
        String bundleVersion = configuration.get(OPENICF_BUNDLEVERSION).asString();
        String connectorName = configuration.get(OPENICF_CONNECTOR_NAME).asString();
        return new ConnectorKey(bundleName, bundleVersion, connectorName);
    }

    /**
     * @param info
     * @return
     * @throws IllegalArgumentException if the configuration can not be read from {@code info}
     */
    public static RemoteFrameworkConnectionInfo getRemoteFrameworkConnectionInfo(Map<String, Object> info) {
        String _host = ConnectorUtil.coercedTypeCasting(info.get(OPENICF_HOST), String.class);
        int _port = ConnectorUtil.coercedTypeCasting(info.get(OPENICF_PORT), int.class);
        GuardedString _key = ConnectorUtil.coercedTypeCasting(info.get(OPENICF_KEY), GuardedString.class);
        boolean _useSSL = ConnectorUtil.coercedTypeCasting(info.get(OPENICF_USE_SSL), boolean.class);
        //List<TrustManager> _trustManagers;
        int _timeout = ConnectorUtil.coercedTypeCasting(info.get(OPENICF_TIMEOUT), int.class);
        return new RemoteFrameworkConnectionInfo(_host, _port, _key, _useSSL, null, _timeout);

    }


    public static Map<String, Object> getRemoteFrameworkConnectionMap(RemoteFrameworkConnectionInfo info) {
        Map<String, Object> result = new HashMap<String, Object>(6);
        result.put(OPENICF_HOST, info.getHost());
        result.put(OPENICF_PORT, info.getPort());
        result.put(OPENICF_KEY, info.getKey().toString());
        result.put(OPENICF_USE_SSL, info.getUseSSL());
        result.put(OPENICF_TRUST_MANAGERS, info.getTrustManagers());
        result.put(OPENICF_TIMEOUT, info.getTimeout());
        return result;
    }


    public static ConnectorReference getConnectorReference(JsonNode configuration) throws JsonNodeException {
        JsonNode connectorRef = configuration.get(OPENICF_CONNECTOR_REF);
        connectorRef.expect(Map.class);
        ConnectorKey key = getConnectorKey(connectorRef);
        String connectorHost = connectorRef.get(OPENICF_CONNECTOR_HOST_REF).defaultTo(ConnectorReference.SINGLE_LOCAL_CONNECTOR_MANAGER).asString();
        return new ConnectorReference(key, connectorHost);
    }

    public static void setConnectorReference(ConnectorReference source, Map<String, Object> target) {
        Map<String, Object> connectorReference = getConnectorKey(source.getConnectorKey());
        connectorReference.put(OPENICF_CONNECTOR_HOST_REF, source.getConnectorHost());
        target.put(OPENICF_CONNECTOR_REF, connectorReference);
    }


    public static Map<String, ObjectClassInfoHelper> getObjectTypes(JsonNode configuration) throws JsonNodeException, SchemaException {
        JsonNode objectTypes = configuration.get(OPENICF_OBJECT_TYPES);
        Map<String, ObjectClassInfoHelper> result = new HashMap<String, ObjectClassInfoHelper>(objectTypes.expect(Map.class).asMap().size());
        for (Map.Entry<String, Object> entry : objectTypes.asMap().entrySet()) {
            result.put(entry.getKey(), new ObjectClassInfoHelper((Map<String, Object>) entry.getValue()));
        }
        return result;
    }


    public static void setObjectAndOperationConfiguration(Schema source, Map<String, Object> target) {

        Map<String, Object> objectTypes = new LinkedHashMap<String, Object>(source.getObjectClassInfo().size());
        for (ObjectClassInfo objectClassInfo : source.getObjectClassInfo()) {
            objectTypes.put(objectClassInfo.getType(), getObjectClassInfoMap(objectClassInfo));
        }
        target.put(OPENICF_OBJECT_TYPES, objectTypes);


        Map<String, Object> optionsByOperation = new LinkedHashMap<String, Object>(12);
        for (Map.Entry<OperationType, Class<? extends APIOperation>> e : operationMap.entrySet()) {

        }
        target.put(OPENICF_OPERATION_OPTIONS, optionsByOperation);

    }


    public static Map<String, ConnectorObjectOptions> getOperationOptionConfiguration(JsonNode configuration) throws JsonNodeException, SchemaException {
        JsonNode operationOptions = configuration.get(OPENICF_OPERATION_OPTIONS).expect(Map.class);
        if (!operationOptions.isNull()) {
            Map<String, ConnectorObjectOptions> operationOptionConfigurationMap = new HashMap<String, ConnectorObjectOptions>();
            Map<String, Map<Class<? extends APIOperation>, OperationOptionInfoHelper>> objectOperationOptionMap = new HashMap<String, Map<Class<? extends APIOperation>, OperationOptionInfoHelper>>();
            Map<Class<? extends APIOperation>, OperationOptionInfoHelper> globalOperationOptionMap = new HashMap<Class<? extends APIOperation>, OperationOptionInfoHelper>();

            for (Map.Entry<OperationType, Class<? extends APIOperation>> entry : operationMap.entrySet()) {
                JsonNode operation = operationOptions.get(entry.getValue().getSimpleName()).expect(Map.class);
                if (operation.isNull()) {
                    continue;
                }

                JsonNode operationOptionInfos = operation.get("operationOptionInfos").expect(Map.class);
                OperationOptionInfoHelper globalOperationOptionInfoHelper = null;
                if (operationOptionInfos.size() > 0) {
                    globalOperationOptionInfoHelper = new OperationOptionInfoHelper(operationOptionInfos);
                    globalOperationOptionMap.put(entry.getValue(), globalOperationOptionInfoHelper);
                }

                JsonNode objectFeatures = operation.get("objectFeatures").expect(Map.class);
                if (objectFeatures.size() > 0) {
                    for (String objectOperationName : objectFeatures.asMap().keySet()) {
                        JsonNode objectOperationConfig = objectFeatures.get(objectOperationName).expect(Map.class);
                        OperationOptionInfoHelper operationOptionInfoHelper;
                        if (globalOperationOptionInfoHelper != null) {
                            operationOptionInfoHelper = new OperationOptionInfoHelper(objectOperationConfig, globalOperationOptionInfoHelper);
                        } else {
                            operationOptionInfoHelper = new OperationOptionInfoHelper(objectOperationConfig);
                        }

                        if (objectOperationOptionMap.containsKey(objectOperationName)) {
                            objectOperationOptionMap.get(objectOperationName).put(entry.getValue(), operationOptionInfoHelper);
                        } else {
                            Map<Class<? extends APIOperation>, OperationOptionInfoHelper> operationMap = new HashMap<Class<? extends APIOperation>, OperationOptionInfoHelper>();
                            operationMap.put(entry.getValue(), operationOptionInfoHelper);
                            objectOperationOptionMap.put(objectOperationName, operationMap);
                        }
                    }
                }
            }
            for (Map.Entry<String, Map<Class<? extends APIOperation>, OperationOptionInfoHelper>> objectOperation : objectOperationOptionMap.entrySet()) {
                Map<Class<? extends APIOperation>, OperationOptionInfoHelper> objectOperationsMap = objectOperation.getValue();

                for (Map.Entry<Class<? extends APIOperation>, OperationOptionInfoHelper> globalOperation : globalOperationOptionMap.entrySet()) {
                    if (!objectOperationsMap.containsKey(globalOperation.getKey())) {
                        objectOperationsMap.put(globalOperation.getKey(), globalOperation.getValue());
                    }
                }

                ConnectorObjectOptions connectorObjectOptions = new ConnectorObjectOptions(new OperationOptionInfoHelper(new JsonNode(new HashMap())), objectOperationsMap);
                operationOptionConfigurationMap.put(objectOperation.getKey(), connectorObjectOptions);
            }
            return operationOptionConfigurationMap;
        }
        return Collections.emptyMap();
    }


    public static Map<String, Object> getObjectClassInfoMap(ObjectClassInfo info) {
        Map<String, Object> schema = new LinkedHashMap<String, Object>();
        schema.put(SCHEMA, JSON_SCHEMA_DRAFT03);
        schema.put(ID, info.getType());
        schema.put(TYPE, TYPE_OBJECT);
        schema.put(OPENICF_OBJECT_CLASS, info.getType());
        //schema.put(ConnectorUtil.PROPERTY_IS_CONTAINER, info.isContainer());
        Map<String, Object> properties = new LinkedHashMap<String, Object>(info.getAttributeInfo().size());
        schema.put(PROPERTIES, properties);
        for (AttributeInfo attributeInfo : info.getAttributeInfo()) {
            properties.put(attributeInfo.getName(), getAttributeInfoMap(attributeInfo));
        }
        return schema;
    }

    /**
     * Build a {@link Map} from the given {@link org.identityconnectors.framework.common.objects.AttributeInfo}
     * <p/>
     * The result will look like this:
     * {
     * "type"    : "number",
     * "mapName" : "lastLogin",
     * "mapType" : "JAVA_TYPE_DOUBLE",
     * "flags" : [
     * "REQUIRED",
     * "MULTIVALUED",
     * "NOT_CREATABLE",
     * "NOT_UPDATEABLE",
     * "NOT_READABLE",
     * "NOT_RETURNED_BY_DEFAULT"
     * ]
     * }
     *
     * @param info
     */
    public static Map<String, Object> getAttributeInfoMap(AttributeInfo info) {
        Map<String, Object> schema = new LinkedHashMap<String, Object>();
        if (info.isMultiValued()) {
            schema.put(Constants.TYPE, Constants.TYPE_ARRAY);
            Map<String, Object> itemSchema = new LinkedHashMap<String, Object>(2);
            itemSchema.put(Constants.TYPE, findJSONTypeForClass(info.getType()));
            itemSchema.put(ConnectorUtil.OPENICF_NATIVE_TYPE, findNameForClass(info.getType()));
            schema.put(Constants.ITEMS, itemSchema);
        } else {
            schema.put(Constants.TYPE, findJSONTypeForClass(info.getType()));
        }
        if (info.isRequired()) {
            schema.put(Constants.REQUIRED, true);
        }
        schema.put(ConnectorUtil.OPENICF_NATIVE_NAME, info.getName());
        schema.put(ConnectorUtil.OPENICF_NATIVE_TYPE, findNameForClass(info.getType()));
        if (!info.getFlags().isEmpty()) {
            List<String> flags = null;
            for (AttributeInfo.Flags flag : info.getFlags()) {
                if (AttributeInfo.Flags.MULTIVALUED.equals(flag) || AttributeInfo.Flags.REQUIRED.equals(flag)) {
                    continue;
                }
                if (null == flags) {
                    flags = new ArrayList<String>(4);
                }
                flags.add(flag.name());
            }
            if (null != flags) {
                schema.put(ConnectorUtil.OPENICF_FLAGS, Collections.unmodifiableList(flags));
            }
        }
        return schema;
    }


    public static String normalizeConnectorName(String connectorName) {
        String name = null;
        if (connectorName != null) {
            int lastDot = connectorName.lastIndexOf(46);
            if (lastDot != -1) {
                name = connectorName.substring(lastDot + 1);
            }
        }

        return name;
    }

    /**
     * Coerce the {@code source} object to an object of {@code clazz} type.
     * <p/>
     *
     * @param <T>
     * @param source
     * @param clazz
     * @return
     * @throws NumberFormatException
     * @throws URISyntaxException
     * @throws UnsupportedOperationException
     */
    @SuppressWarnings("unchecked")
    public static <T> T coercedTypeCasting(Object source, Class<T> clazz) throws IllegalArgumentException {
        if (null == clazz) {
            throw new IllegalArgumentException("Target Class can not be null");
        }

        Class<T> targetClazz = clazz;
        Class sourceClass = (source == null ? null : source.getClass());
        boolean coerced = false;
        T result = null;
        try {
            if (source == null) {
                return null;
            }

            //Default JSON Type conversion
            if (targetClazz.equals(Object.class)) {
                if ((Number.class.isAssignableFrom(sourceClass)) || (int.class == clazz) || (double.class == clazz) || (float.class == clazz) || (long.class == clazz)) {
                    return (T) source;
                } else if ((Boolean.class.isAssignableFrom(sourceClass)) || (boolean.class == clazz)) {
                    return (T) source;
                } else if (String.class.isAssignableFrom(sourceClass)) {
                    return (T) source;
                } else if (Map.class.isAssignableFrom(sourceClass)) {
                    return (T) source;
                } else if (List.class.isAssignableFrom(sourceClass)) {
                    return (T) source;
                } else if (sourceClass == QualifiedUid.class) {
                    //@TODO: Not null safe!!!
                    Map<String, Object> v = new HashMap<String, Object>(2);
                    v.put("_id", ((QualifiedUid) source).getUid().getUidValue());
                    v.put("_type", ((QualifiedUid) source).getObjectClass().getObjectClassValue());
                    return (T) v;
                } else if (sourceClass == Script.class) {
                    Map<String, Object> v = new HashMap<String, Object>(2);
                    v.put("scriptLanguage", ((Script) source).getScriptLanguage());
                    v.put("scriptText", ((Script) source).getScriptText());
                    return (T) v;
                } else {
                    targetClazz = (Class<T>) String.class;
                }
            }

            if (targetClazz.isAssignableFrom(sourceClass)) {
                return (T) source;
            } else if (targetClazz == sourceClass) {
                return (T) source;
            } else if (targetClazz.equals(java.math.BigDecimal.class)) {
                if (Double.class.isAssignableFrom(sourceClass) || sourceClass == double.class) {
                    result = (T) BigDecimal.valueOf((Double) source);
                    coerced = true;
                } else if (Integer.class.isAssignableFrom(sourceClass) || sourceClass == int.class) {
                    result = (T) BigDecimal.valueOf((Integer) source);
                    coerced = true;
                } else if (Long.class.isAssignableFrom(sourceClass) || sourceClass == long.class) {
                    result = (T) BigDecimal.valueOf((Long) source);
                    coerced = true;
                } else if (sourceClass == String.class) {
                    java.math.BigDecimal v = new java.math.BigDecimal((String) source);
                    result = targetClazz.cast(v);
                    coerced = true;
                }
            } else if (targetClazz.equals(java.math.BigInteger.class)) {
                if (Long.class.isAssignableFrom(sourceClass) || sourceClass == long.class) {
                    result = (T) BigInteger.valueOf((Long) source);
                    coerced = true;
                } else if (sourceClass == String.class) {
                    java.math.BigInteger v = new java.math.BigInteger((String) source);
                    result = targetClazz.cast(v);
                    coerced = true;
                } else {
                    result = (T) BigInteger.valueOf(coercedTypeCasting(source, Long.class));
                    coerced = true;
                }
            } else if (targetClazz.equals(boolean.class) || targetClazz.equals(Boolean.class)) {
                if (sourceClass == Boolean.class) {
                    result = (T) source;
                    coerced = true;
                } else if (sourceClass == Integer.class) {
                    int val = ((Integer) source).intValue();
                    if (val == 0) {
                        result = targetClazz.cast(Boolean.FALSE);
                        coerced = true;
                    } else if (val == 1) {
                        result = targetClazz.cast(Boolean.TRUE);
                        coerced = true;
                    }
                } else if (sourceClass == String.class) {
                    String s = (String) source;
                    if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false")) {
                        result = targetClazz.cast(Boolean.valueOf((String) source));
                        coerced = true;
                    }
                }
            } else if (targetClazz.equals(byte[].class)) {
                if (sourceClass == String.class) {
                    result = targetClazz.cast(((String) source).getBytes());
                    coerced = true;
                } else if (sourceClass == String.class) {
                    result = targetClazz.cast(Base64.decode((String) source));
                    coerced = true;
                } else if (sourceClass == GuardedByteArray.class) {
                    GuardedByteArray gba = (GuardedByteArray) source;
                    byte[] byteArray = decrypt(gba);
                    result = targetClazz.cast(byteArray);
                    coerced = true;
                }
            } else if ((targetClazz.equals(Character.class)) || (targetClazz.equals(char.class))) {
                if (sourceClass == String.class) {
                    Character v = ((String) source).charAt(0);
                    result = (T) v;
                    coerced = true;
                }
            } else if (targetClazz.equals(Character[].class)) {
                if (sourceClass == String.class) {
                    char[] charArray = ((String) source).toCharArray();
                    Character[] characterArray = new Character[charArray.length];
                    for (int i = 0; i < charArray.length; i++) {
                        characterArray[i] = new Character(charArray[i]);
                    }
                    result = targetClazz.cast(characterArray);
                    coerced = true;
                }
            } else if (targetClazz.equals(char[].class)) {
                if (sourceClass == String.class) {
                    char[] charArray = ((String) source).toCharArray();
                    result = targetClazz.cast(charArray);
                    coerced = true;
                }
            } else if (targetClazz.equals(Date.class)) {
                if (sourceClass == String.class) {
                    //TODO AttributeUtil.getDateValue()
                }
            } else if (targetClazz.equals(double.class)) {
                if (sourceClass == Double.class) {
                    result = (T) source;
                    coerced = true;
                } else if (sourceClass == int.class) {
                    result = (T) Double.valueOf((Integer.valueOf((Integer) source).doubleValue()));
                    coerced = true;
                } else if (sourceClass == Integer.class) {
                    result = (T) Double.valueOf(((Integer) source).doubleValue());
                    coerced = true;
                } else if (sourceClass == String.class) {
                    result = targetClazz.cast(Double.valueOf((String) source));
                    coerced = true;
                }
            } else if (targetClazz.equals(Double.class)) {
                if (sourceClass == double.class) {
                    result = (T) source;
                    coerced = true;
                } else if (sourceClass == int.class) {
                    result = (T) Double.valueOf((Integer.valueOf((Integer) source).doubleValue()));
                    coerced = true;
                } else if (sourceClass == Integer.class) {
                    result = (T) Double.valueOf(((Integer) source).doubleValue());
                    coerced = true;
                } else if (sourceClass == String.class) {
                    result = targetClazz.cast(Double.valueOf((String) source));
                    coerced = true;
                }
            } else if (targetClazz.equals(java.io.File.class)) {
                if (sourceClass == String.class) {
                    File file = new File((String) source);
                    result = targetClazz.cast(file);
                    coerced = true;
                }
            } else if (targetClazz.equals(float.class) || targetClazz.equals(Float.class)) {
                if (sourceClass == Float.class || sourceClass == float.class) {
                    result = (T) source;
                    coerced = true;
                } else if (sourceClass == Double.class || sourceClass == double.class) {
                    result = (T) new Float((Double) source);
                    coerced = true;
                } else if (sourceClass == int.class) {
                    result = (T) Float.valueOf((Integer.valueOf((Integer) source).floatValue()));
                    coerced = true;
                } else if (sourceClass == Integer.class) {
                    result = (T) Float.valueOf(((Integer) source).floatValue());
                    coerced = true;
                } else if (sourceClass == String.class) {
                    result = targetClazz.cast(Float.valueOf((String) source));
                    coerced = true;
                }
            } else if (targetClazz.equals(GuardedByteArray.class)) {
                if (sourceClass == String.class) {
                    byte[] byteArray = ((String) source).getBytes();
                    GuardedByteArray v = new GuardedByteArray(byteArray);
                    result = targetClazz.cast(v);
                    coerced = true;
                }
            } else if (targetClazz.equals(GuardedString.class)) {
                if (sourceClass == String.class) {
                    char[] charArray = ((String) source).toCharArray();
                    GuardedString v = new GuardedString(charArray);
                    result = targetClazz.cast(v);
                    coerced = true;
                }
            } else if (targetClazz.equals(int.class) || targetClazz.equals(Integer.class)) {
                if (sourceClass == Integer.class || sourceClass == int.class) {
                    result = (T) source;
                    coerced = true;
                } else if (sourceClass == String.class) {
                    result = (T) Integer.valueOf((String) source);
                    coerced = true;
                } else if (sourceClass == Float.class) {
                    result = targetClazz.cast(new Integer(((Float) source).intValue()));
                    coerced = true;
                } else if (sourceClass == Long.class) {
                    Long l = (Long) source;
                    if (l.longValue() <= Integer.MAX_VALUE) {
                        result = targetClazz.cast(new Integer(l.intValue()));
                        coerced = true;
                    }
                } else if (sourceClass == Boolean.class) {
                    boolean val = ((Boolean) source).booleanValue();
                    if (val) {
                        result = targetClazz.cast(new Integer(1));
                    } else {
                        result = targetClazz.cast(new Integer(0));
                    }
                    coerced = true;
                }
            } else if (targetClazz.equals(long.class) || targetClazz.equals(Long.class)) {
                if (sourceClass == int.class) {
                    result = (T) Long.valueOf((Integer.valueOf((Integer) source).longValue()));
                    coerced = true;
                } else if (sourceClass == Integer.class) {
                    result = (T) Long.valueOf(((Integer) source).longValue());
                    coerced = true;
                } else if (sourceClass == Long.class || sourceClass == long.class) {
                    result = (T) source;
                    coerced = true;
                } else if (sourceClass == String.class) {
                    result = targetClazz.cast(Long.valueOf((String) source));
                    coerced = true;
                }
            } else if (targetClazz.equals(Name.class)) {
                if (sourceClass == String.class) {
                    result = targetClazz.cast(new Name((String) source));
                    coerced = true;
                }
            } else if (targetClazz.equals(ObjectClass.class)) {
                if (sourceClass == String.class) {
                    ScriptBuilder sb = new ScriptBuilder();
                    sb.setScriptLanguage("");
                    sb.setScriptText("");
                    result = targetClazz.cast(sb.build());
                    coerced = true;
                }
            } else if (targetClazz.equals(QualifiedUid.class)) {
                if (sourceClass == String.class) {
                    ScriptBuilder sb = new ScriptBuilder();
                    sb.setScriptLanguage("");
                    sb.setScriptText("");
                    result = targetClazz.cast(sb.build());
                    coerced = true;
                }
            } else if (targetClazz.equals(Script.class)) {
                if (sourceClass == String.class) {
                    ScriptBuilder sb = new ScriptBuilder();
                    sb.setScriptLanguage("");
                    sb.setScriptText("");
                    result = targetClazz.cast(sb.build());
                    coerced = true;
                } else if (Map.class.isAssignableFrom(sourceClass)) {
                    ScriptBuilder sb = new ScriptBuilder();
                    sb.setScriptLanguage((String) ((Map) source).get("scriptLanguage"));
                    sb.setScriptText((String) ((Map) source).get("scriptText"));
                    result = targetClazz.cast(sb.build());
                    coerced = true;
                }
            } else if (targetClazz.equals(String.class)) {
                if (sourceClass == byte[].class) {
                    result = (T) new String((byte[]) source);
                    coerced = true;
                } else if (sourceClass == char.class) {
                    result = (T) new String((char[]) source);
                    coerced = true;
                } else if (sourceClass == Character[].class) {
                    Character[] characterArray = (Character[]) source;
                    char[] charArray = new char[characterArray.length];
                    for (int i = 0; i < characterArray.length; i++) {
                        charArray[i] = characterArray[i];
                    }
                    result = (T) new String(charArray);
                    coerced = true;
                } else if (sourceClass == Double.class) {
                    String s = ((Double) source).toString();
                    result = targetClazz.cast(s);
                    coerced = true;
                } else if (sourceClass == Float.class) {
                    String s = ((Float) source).toString();
                    result = targetClazz.cast(s);
                    coerced = true;
                } else if (sourceClass == Boolean.class) {
                    Boolean b = (Boolean) source;
                    result = targetClazz.cast(Boolean.toString(b.booleanValue()));
                    coerced = true;
                } else if (sourceClass == Long.class) {
                    String s = ((Long) source).toString();
                    result = targetClazz.cast(s);
                    coerced = true;
                } else if (sourceClass == Integer.class) {
                    String s = ((Integer) source).toString();
                    result = targetClazz.cast(s);
                    coerced = true;
                } else if (sourceClass == java.math.BigInteger.class) {
                    String s = ((java.math.BigInteger) source).toString();
                    result = targetClazz.cast(s);
                    coerced = true;
                } else if (sourceClass == java.math.BigDecimal.class) {
                    String s = ((java.math.BigDecimal) source).toString();
                    result = targetClazz.cast(s);
                    coerced = true;
                } else if (sourceClass == java.io.File.class) {
                    File file = (File) source;
                    String s = file.getPath();
                    result = targetClazz.cast(s);
                    coerced = true;
                } else if (sourceClass == java.net.URI.class) {
                    java.net.URI uri = (java.net.URI) source;
                    String s = uri.toString();
                    result = targetClazz.cast(s);
                    coerced = true;
                } else if (sourceClass == Character.class) {
                    Character c = (Character) source;
                    char[] charArray = new char[1];
                    charArray[0] = c.charValue();
                    String s = new String(charArray);
                    result = targetClazz.cast(s);
                    coerced = true;
                } else if (sourceClass == GuardedString.class) {
                    String s = decrypt((GuardedString) source);
                    result = targetClazz.cast(s);
                    coerced = true;
                } else if (sourceClass == GuardedByteArray.class) {
                    byte[] s = decrypt((GuardedByteArray) source);
                    result = targetClazz.cast(new String(s));
                    coerced = true;
                }
            } else if (targetClazz.equals(Uid.class)) {
                if (sourceClass == String.class) {
                    Uid v = new Uid((String) source);
                    result = targetClazz.cast(v);
                    coerced = true;
                }
            } else if (targetClazz.equals(java.net.URI.class)) {
                if (sourceClass == String.class) {
                    try {
                        java.net.URI v = new java.net.URI((String) source);
                        result = targetClazz.cast(v);
                        coerced = true;
                    } catch (URISyntaxException e) {
                        throw new IOException(e);
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(source.getClass().getCanonicalName() + " to " + targetClazz.getCanonicalName(), e);
        }

        if (coerced == false) {
            throw new IllegalArgumentException(source.getClass().getCanonicalName() + " to " + targetClazz.getCanonicalName());
        }
        return result;
    }

    private static byte[] decrypt(GuardedByteArray guardedByteArray) {
        final ByteArrayOutputStream clearStream = new ByteArrayOutputStream();
        GuardedByteArray.Accessor accessor = new GuardedByteArray.Accessor() {

            public void access(byte[] clearBytes) {
                clearStream.write(clearBytes, 0, clearBytes.length);
            }
        };
        guardedByteArray.access(accessor);
        return clearStream.toByteArray();
    }

    private static String decrypt(GuardedString guardedString) {
        final String[] clearText = new String[1];
        GuardedString.Accessor accessor = new GuardedString.Accessor() {

            public void access(char[] clearChars) {
                clearText[0] = new String(clearChars);
            }
        };

        guardedString.access(accessor);
        return clearText[0];
    }
}