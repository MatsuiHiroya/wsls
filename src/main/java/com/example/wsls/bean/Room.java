package com.example.wsls.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class Room implements Serializable {

    private Integer id;
    private String name;
    private String overview;
    //ここキャメルでokだっけ？
    private Timestamp limitTime;
    private boolean flag;
    private Integer authorId;

    /*public Room(Integer id, String name, String overview, Timestamp limitTime, boolean flag, Integer authorId) {
        this.id = null;
        this.name = "";
        this.overview = "";
        this.limitTime = null;
        this.flag = false;
        this.authorId = null;
    }*/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Timestamp getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(Timestamp limitTime) {
        this.limitTime = limitTime;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }
}
