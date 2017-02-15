package salesforce

import com.fasterxml.jackson.annotation.JsonProperty
import com.thoughtworks.xstream.annotations.XStreamAlias
import org.apache.camel.component.salesforce.api.dto.AbstractSObjectBase

@XStreamAlias("Case")
class Case extends AbstractSObjectBase {
    Case(String Id) {
        setId(Id)
    }

    @JsonProperty("CaseNumber")
    String caseNumber;
    @JsonProperty("Subject")
    private String subject;
    @JsonProperty("Description")
    String description;
}