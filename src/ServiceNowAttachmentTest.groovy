@Grab(group='org.apache.cxf', module='cxf-core', version='3.1.7')
@Grab(group='org.apache.cxf', module='cxf-rt-frontend-jaxrs', version='3.1.7')
@Grab(group='org.apache.cxf', module='cxf-rt-rs-security-oauth2', version='3.1.7')
@Grab(group='org.apache.cxf', module='cxf-rt-rs-extension-providers', version='3.1.7')
@Grab(group='org.apache.cxf', module='cxf-rt-rs-client', version='3.1.7')
@Grab(group='com.fasterxml.jackson.core', module='jackson-core', version='2.8.3')
@Grab(group='com.fasterxml.jackson.core', module='jackson-annotations', version='2.8.3')
@Grab(group='com.fasterxml.jackson.core', module='jackson-databind', version='2.8.3')
@Grab(group='com.fasterxml.jackson.jaxrs', module='jackson-jaxrs-json-provider', version='2.8.3')

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import org.apache.cxf.jaxrs.client.JAXRSClientFactory

import javax.ws.rs.*
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response
import java.nio.charset.StandardCharsets

// *****************************************************************************
//
// *****************************************************************************

class ServiceNow {
    @Path('/attachment')
    interface AttachmentClient {
        @POST
        @Produces('application/json')
        @Path('/file')
        Response uploadContent(
            @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
            @QueryParam('file_name') String fileName,
            @QueryParam('table_name') String tableName,
            @QueryParam('table_sys_id') String tableSysId,
            InputStream body
        )
    }

    def getAttachmentClient() {
        return JAXRSClientFactory.create(
            System.getenv('SERVICENOW_API_URL'),
            AttachmentClient.class,
            [ new JacksonJsonProvider() ],
            System.getenv('SERVICENOW_USERNAME'),
            System.getenv('SERVICENOW_PASSWORD'),
            null)
    }
}

// *****************************************************************************
//
// *****************************************************************************

def sn = new ServiceNow()

def r = sn.attachmentClient.uploadContent(
    'application/octet-stream',
    UUID.randomUUID().toString(),
    'ecc_agent_mib',
    '358901a737300100dcd48c00dfbe5d7e',
    new ByteArrayInputStream('my content'.getBytes(StandardCharsets.UTF_8))
)

println r.readEntity(Map.class)