package com.example.wsls.service;

import com.example.wsls.bean.LotteryResult;
import com.example.wsls.bean.PostedRandom;
import com.example.wsls.bean.Room;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IRoomPageService {
    public boolean checkUserRole(Integer roomId,Integer userId);
    public Room findRoom(Integer roomId);
    public void insertRandom(Integer roomId,Integer userId,String role,String random);
    public List<PostedRandom> selectPostedRandom(Integer roomId);
    public List<PostedRandom> calculation(Integer roomId);
    public String randomToHash(Long random);
    public void closeLottery(Integer roomId);
    public PostedRandom findSponsorInfo(Integer roomId,Integer sponsorId);
}
