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
 *
 * $Id$
 */

package org.forgerock.openidm.provisioner.openicf;

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.framework.api.ConnectorKey;

/**
 * ConnectorReference holds the required {@ConnectorKey} value to find the correct connector in the
 * {@link org.identityconnectors.framework.api.ConnectorInfoManager}. The {@code getConnectorHost} is the key
 * for the ConnectorInfoManager instance.
 *
 * @author $author$
 * @version $Revision$ $Date$
 */
public class ConnectorReference {
    public static final String SINGLE_LOCAL_CONNECTOR_MANAGER = "#LOCAL";
    public static final String OSGI_SERVICE_CONNECTOR_MANAGER = "osgi:service/org.identityconnectors.framework.api.ConnectorInfoManager";
    public static final String OSGI_SERVICE_CONNECTOR_MANAGER_11 = "osgi:service/org.identityconnectors.framework.api.ConnectorInfoManager/(ConnectorBundle-FrameworkVersion=1.1)";

    public enum ConnectorLocation {
        /**
         * Connector loaded with {@code LocalConnectorInfoManagerImpl}.
         */
        LOCAL,
        /**
         * Connector loaded with {@code OsgiConnectorInfoManagerImpl}.
         */
        OSGI,
        /**
         * Connector loaded with {@code RemoteConnectorInfoManagerImpl}.
         */
        REMOTE;
    }
    private final ConnectorKey connectorKey;
    private final String connectorLocationName;
    private final ConnectorLocation connectorLocation;

    /**
     *
     * @param connectorKey
     * @throws AssertionError when the {@code connectorKey} is null.
     */
    public ConnectorReference(ConnectorKey connectorKey) {
        this(connectorKey, null);
    }

    /**
     *
     * @param connectorKey
     * @param connectorHost
     * @throws AssertionError when the {@code connectorKey} is null.
     */
    public ConnectorReference(ConnectorKey connectorKey, String connectorHost) {
        assert null != connectorKey;
        this.connectorKey = connectorKey;
        if (StringUtil.isBlank(connectorHost) || SINGLE_LOCAL_CONNECTOR_MANAGER
                .equalsIgnoreCase(connectorHost)) {
            connectorLocationName = SINGLE_LOCAL_CONNECTOR_MANAGER;
            connectorLocation = ConnectorLocation.LOCAL;
        } else {
            connectorLocationName = connectorHost;
            if (connectorHost.toLowerCase().startsWith("osgi")) {
                connectorLocation = ConnectorLocation.OSGI;
            } else {
                connectorLocation = ConnectorLocation.REMOTE;
            }
        }
    }

    /**
     *
     * @return
     */
    public ConnectorKey getConnectorKey() {
        return connectorKey;
    }

    /**
     * Gets the key for the {@link org.identityconnectors.framework.api.ConnectorInfoManager}
     *
     * @return if no other value was specified the default value is {#SINGLE_LOCAL_CONNECTOR_MANAGER}
     */
    public String getConnectorHost() {
        return connectorLocationName;
    }

    @Override
    public String toString() {
        return (new StringBuilder(connectorKey.toString())).append(" on host: ").append(connectorLocationName).toString();
    }

    public ConnectorLocation getConnectorLocation() {
       return connectorLocation;
    }
}
