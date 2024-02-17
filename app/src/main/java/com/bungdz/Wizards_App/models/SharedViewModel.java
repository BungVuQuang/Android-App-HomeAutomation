package com.bungdz.Wizards_App.models;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

public class SharedViewModel extends ViewModel {
    private ArrayList<String> listRole;
    private boolean isGatewayConnected;

    public void setListRole(ArrayList<String> listRole) {
        this.listRole = listRole;
    }
    public void setActive(boolean isGatewayConnected) {
        this.isGatewayConnected = isGatewayConnected;
    }

    public boolean getActive() {
        return isGatewayConnected;
    }

    public void addRole(String Role) {
        this.listRole.add(Role);
    }

    public void removeRole(String Role) {
        this.listRole.remove(Role);
    }

    public ArrayList<String> getListRole() {
        return listRole;
    }
}
