/** 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2012 ForgeRock AS. All rights reserved.
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
 */

/**
 * A configuration for allowed HTTP requests. Each entry in the configuration contains a pattern 
 * to match against the incoming request ID and, in the event of a match, the associated roles, 
 * methods, and actions that are allowed for requests on that particular pattern.
 *
 * pattern:  A pattern to match against an incoming request's resource ID
 * roles:  A comma separated list of allowed roles
 * methods:  A comma separated list of allowed methods
 * actions:  A comma separated list of allowed actions
 * customAuthz: A custom function for additional authorization logic/checks (optional)
 * excludePatterns: A comma separated list of patterns to exclude from the pattern match (optional)
 *
 * A single '*' character indicates all possible values.  With patterns ending in "/*", the "*"
 * acts as a wild card to indicate the pattern accepts all resource IDs "below" the specified
 * pattern (prefix).  For example the pattern "managed/*" would match "managed/user" or anything
 * starting with "managed/".  Note: it would not match "managed", which would need to have its 
 * own entry in the config.
 */
var httpAccessConfig = 
{ 
    "configs" : [
        // Anyone can read from these endpoints
        {  
           "pattern"    : "info/*",
           "roles"      : "*",
           "methods"    : "read",
           "actions"    : "*"
        },
        {  
           "pattern"    : "config/ui/configuration",
           "roles"      : "openidm-reg,openidm-authorized",
           "methods"    : "read",
           "actions"    : "*"
        },
        // These options should only be available anonymously if selfReg is enabled
        {  
           "pattern"    : "config/ui/*",
           "roles"      : "openidm-reg",
           "methods"    : "read",
           "actions"    : "*",
           "customAuthz" : "checkIfUIIsEnabled('selfRegistration')"
        },
        {  
           "pattern"    : "managed/user/*",
           "roles"      : "openidm-reg",
           "methods"    : "create",
           "actions"    : "*",
           "customAuthz" : "checkIfUIIsEnabled('selfRegistration') && managedUserRestrictedToAllowedRoles('openidm-authorized')"
        },

        // Anonymous user can call the siteIdentification endpoint if it is enabled:
        {  
           "pattern"    : "endpoint/siteIdentification",
           "roles"      : "openidm-reg",
           "methods"    : "*",
           "actions"    : "*",
           "customAuthz" : "checkIfUIIsEnabled('siteIdentification')"
        },

        // Anonymous user can call the securityQA endpoint if it enabled:
        {  
           "pattern"    : "endpoint/securityQA",
           "roles"      : "openidm-reg",
           "methods"    : "*",
           "actions"    : "*",
           "customAuthz" : "checkIfUIIsEnabled('securityQuestions')"
        },
        // This is needed by both self reg and security questions
        {  
           "pattern"    : "policy/managed/user/*",
           "roles"      : "openidm-reg",
           "methods"    : "read,action",
           "actions"    : "*",
           "customAuthz" : "checkIfUIIsEnabled('selfRegistration') || checkIfUIIsEnabled('securityQuestions')"
        },

        // openidm-admin can request anything
        {  
            "pattern"   : "*",
            "roles"     : "openidm-admin",
            "methods"   : "*", // default to all methods allowed
            "actions"   : "*" // default to all actions allowed
        },
        
        // Additional checks for authenticated users
        {  
            "pattern"   : "policy/*",
            "roles"     : "openidm-authorized", // openidm-authorized is logged-in users
            "methods"   : "read,action",
            "actions"   : "*"
        },
        {  
            "pattern"   : "config/ui/*",
            "roles"     : "openidm-authorized",
            "methods"   : "read",
            "actions"   : "*"
        },
        {  
            "pattern"   : "authentication",
            "roles"     : "openidm-authorized",
            "methods"   : "action",
            "actions"   : "reauthenticate"
        },
        {   
            "pattern"   : "*",
            "roles"     : "openidm-authorized",
            "methods"   : "create,read,update,patch,action,query", // note the missing 'delete' - by default, users cannot delete things
            "actions"   : "*",
            "customAuthz" : "ownDataOnly() && managedUserRestrictedToAllowedRoles('openidm-authorized')"
        },

        // enforcement of which notifications you can read and delete is done within the endpoint 
        {
            "pattern"   : "endpoint/usernotifications",
            "roles"     : "openidm-authorized",
            "methods"   : "read,delete",
            "actions"   : "*"
        },
        
        // Workflow-related endpoints for authorized users
        {
            "pattern"   : "workflow/taskinstance/*",
            "roles"     : "openidm-authorized",
            "methods"   : "action",
            "actions"   : "complete",
            "customAuthz" : "isMyTask()"
        },
        {
            "pattern"   : "workflow/taskinstance/*",
            "roles"     : "openidm-authorized",
            "methods"   : "read,update",
            "actions"   : "*",
            "customAuthz" : "canUpdateTask()"
        },
        {
            "pattern"   : "workflow/processinstance/",
            "roles"     : "openidm-authorized",
            "methods"   : "action",
            "actions"   : "createProcessInstance",
            "customAuthz": "isAllowedToStartProcess()"
        },
        {
            "pattern"   : "workflow/processdefinition/*",
            "roles"     : "openidm-authorized",
            "methods"   : "*",
            "actions"   : "read",
            "customAuthz": "isOneOfMyWorkflows()"
        },
        // Clients authenticated via SSL mutual authentication
        {
            "pattern"   : "*",
            "roles"     : "openidm-cert",
            "methods"   : "",  // default to no methods allowed
            "actions"   : ""  // default to no actions allowed
        }
    ] 
};

// Additional custom authorization functions go here
