package com.yootk.util.split;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class SplitPageUtil {
    private Long currentPage ;
    private Integer lineSize ;
    private String column ;
    private String keyword ;
    private HttpServletRequest request ;
    public SplitPageUtil() {
        this.request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest() ;
    }
}
