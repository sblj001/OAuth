package com.yootk.test;

import com.yootk.oauth.util.EncryptUtil;

import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

public class TestPassword {
    public static void main(String[] args) {
        System.out.println(EncryptUtil.encode("hello"));
        Deque<Serializable> deque = new LinkedList<>() ;
        deque.push("42ae6c7e-ab12-47b9-8a72-ec7060816baf");
        System.out.println(deque.contains("42ae6c7e-ab12-47b9-8a72-ec7060816baf"));
    }
}
