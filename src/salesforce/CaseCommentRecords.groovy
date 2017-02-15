package salesforce

import com.thoughtworks.xstream.annotations.XStreamImplicit
import org.apache.camel.component.salesforce.api.dto.AbstractQueryRecordsBase

class CaseCommentRecords extends AbstractQueryRecordsBase {
    @XStreamImplicit
    List<CaseComment> records;
}
