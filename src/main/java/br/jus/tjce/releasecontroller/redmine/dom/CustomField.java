package br.jus.tjce.releasecontroller.redmine.dom;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomField {
    private int id;
    private String name;
    private boolean multiple;
    private Object value;

    @JsonProperty("id")
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("multiple")
    public boolean isMultiple() { return multiple; }
    public void setMultiple(boolean multiple) { this.multiple = multiple; }

    @JsonProperty("value")
    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }
}
