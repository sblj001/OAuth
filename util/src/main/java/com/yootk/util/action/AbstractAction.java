package com.yootk.util.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.io.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public class AbstractAction {
    private static final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd") ;
    @Autowired
    private MessageSource messageSource ;

    public String getUploadDir() {
        return "/upload/" ;
    }
    public String upload(MultipartFile file) {  // 进行上传的控制
        String fileName = UUID.randomUUID() + "." + file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1) ;
        String filePath = this.getApplication().getRealPath(this.getUploadDir()) + fileName ;
        OutputStream output = null ;
        try {
            File saveFile = new File(filePath) ;
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs() ;
            }
            output = new FileOutputStream(saveFile);
            byte [] data = new byte[1024] ;
            int temp = 0 ;
            InputStream inputStream = file.getInputStream() ;
            while((temp = inputStream.read(data)) != -1) {
                output.write(data, 0, temp);
            }
        } catch (Exception e) {
            return null ;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileName ;
    }
    /**
     * 通过资源文件读取相应的key对应的数据内容
     * @param key 要读取资源文件的key名称
     * @param args 进行文本格式化处理的相关参数
     * @return 如果该内容存在则返回处理好的文本，否则返回null
     */
    public String getMessage(String key, String ... args) {
        try {
            return this.messageSource.getMessage(key, args, Locale.getDefault());
        } catch (Exception e) {
            return null ;
        }
    }

    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest() ;
    }
    public HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }
    public HttpSession getSession() {
        return this.getRequest().getSession() ;
    }
    public ServletContext getApplication() {
        return this.getRequest().getServletContext() ;
    }
    @InitBinder
    public void initBinder(WebDataBinder binder) {  // 通过此绑定设置处理转换
        binder.registerCustomEditor(java.util.Date.class,new PropertyEditorSupport(){
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                LocalDate localDate = LocalDate.parse(text,LOCAL_DATE_FORMAT) ; // 设置本地日期实例
                ZoneId zoneId = ZoneId.systemDefault() ;
                Instant instant = localDate.atStartOfDay().atZone(zoneId).toInstant() ;
                super.setValue(java.util.Date.from(instant)); // 字符串与日期转换
            }
        });
    }
}
