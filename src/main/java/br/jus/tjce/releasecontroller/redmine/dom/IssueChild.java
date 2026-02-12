package br.jus.tjce.releasecontroller.redmine.dom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueChild {
    private Integer id;
    private Tracker tracker;
    private String subject;
    private List<IssueChild> children;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Tracker getTracker() { return tracker; }
    public void setTracker(Tracker tracker) { this.tracker = tracker; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public List<IssueChild> getChildren() { return children; }
    public void setChildren(List<IssueChild> children) { this.children = children; }
}
