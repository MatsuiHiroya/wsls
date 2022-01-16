package com.example.wsls.bean;

import java.io.Serializable;

public class PostedRandom implements Serializable {

    //列名と同じにしよう
    private Integer roomId;
    private Integer userId;
    private String role;
    private Long random;
    private Long result;

    public PostedRandom(){
        roomId = null;
        userId = null;
        role = "";
        random = null;
        result = 0L;
    }

    public PostedRandom(Integer roomId, Integer userId, String role, Long random, Long result) {
        this.roomId = roomId;
        this.userId = userId;
        this.role = role;
        this.random = random;
        this.result = result;
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

    public Long getResult() {
        return result;
    }

    public void setResult(Long result) {
        this.result = result;
    }
}
