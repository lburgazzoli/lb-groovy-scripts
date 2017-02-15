package salesforce

import com.fasterxml.jackson.annotation.JsonProperty
import com.thoughtworks.xstream.annotations.XStreamAlias
import org.apache.camel.component.salesforce.api.dto.AbstractSObjectBase

@XStreamAlias("CaseComment")
class CaseComment extends AbstractSObjectBase {
    @JsonProperty("ParentId")
    String parentId
    @JsonProperty("IsPublished")
    Boolean isPublished
    @JsonProperty("CommentBody")
    String commentBody
}
