package com.yootk.oauth.service.impl;

import com.yootk.oauth.dao.IMemberDAO;
import com.yootk.oauth.service.IMemberService;
import com.yootk.oauth.vo.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements IMemberService {
    @Autowired
    private IMemberDAO memberDAO ;
    @Override
    public Member get(String mid) {
        return this.memberDAO.findById(mid);
    }
}
