/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.connector.utils;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.esb.connector.exception.InvalidConfigurationException;
import org.wso2.carbon.esb.connector.exception.ResponseParsingException;
import org.wso2.carbon.esb.connector.exception.SalesforceConnectionException;
import org.wso2.carbon.esb.connector.pojo.SalesforceConfig;

import javax.xml.stream.XMLStreamException;

public class SalesforceUtils {

    private static final Log log = LogFactory.getLog(SalesforceUtils.class);
    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String NO_ENTITY_BODY = "NO_ENTITY_BODY";
    private static final String HTTP_SC = "HTTP_SC";

    /**
     * Retrieves connection name from message context if configured as configKey attribute
     * or from the template parameter
     *
     * @param messageContext Message Context from which the parameters should be extracted from
     * @return connection name
     */
    public static String getConnectionName(MessageContext messageContext) throws InvalidConfigurationException {

        String connectionName = (String) messageContext.getProperty(SalesforceConstants.SF_OAUTH_CONFIG_NAME);
        if (connectionName == null) {
            throw new InvalidConfigurationException("Connection name is not set.");
        }
        return connectionName;
    }

    public static String getCreateJobUrl(SalesforceConfig salesforceConfig) {
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_INGEST_RELATIVE_PATH;
    }

    public static String getCreateQueryJobUrl(SalesforceConfig salesforceConfig) {
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_QUERY_RELATIVE_PATH;
    }

    public static String getUploadJobDataUrl(SalesforceConfig salesforceConfig, String jobId) {
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_INGEST_RELATIVE_PATH
                + jobId + SalesforceConstants.SF_API_JOBS_BATCHES;
    }

    public static String getCloseJobUrl(SalesforceConfig salesforceConfig, String jobId) {
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_INGEST_RELATIVE_PATH + jobId +  "/";
    }

    public static String getAbortJobUrl(SalesforceConfig salesforceConfig, String jobId) {
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_INGEST_RELATIVE_PATH + jobId;
    }

    public static String getDeleteJobUrl(SalesforceConfig salesforceConfig, String jobId) {
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_INGEST_RELATIVE_PATH + jobId;
    }

    public static String getAbortQueryJobUrl(SalesforceConfig salesforceConfig, String jobId) {
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_QUERY_RELATIVE_PATH + jobId;
    }

    public static String getDeleteQueryJobUrl(SalesforceConfig salesforceConfig, String jobId) {
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_QUERY_RELATIVE_PATH + jobId;
    }

    public static String getGetAllJobInfoUrl(SalesforceConfig salesforceConfig, Boolean isPkChunkingEnabled,
                                             String jobType, String queryLocator) {
        String paramString = "";
        if (isPkChunkingEnabled != null) {
            paramString += SalesforceConstants.IS_PK_CHUNKING_ENABLED + "=" + isPkChunkingEnabled;
        }
        if (StringUtils.isNotEmpty(queryLocator)) {
            if (StringUtils.isNotEmpty(paramString)) {
                paramString += "&";
            }
            paramString += SalesforceConstants.QUERY_LOCATOR + "=" + queryLocator;
        }
        if (StringUtils.isNotEmpty(jobType)) {
            if (StringUtils.isNotEmpty(paramString)) {
                paramString += "&";
            }
            paramString += SalesforceConstants.JOB_TYPE + "=" + jobType;
        }
        if (StringUtils.isNotEmpty(paramString)) {
            paramString = "?" + paramString;
        }
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_INGEST_RELATIVE_PATH + paramString;
    }

    public static String getGetAllQueryJobInfoUrl(SalesforceConfig salesforceConfig, Boolean isPkChunkingEnabled,
                                                  String jobType, String queryLocator) {
        String paramString = "";
        if (isPkChunkingEnabled != null) {
            paramString += SalesforceConstants.IS_PK_CHUNKING_ENABLED + "=" + isPkChunkingEnabled;
        }
        if (StringUtils.isNotEmpty(queryLocator)) {
            if (StringUtils.isNotEmpty(paramString)) {
                paramString += "&";
            }
            paramString += SalesforceConstants.QUERY_LOCATOR + "=" + queryLocator;
        }
        if (StringUtils.isNotEmpty(jobType)) {
            if (StringUtils.isNotEmpty(paramString)) {
                paramString += "&";
            }
            paramString += SalesforceConstants.JOB_TYPE + "=" + jobType;
        }
        if (StringUtils.isNotEmpty(paramString)) {
            paramString = "?" + paramString;
        }
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_QUERY_RELATIVE_PATH + paramString;
    }

    public static String getGetJobInfoUrl(SalesforceConfig salesforceConfig, String jobId) {
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_INGEST_RELATIVE_PATH + jobId;
    }

    public static String getGetQueryJobInfoUrl(SalesforceConfig salesforceConfig, String jobId) {
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_QUERY_RELATIVE_PATH + jobId;
    }

    public static String getGetQueryJobResultUrl(SalesforceConfig salesforceConfig, String queryJobId,
                                                 Integer maxRecords, String locator) {
        String paramString = "";
        if (maxRecords != null) {
            paramString += SalesforceConstants.LOCATOR + "=" + locator;
        }
        if (StringUtils.isNotEmpty(locator)) {
            if (StringUtils.isNotEmpty(paramString)) {
                paramString += "&";
            }
            paramString += SalesforceConstants.MAX_RECORDS + "=" + maxRecords;
        }
        if (StringUtils.isNotEmpty(paramString)) {
            paramString = "?" + paramString;
        }
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_QUERY_RELATIVE_PATH + queryJobId
                + SalesforceConstants.SF_API_JOBS_QUERY_RESULTS_RELATIVE_PATH
                + paramString;
    }

    public static String getGetJobFailedResultsUrl(SalesforceConfig salesforceConfig, String queryJobId) {
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_INGEST_RELATIVE_PATH + queryJobId
                + SalesforceConstants.SF_API_JOB_FAILED_RESULTS_RELATIVE_PATH;
    }

    public static String getGetJobSuccessfulResultsUrl(SalesforceConfig salesforceConfig, String queryJobId) {
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_INGEST_RELATIVE_PATH + queryJobId
                + SalesforceConstants.SF_API_JOB_SUCCESSFUL_RESULTS_RELATIVE_PATH;
    }

    public static String getGetJobUnprocessedResultsUrl(SalesforceConfig salesforceConfig, String queryJobId) {
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_API_JOBS_INGEST_RELATIVE_PATH + queryJobId
                + SalesforceConstants.SF_API_JOB_UNPROCESSED_RESULTS_RELATIVE_PATH;
    }

    public static String getSFTokenUrl(SalesforceConfig salesforceConfig) {
        return salesforceConfig.getInstanceUrl() + SalesforceConstants.SF_TOKEN_RELATIVE_PATH + "?" + GRANT_TYPE + "="
                + REFRESH_TOKEN + "&" + CLIENT_ID + "=" + salesforceConfig.getClientId() + "&"
                + CLIENT_SECRET + "=" + salesforceConfig.getClientSecret() + "&" + REFRESH_TOKEN + "="
                + salesforceConfig.getRefreshToken();
    }

    /**
     * Sets the error code and error detail in message
     *
     * @param messageContext Message Context
     * @param statusCode     Status code to be set
     * @param message        Error message to be set
     */
    public static void setErrorsInMessage(MessageContext messageContext, int statusCode, String message) {
        messageContext.setProperty(ResponseConstants.PROPERTY_ERROR_CODE, statusCode);
        messageContext.setProperty(ResponseConstants.PROPERTY_ERROR_MESSAGE, message);
    }

    public static BulkJobOperationType getBulkJobOperationTypeEnum(String enumString)
            throws InvalidConfigurationException {
        try {
            return BulkJobOperationType.valueOf(enumString);
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Invalid operation type provided: " + enumString);
        }
    }

    public static BulkQueryJobOperationType getBulkQueryJobOperationTypeEnum(String enumString)
            throws InvalidConfigurationException {
        try {
            return BulkQueryJobOperationType.valueOf(enumString);
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Invalid operation type provided: " + enumString);
        }
    }

    public static BulkJobState getBulkJobStateTypeEnum(String enumString) throws InvalidConfigurationException {
        try {
            return BulkJobState.valueOf(enumString);
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Invalid job state provided: " + enumString);
        }
    }

    public static ColumnDelimiter getColumnDelimiterEnum(String enumString) throws InvalidConfigurationException {
        try {
            return ColumnDelimiter.valueOf(enumString);
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Invalid content type provided: " + enumString);
        }
    }

    public static LineEnding getLineEndingEnum(String enumString) throws InvalidConfigurationException {
        try {
            return LineEnding.valueOf(enumString);
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Invalid content type provided: " + enumString);
        }
    }

    public static JobType getJobTypeEnum(String enumString) throws InvalidConfigurationException {
        try {
            return JobType.valueOf(enumString);
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Invalid content type provided: " + enumString);
        }
    }

    /**
     * Generates the output payload
     *
     * @param messageContext The message context that is processed
     * @param xmlString   Result of the status
     */
    public static void generateOutput(MessageContext messageContext, String xmlString)
            throws InvalidConfigurationException {

        try {
            OMElement omElement = AXIOMUtil.stringToOM(xmlString);
            //Detaching first element (soapBody.getFirstElement().detach()) will be done by following method anyway.
            JsonUtil.removeJsonPayload(((Axis2MessageContext) messageContext).getAxis2MessageContext());

            ((Axis2MessageContext) messageContext).getAxis2MessageContext().
                    removeProperty("NO_ENTITY_BODY");
            SOAPBody soapBody = messageContext.getEnvelope().getBody();
            soapBody.addChild(omElement);
        } catch (XMLStreamException e) {
            throw new InvalidConfigurationException(e.getMessage(), e);
        }
    }

    public static void generateJsonOutput(MessageContext messageContext, String jsonString, int responseCode)
            throws ResponseParsingException {
        try {
            org.apache.axis2.context.MessageContext axisCtx = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
            axisCtx.setProperty(Constants.Configuration.MESSAGE_TYPE, RequestConstants.APPLICATION_JSON);
            axisCtx.setProperty(Constants.Configuration.CONTENT_TYPE, RequestConstants.APPLICATION_JSON);
            axisCtx.removeProperty(NO_ENTITY_BODY);
            JsonUtil.getNewJsonPayload(axisCtx, jsonString, true, true);
            axisCtx.setProperty(HTTP_SC, responseCode);
        } catch (AxisFault e) {
            throw new ResponseParsingException(e.getMessage(), e);
        }
    }


    /**
     * Create an OMElement.
     *
     * @param elementName Name of the element
     * @param value       Value to be added
     * @return OMElement or null if error
     */
    public static OMElement createOMElement(String elementName, String value) {
        OMElement resultElement = null;
        try {
            if (StringUtils.isNotEmpty(value)) {
                resultElement = AXIOMUtil.
                        stringToOM("<" + elementName + ">" + value
                                + "</" + elementName + ">");
            } else {
                resultElement = AXIOMUtil.
                        stringToOM("<" + elementName
                                + "></" + elementName + ">");
            }
        } catch (XMLStreamException | OMException e) {
            log.error("Error while generating OMElement from element name" + elementName, e);
        }
        return resultElement;
    }

    public static String csvToXml(String csvString) {
        String[] lines = csvString.split("\n");
        String[] headers = lines[0].split(",");
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].contains("\"")) {
                headers[i] = headers[i].replace("\"", "");
            }
        }
        StringBuilder xml = new StringBuilder("<root>\n");
        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].split(",");
            xml.append("  <record>\n");
            for (int j = 0; j < headers.length; j++) {
                xml.append("    <").append(headers[j]).append(">").append(fields[j]).append("</").append(headers[j]).append(">\n");
            }
            xml.append("  </record>\n");
        }
        xml.append("</root>");
        return xml.toString();
    }

    public static String csvToJson(String csvString) {
        String[] lines = csvString.split("\n");
        String[] headers = lines[0].split(",");
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].split(",");
            json.append("  {\n");
            for (int j = 0; j < headers.length; j++) {
                json.append("    \"").append(headers[j]).append("\": \"").append(fields[j]).append("\"");
                if (j < headers.length - 1) {
                    json.append(",\n");
                } else {
                    json.append("\n");
                }
            }
            json.append("  }");
            if (i < lines.length - 1) {
                json.append(",\n");
            } else {
                json.append("\n");
            }
        }
        json.append("]");
        return json.toString();
    }

    public static String getSuccessXml() {
        return "<result>Success</result>";
    }

    public static String getSuccessJson() {
        return "{\"result\":\"Success\"}";
    }

    public static void generateErrorOutput(MessageContext messageContext, Exception e) {
        org.apache.axis2.context.MessageContext axisCtx = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
        axisCtx.setProperty(Constants.Configuration.MESSAGE_TYPE, RequestConstants.APPLICATION_JSON);
        axisCtx.setProperty(Constants.Configuration.CONTENT_TYPE, RequestConstants.APPLICATION_JSON);
        axisCtx.removeProperty(NO_ENTITY_BODY);
        String jsonString = "{\"error\":\"" + e.getMessage() + "\"}";
        log.info("setting error with code: ");
        try {
            JsonUtil.getNewJsonPayload(axisCtx, jsonString, true, true);
            if (e instanceof InvalidConfigurationException) {
                axisCtx.setProperty(HTTP_SC, ResponseConstants.HTTP_BAD_REQUEST);
            } else if (e instanceof ResponseParsingException) {
                axisCtx.setProperty(HTTP_SC, ResponseConstants.HTTP_INTERNAL_SERVER_ERROR);
            } else if (e instanceof SalesforceConnectionException){
                int code = ((SalesforceConnectionException)e).getResponseCode();
                axisCtx.setProperty(HTTP_SC, code);
            } else {
                axisCtx.setProperty(HTTP_SC, ResponseConstants.HTTP_INTERNAL_SERVER_ERROR);
            }
        } catch (AxisFault ex) {
            log.error("Error while generating error output", ex);
            axisCtx.setProperty(HTTP_SC, 500);
        }
    }
}
