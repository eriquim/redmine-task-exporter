package br.jus.tjce.releasecontroller.redmine.dom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "user")
public class User {
    private int id;
    private String login;
    private boolean admin;
    private String firstname;
    private String lastname;
    private String mail;
    private String status;
    private OffsetDateTime createdOn;
    private OffsetDateTime lastLoginOn;
    private List<CustomField> customFields;
    private List<Membership> memberships;
    private List<Object> groups;

    @JsonProperty("id")
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @JsonProperty("login")
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    @JsonProperty("admin")
    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }

    @JsonProperty("firstname")
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    @JsonProperty("lastname")
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    @JsonProperty("mail")
    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    @JsonProperty("status")
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @JsonProperty("created_on")
    public OffsetDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(OffsetDateTime createdOn) { this.createdOn = createdOn; }

    @JsonProperty("last_login_on")
    public OffsetDateTime getLastLoginOn() { return lastLoginOn; }
    public void setLastLoginOn(OffsetDateTime lastLoginOn) { this.lastLoginOn = lastLoginOn; }

    @JsonProperty("custom_fields")
    public List<CustomField> getCustomFields() { return customFields; }
    public void setCustomFields(List<CustomField> customFields) { this.customFields = customFields; }

    @JsonProperty("memberships")
    public List<Membership> getMemberships() { return memberships; }
    public void setMemberships(List<Membership> memberships) { this.memberships = memberships; }

    @JsonProperty("groups")
    public List<Object> getGroups() { return groups; }
    public void setGroups(List<Object> groups) { this.groups = groups; }
}
