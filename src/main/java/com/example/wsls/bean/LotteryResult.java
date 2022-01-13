package com.example.wsls.bean;

public class LotteryResult {

    private Integer roomId;
    private Integer userId;
    private Long postedRandom;
    private Long calculatedRandom;

    public LotteryResult(Integer roomId,Integer userId,Long postedRandom,Long calculatedRandom){
        this.roomId = roomId;
        this.userId = userId;
        this.postedRandom = postedRandom;
        this.calculatedRandom = calculatedRandom;
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

    public Long getPostedRandom() {
        return postedRandom;
    }

    public void setPostedRandom(Long postedRandom) {
        this.postedRandom = postedRandom;
    }

    public Long getCalculatedRandom() {
        return calculatedRandom;
    }

    public void setCalculatedRandom(Long calculatedRandom) {
        this.calculatedRandom = calculatedRandom;
    }
}
