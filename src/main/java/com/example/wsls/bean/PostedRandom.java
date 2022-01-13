package com.example.wsls.bean;

import java.io.Serializable;
import java.math.BigInteger;

public class PostedRandom implements Serializable {

    //列名と同じにしよう
    private Integer roomId;
    private Integer userId;
    private String role;
    private Long random;

    public PostedRandom(){
        roomId = null;
        userId = null;
        role = "";
        random = null;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getRandom() {
        return random;
    }

    public void setRandom(Long random) {
        this.random = random;
    }
}
