package br.jus.tjce.releasecontroller.redmine.dom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Relation {
    
    private Integer id;

    @JsonProperty("issue_id")
    private Integer issueId;

    @JsonProperty("issue_to_id")
    private Integer issueToId;

    @JsonProperty("relation_type")
    private String relationType;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIssueId() { return issueId; }
    public void setIssueId(Integer issueId) { this.issueId = issueId; }

    public Integer getIssueToId() { return issueToId; }
    public void setIssueToId(Integer issueToId) { this.issueToId = issueToId; }

    public String getRelationType() { return relationType; }
    public void setRelationType(String relationType) { this.relationType = relationType; }
}
