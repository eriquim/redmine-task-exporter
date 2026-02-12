package br.jus.tjce.releasecontroller.redmine.dom;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "issue")
public class Issue {

	@JsonProperty("id")
    private Integer id;

	@JsonProperty("project")
    private Project project;

	@JsonProperty("tracker")
    private Tracker tracker;

	@JsonProperty("status")
    private Status status;
	
	@JsonProperty("priority")
    private Priority priority;
    
	@JsonProperty("author")
    private Author author;

    @JsonProperty("assigned_to")
    private AssignedTo assignedTo;

    @JsonProperty("fixed_version")
    private FixedVersion fixedVersion;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("description")
    private String description;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("due_date")
    private String dueDate;

    @JsonProperty("done_ratio")
    private Integer doneRatio;

    @JsonProperty("is_private")
    private Boolean isPrivate;

    @JsonProperty("estimated_hours")
    private Double estimatedHours;

    @JsonProperty("total_estimated_hours")
    private Double totalEstimatedHours;

    @JsonProperty("spent_hours")
    private Double spentHours;

    @JsonProperty("total_spent_hours")
    private Double totalSpentHours;

    @JsonProperty("custom_fields")
    private List<CustomField> customFields;

    @JsonProperty("created_on")
    private OffsetDateTime createdOn;

    @JsonProperty("updated_on")
    private OffsetDateTime updatedOn;

    @JsonProperty("closed_on")
    private OffsetDateTime closedOn;

    private List<IssueChild> children;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public Tracker getTracker() { return tracker; }
    public void setTracker(Tracker tracker) { this.tracker = tracker; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    public AssignedTo getAssignedTo() { return assignedTo; }
    public void setAssignedTo(AssignedTo assignedTo) { this.assignedTo = assignedTo; }

    public FixedVersion getFixedVersion() { return fixedVersion; }
    public void setFixedVersion(FixedVersion fixedVersion) { this.fixedVersion = fixedVersion; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public Integer getDoneRatio() { return doneRatio; }
    public void setDoneRatio(Integer doneRatio) { this.doneRatio = doneRatio; }

    public Boolean getIsPrivate() { return isPrivate; }
    public void setIsPrivate(Boolean isPrivate) { this.isPrivate = isPrivate; }

    public Double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Double estimatedHours) { this.estimatedHours = estimatedHours; }

    public Double getTotalEstimatedHours() { return totalEstimatedHours; }
    public void setTotalEstimatedHours(Double totalEstimatedHours) { this.totalEstimatedHours = totalEstimatedHours; }

    public Double getSpentHours() { return spentHours; }
    public void setSpentHours(Double spentHours) { this.spentHours = spentHours; }

    public Double getTotalSpentHours() { return totalSpentHours; }
    public void setTotalSpentHours(Double totalSpentHours) { this.totalSpentHours = totalSpentHours; }

    public List<CustomField> getCustomFields() { return customFields; }
    public void setCustomFields(List<CustomField> customFields) { this.customFields = customFields; }

    public OffsetDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(OffsetDateTime createdOn) { this.createdOn = createdOn; }

    public OffsetDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(OffsetDateTime updatedOn) { this.updatedOn = updatedOn; }

    public OffsetDateTime getClosedOn() { return closedOn; }
    public void setClosedOn(OffsetDateTime closedOn) { this.closedOn = closedOn; }

    public List<IssueChild> getChildren() { return children; }
    public void setChildren(List<IssueChild> children) { this.children = children; }
}
