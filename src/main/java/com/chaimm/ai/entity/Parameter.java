package com.chaimm.ai.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 大闲人柴毛毛
 * @date 2017/12/31 下午1:56
 * @description
 */
public class Parameter {
    /** access_token */
    public static String AccessToken_Parameters = null;
    /** ticket */
    public static String Ticket_Parameter = null;

    public static final String ABS_PATH = "/usr/web/tomcat/webapps/upload/";
//    public static final String ABS_PATH = "/Users/chibozhou/Downloads/";
    public static final String URL_PATH = "http://www.chaimm.com/upload/";

//    public static final String APPID = "wxf2f80142a242f384";
//    public static final String SECRET = "5f4307f21a18bdfe74bea682627bfd53";
    public static final String APPID = "wx82de919618ed3240";
    public static final String SECRET = "43e3037e403c0d267e2d190aa3953cbd";

    public static final Map<String,String> userResultMap = new HashMap<>();

    public static final String userFilePath = "/usr/web/userFile";

    public static final Map<String, List<String>> resultMap_yanzhi = new HashMap<>();
    public static final Map<String, List<String>> resultMap_nengli = new HashMap<>();

    public static final String resultPath_yanzhi = "/usr/web/tomcat/webapps/upload/result/yanzhi/";
    public static final String resultPath_nengli = "/usr/web/tomcat/webapps/upload/result/nengli/";
//    public static final String resultPath_yanzhi = "/Users/chibozhou/百度云同步盘/project\\(1\\)/柴毛毛公众号/result/yanzhi/";
//    public static final String resultPath_nengli = "/Users/chibozhou/百度云同步盘/project\\(1\\)/柴毛毛公众号/result/nengli/";
}
