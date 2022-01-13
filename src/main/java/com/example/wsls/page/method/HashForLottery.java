package com.example.wsls.page.method;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashForLottery {
    private MessageDigest digest;
    private String randomHash;

    public String randomToHash(Integer random){
        try {
            //受け取った乱数をハッシュ化する
            digest = MessageDigest.getInstance("MD5");
            byte[] result = digest.digest(random.toString().getBytes());
            randomHash = String.format("%040x", new BigInteger(1, result));
        }
        catch(NoSuchAlgorithmException e){
            System.out.println("NoSuchAlgorithmException");
        }
        return randomHash;
    }

}
