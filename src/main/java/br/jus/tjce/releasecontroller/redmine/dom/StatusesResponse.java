package br.jus.tjce.releasecontroller.redmine.dom;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusesResponse {
    
    @JsonProperty("issue_statuses")
    private List<Status> issueStatuses;

    public List<Status> getIssueStatuses() {
        return issueStatuses;
    }

    public void setIssueStatuses(List<Status> issueStatuses) {
        this.issueStatuses = issueStatuses;
    }
}
