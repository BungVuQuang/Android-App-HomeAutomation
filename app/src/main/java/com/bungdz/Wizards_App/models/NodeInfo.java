package com.bungdz.Wizards_App.models;

public class NodeInfo {

    private   String parent;
    private   String role;
    private   String unicast;
    private   String uuid;

    public NodeInfo(){

    }
    public NodeInfo(String parent, String role, String unicast, String uuid){
        this.parent = parent;
        this.role=role;
        this.unicast=unicast;
        this.uuid=uuid;
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUnicast() {
        return unicast;
    }

    public void setUnicast(String unicast) {
        this.unicast = unicast;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

}
