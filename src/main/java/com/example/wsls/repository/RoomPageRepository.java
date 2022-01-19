package com.example.wsls.repository;

import com.example.wsls.bean.PostedRandom;
import com.example.wsls.bean.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public class RoomPageRepository implements IRoomPageRepository{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RoomPageRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Room findRoom(Integer roomId){
        String sql = "select * from ROOM where id = ?";
        return jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<>(Room.class),roomId);
    }

    @Override
    public int insertRandom(Integer roomId, Integer userId, String role, Long random){
        if (checkPostedRandom(roomId,userId) == false) {
            var sql = "delete from POSTED_RANDOM where ROOM_ID = ? and USER_ID = ?";
            jdbcTemplate.update(sql,roomId,userId);
        }
        var sql = "insert into POSTED_RANDOM(ROOM_ID,USER_ID,ROLE,RANDOM,RESULT) values (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,roomId,userId,role,random,0L);
    }

    @Override
    public List<PostedRandom> selectPostedRandomList(Integer roomId){
        var sql = "select * from POSTED_RANDOM where ROOM_ID = ? and RANDOM is not null";
        return jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(PostedRandom.class),roomId);
    }

    public boolean checkPostedRandom(Integer roomId,Integer userId){
        var sql = "select RANDOM from POSTED_RANDOM where ROOM_ID = ? and USER_ID = ?";
        var pr= jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(PostedRandom.class),roomId,userId);
        if (pr == null) return true;
        else return false;
    }

    @Override
    public int closeLottery(Integer roomId){
        var sql = "update ROOM set FLAG = 'false' where ID = ?";
        return jdbcTemplate.update(sql,roomId);
    }

    @Override
    public PostedRandom findSponsorInfo(Integer roomId,Integer sponsorId){
        var sql = "select * from POSTED_RANDOM where ROOM_ID = ? and USER_ID = ?";
        return jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<>(PostedRandom.class),roomId,sponsorId);
    }

}
