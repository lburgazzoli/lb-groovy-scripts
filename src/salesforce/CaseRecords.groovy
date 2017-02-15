package salesforce

import com.thoughtworks.xstream.annotations.XStreamImplicit
import org.apache.camel.component.salesforce.api.dto.AbstractQueryRecordsBase

class CaseRecords extends AbstractQueryRecordsBase {
    @XStreamImplicit
    List<Case> records;
}