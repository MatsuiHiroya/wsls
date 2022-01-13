package com.example.wsls.repository;

import com.example.wsls.bean.PostedRandom;
import com.example.wsls.bean.Room;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface IRoomPageRepository {
    public Room findRoom(Integer roomId);
    public int insertRandom(Integer roomId, Integer userId, String role, BigInteger random);
    public List<PostedRandom> selectPostedRandomList(Integer roomId);
}
