package br.jus.tjce.releasecontroller.redmine.dom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Membership {
    private int id;
    private Project project;
    private List<Role> roles;

    @JsonProperty("id")
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @JsonProperty("project")
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    @JsonProperty("roles")
    public List<Role> getRoles() { return roles; }
    public void setRoles(List<Role> roles) { this.roles = roles; }
}
