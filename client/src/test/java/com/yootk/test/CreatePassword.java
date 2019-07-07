package com.yootk.test;

import com.yootk.util.encrypt.EncryptUtil;

public class CreatePassword {
    public static void main(String[] args) {
        // 通过client_id和时间戳生成一个密码
        String password = "client000111" + System.currentTimeMillis() ;
        System.out.println(EncryptUtil.encode(password));
    }
}
