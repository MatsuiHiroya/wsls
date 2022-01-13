package com.example.wsls.service;

import com.example.wsls.bean.LotteryResult;
import com.example.wsls.bean.PostedRandom;
import com.example.wsls.bean.Room;
import com.example.wsls.repository.IRoomPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoomPageService implements IRoomPageService {

    //private MessageDigest digest;
    private String randomHash;

    private final IRoomPageRepository roomPageRepository;

    @Autowired
    public RoomPageService(IRoomPageRepository roomPageRepository){
        this.roomPageRepository = roomPageRepository;
    }

    //主催者だったらtrue,参加者だったらfalseをreturn
    @Override
    public boolean checkUserRole(Integer roomId,Integer userId){
        var roomAuthorId =  roomPageRepository.findRoom(roomId).getAuthorId();
        if(roomAuthorId == userId) return true;
        else return false;
    }

    @Override
    public Room findRoom(Integer roomId){
        return roomPageRepository.findRoom(roomId);
    }

    @Override
    public void insertRandom(Integer roomId,Integer userId,String role,String random){
        //String型のrandomをBigInteger型に変換
        var bigIntegerRandom = BigInteger.valueOf(Long.parseLong(random));
        var n = roomPageRepository.insertRandom(roomId,userId,role,bigIntegerRandom);
        System.out.println("input line:"+n);
    }

    @Override
    public List<PostedRandom> selectPostedRandom(Integer roomId){
        return roomPageRepository.selectPostedRandomList(roomId);
    }

    @Override
    public List<LotteryResult> calculation(Integer roomId){
        var postedRandomList = roomPageRepository.selectPostedRandomList(roomId);
        //postedRandomList.stream().forEach(i -> System.out.println(i.getRandom()));
        //乱数の合計を取得
        Long totalRandom = postedRandomList.stream()
                    .mapToLong(list -> list.getRandom())
                    .sum();
        System.out.println(totalRandom);

        //listから主催者の乱数を取得
        Long sponsorRandom = postedRandomList.stream()
                    .filter(item -> item.getRole().equals("S"))
                    .mapToLong(list -> list.getRandom())
                    .findFirst()
                    .getAsLong();
        System.out.println(sponsorRandom);


        //var s = randomToHash(sponsorRandom);
        //hashToLong(s);

        var lotteryResultList = new ArrayList<LotteryResult>();
        postedRandomList.stream()
                    .filter(item -> item.getRole().equals("P"))
                    .forEach(item -> {
                        //(ri,rs,sum(r1...rn,rs))をpreRandomに代入
                        Long preRandom = item.getRandom() + sponsorRandom + totalRandom;
                        //h(ri,rs,sum(r1...rn,rs))を計算
                        var hashedRandom = randomToHash(preRandom);
                        lotteryResultList.add(new LotteryResult(
                                roomId,
                                item.getUserId(),
                                item.getRandom(),
                                //抽選数値
                                hashToLong(hashedRandom)));
                    });

        lotteryResultList.stream().
                forEach(i -> System.out.println(i.getUserId() + "の抽選数値:" + i.getCalculatedRandom()));

        return lotteryResultList;

        //(ri,rs,sum(r1...rn,rs))をrに代入
        //Long preRandom = postedRandom + sponsorRandom + totalRandom;

        /*for(PostedRandom p : postedRandomList){

        }*/
                    //.findFirst()
                    //.getAsLong();*/
                    //.mapToLong(list -> list.getRandom());

    }

    //受け取った乱数をハッシュ化して返すメソッド
    @Override
    public String randomToHash(Long random/*Long postedRandom,Long sponsorRandom,Long totalRandom*/){
        //(ri,rs,sum(r1...rn,rs))をrに代入
        //Long preRandom = postedRandom + sponsorRandom + totalRandom;
        try {
            //受け取った乱数をハッシュ化する
            var digest = MessageDigest.getInstance("SHA-256");
            byte[] result = digest.digest(random.toString().getBytes());
            randomHash = String.format("%040x", new BigInteger(1, result));
        }
        catch(NoSuchAlgorithmException e){
            System.out.println("NoSuchAlgorithmException");
        }
        return randomHash;
    }

    //ハッシュ値を比較しやすくするために、16進数のハッシュ値をLongの値に変換するメソッド
    private Long hashToLong(String hashResult) {
        System.out.println("Stringのhash:" + hashResult);
        Long multipleTotal = 1L;
        Long sumTotal = 1L;
        int countOf0 = 0;

        for(int i = 0; i < hashResult.length(); i++){
            Long l = Long.parseLong(String.valueOf(hashResult.charAt(i)), 16);
            if(i < 16 && l != 0) multipleTotal = multipleTotal * l;
            else if(l == 0) countOf0++;
            else sumTotal = sumTotal+l;
        }

        /**
         * bigIntegerを使う場合
         * 京や垓の数値が出て来るため。運用を考えると不向きかも
        BigInteger b = new BigInteger("0");
        for (int i = hashResult.length()-1; i >= 0; i--){
            Long l = Long.parseLong(String.valueOf(hashResult.charAt(i)), 16);
            //16進数⇒10進数に変換
            b = b.add(BigInteger.valueOf(l * (long)Math.pow(16,(hashResult.length()-1) - i)));
            System.out.println("i:" + i + "、l:"+ l + "、b:" + b );
        }*/

        System.out.println("抽選数値:" + (multipleTotal + (sumTotal*countOf0)));
        return multipleTotal + (sumTotal*countOf0);

    }

}
