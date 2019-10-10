package com.portsip.main;

/**
 * Created by Nigussie on 01.09.2015.
 */
public class Person {
    public String myName = "";
    public String myNumber = "";

    public String getName() {
        return myName;
    }
    public void setName(String name) {
        myName = name;
    }

    public String getPhoneNum() {
        return myNumber;
    }

    public void setPhoneNum(String number) {
        myNumber = number;
    }

    public long getId() {
        return 0;
    }
}