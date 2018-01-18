package com.chaimm.ai.init;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.chaimm.ai.entity.Parameter;
import com.chaimm.ai.utils.AESDecode;
import com.chaimm.ai.utils.HttpRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

import static com.chaimm.ai.entity.Parameter.APPID;
import static com.chaimm.ai.entity.Parameter.SECRET;
import static com.chaimm.ai.entity.Parameter.userResultMap;

/**
 * @author 大闲人柴毛毛
 * @date 2017/12/31 下午1:49
 * @description
 */
@Component
public class InitToken implements CommandLineRunner {

    @Override
    public void run(String... strings) throws Exception {
        //定时线程1:每隔1.5时获取一次access_token5400000
//        getAccess_token(300000);

        //定时线程1:每隔1.5时获取一次ticket
//        getTicket(5400000);

        // 加载用户信息
//        loadUser();

        // 加载分析结果
        loadResults();
    }

    private void loadResults() {
        // 加载"颜值"的分析结果
        loadYanzhiResults();
        // 加载"能力"的分析结果
        loadNengliResults();

        System.out.println(JSONObject.toJSON(Parameter.resultMap_yanzhi).toString());
        System.out.println(JSONObject.toJSON(Parameter.resultMap_nengli).toString());
    }

    private void loadNengliResults() {
        Map<String, List<String>> results = loadFiles(Parameter.resultPath_nengli);
        Parameter.resultMap_nengli.putAll(results);
    }

    private void loadYanzhiResults() {
        Map<String, List<String>> results = loadFiles(Parameter.resultPath_yanzhi);
        Parameter.resultMap_yanzhi.putAll(results);
    }

    private Map<String, List<String>> loadFiles(String resultMap_yanzhi) {
        Map<String, List<String>> results = new HashMap<>();

        File dir = new File(resultMap_yanzhi);
        File[] fileDirs = dir.listFiles();
        if (fileDirs!=null && fileDirs.length>0) {
            for (File fileDir : fileDirs) {
                String[] fileNames = fileDir.list();
                if (fileNames!=null && fileNames.length>0) {
                    List<String> fileNameList = Arrays.asList(fileNames);
                    results.put(fileDir.getName(), fileNameList);
                }
            }
        }
        return results;
    }

    private void loadUser() throws IOException {
        BufferedReader buf_r = new BufferedReader(new FileReader(new File(Parameter.userFilePath)));
        String line = null;
        userResultMap.clear();
        while ((line=buf_r.readLine()) != null) {
            String[] results = line.split("#");
            Parameter.userResultMap.put(results[0],results[1]);
        }
        buf_r.close();

        System.out.println(JSONObject.toJSON(Parameter.userResultMap).toString());
    }


    /**
     * 定时线程：获取access_token
     */
    private boolean getAccess_token(long time){
        if(time<=0) {
            return false;
        }

        Timer timer = new Timer();
        TimerTask task =new TimerTask(){
            public void run(){
                //获取access_token
                String param = "grant_type=client_credential&appid="+APPID+"&secret="+SECRET;
                String access_token_result = HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/token", param);
                String access_token = "";
                try {
                    access_token = JSONObject.parseObject(access_token_result).getString("access_token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Parameter.AccessToken_Parameters = access_token;
                System.out.println("获取到的access_token="+access_token);
//                System.out.println("Parameter中的access_token="+Parameter.AccessToken_Parameters);

                getTicket();
            }
        };
        timer.scheduleAtFixedRate(task, new Date(),time);//当前时间开始起动 每次间隔n秒再启动
        return true;
    }

    private void getTicket() {
        //获取ticket
        String param = "access_token="+ Parameter.AccessToken_Parameters+"&type=jsapi";
        String ticket_result = HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/ticket/getticket", param);
        String ticket = "";
        try {
            ticket = JSONObject.parseObject(ticket_result).getString("ticket");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Parameter.Ticket_Parameter = ticket;
        System.out.println("获取到的ticket="+ticket);
//                System.out.println("Parameter中的ticket="+Parameter.Ticket_Parameter);
    }


    /**
     * 定时线程：获取ticket
     */
    private boolean getTicket(long time){
        if(time<=0) {
            return false;
        }

        Timer timer = new Timer();
        TimerTask task =new TimerTask(){
            public void run(){
                //获取ticket
                String param = "access_token="+ Parameter.AccessToken_Parameters+"&type=jsapi";
                String ticket_result = HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/ticket/getticket", param);
                String ticket = "";
                try {
                    ticket = JSONObject.parseObject(ticket_result).getString("ticket");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Parameter.Ticket_Parameter = ticket;
                System.out.println("获取到的ticket="+ticket);
//                System.out.println("Parameter中的ticket="+Parameter.Ticket_Parameter);
            }
        };
        timer.scheduleAtFixedRate(task, 10000,time);//当前时间开始起动 每次间隔n秒再启动
        return true;
    }

}
