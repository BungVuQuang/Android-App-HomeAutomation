package com.bungdz.Wizards_App.models;

public class User {
    public  String name, email,num;

    public User(){


    }
    public User(String name, String email, String num){
        this.name=name;
        this.email=email;
        this.num=num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
