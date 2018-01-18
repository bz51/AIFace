package com.chaimm.ai;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.chaimm.ai.entity.Parameter;
import com.chaimm.ai.entity.Result;
import com.chaimm.ai.exception.CommonExp;
import com.chaimm.ai.utils.HttpRequest;
import com.chaimm.ai.utils.ImageTool;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import static com.chaimm.ai.entity.Parameter.*;

/**
 * @author 大闲人柴毛毛
 * @date 2017/12/31 上午11:37
 * @description
 */
@RestController
public class Controller {

//    @GetMapping("/")
    public String wxVerify(String signature, String timestamp, String nonce, String echostr){
        return echostr;
    }

    @GetMapping("getJSTicket")
    public String getJSTicket(){
        getAccessToken();
        getTicket();
        return Parameter.Ticket_Parameter;
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

//    @GetMapping("getAccessToken")
    public String getAccessToken(){
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
        System.out.println("access_token="+access_token);
        return Parameter.AccessToken_Parameters;
    }


    @GetMapping("recognizeFace")
    public Result recognizeFace(String picId, String userToken){

//        String accessToken = getAccessToken();

        String fileName = HttpRequest.downloadByGet("http://file.api.weixin.qq.com/cgi-bin/media/get", "access_token="+Parameter.AccessToken_Parameters+"&media_id="+picId);
        System.out.println(fileName);

        //图片下载失败时：1.提示管理员，2.将pic_id保存至数据库，让管理员手动下载
        if(fileName==null){
            return Result.newFailResult("文件上传失败！");
        }

        try {
            // 识别图片
            JSONObject jsonObject = ImageTool.recognizeFace(URL_PATH+fileName);

            // 绘图
            String paintedFileName = ImageTool.paintImage(jsonObject, new File(Parameter.ABS_PATH+fileName));

            // 老用户直接返回结果
//            System.out.println("userToken="+userToken);
//            String resultURL = Parameter.userResultMap.get(userToken);
//            System.out.println("resultURL="+resultURL);
//            if (resultURL != null && !resultURL.equals("")) {
//                return buildResult(jsonObject, paintedFileName, resultURL);
//            }

            // TODO 生成分析结果
            String resultURL = createResultURL(userToken, jsonObject);

            // 构造返回结果
            return buildResult(jsonObject, paintedFileName, resultURL);
        } catch (CommonExp e) {
            e.printStackTrace();
            System.out.println(Result.newFailResult(e.getMessage()).toString());
            return Result.newFailResult(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(Result.newFailResult("服务器被挤爆了！请稍后重试").toString());
            return Result.newFailResult("服务器被挤爆了！请稍后重试");
        }


    }

    private String createResultURL(String userToken, JSONObject jsonObject) throws IOException {

        String dirName = buildDirName(jsonObject);
        List<String> fileNameList_nengli = Parameter.resultMap_nengli.get(dirName);
        List<String> fileNameList_yanzhi = Parameter.resultMap_yanzhi.get(dirName);

        String fileName_yanzhi = fileNameList_yanzhi.get(new Random().nextInt(fileNameList_yanzhi.size()));
        String fileName_nengli = fileNameList_nengli.get(new Random().nextInt(fileNameList_nengli.size()));

        // TODO 对dirName要进行编码
        String resultURL = Parameter.URL_PATH+"result/yanzhi/"+dirName+"/"+fileName_yanzhi + "," +
                Parameter.URL_PATH+"result/nengli/"+dirName+"/"+fileName_nengli;

//        Parameter.userResultMap.put(userToken, resultURL);
//        BufferedWriter buf_w = new BufferedWriter(new FileWriter(new File(Parameter.userFilePath),true));
//        buf_w.write(userToken+"#"+resultURL);
//        buf_w.newLine();
//        buf_w.close();

        return resultURL;
    }

    private String buildDirName(JSONObject jsonObject) {
        StringBuilder dirName = new StringBuilder();

        Integer gender = (Integer) jsonObject.getJSONArray("gender").get(0);
        Integer age = (Integer) jsonObject.getJSONArray("age").get(0);

        if (gender == 0) {
            dirName.append("nv_");
        } else {
            dirName.append("na_");
        }

        if (age<=15) {
            dirName.append("0_15");
        } else if (age>15 && age<=20) {
            dirName.append("15_20");
        } else if (age>20 && age<=30) {
            dirName.append("20_30");
        } else if (age>30 && age<=40) {
            dirName.append("30_40");
        } else if (age>40) {
            dirName.append("40_");
        }

        return dirName.toString();
    }

    private Result buildResult(JSONObject jsonObject, String paintedFileName, String resultURL) {
        Integer gender = (Integer) jsonObject.getJSONArray("gender").get(0);
        Integer age = (Integer) jsonObject.getJSONArray("age").get(0);
        Integer expression = (Integer) jsonObject.getJSONArray("expression").get(0);
        Integer glass = (Integer) jsonObject.getJSONArray("glass").get(0);

        Result result = new Result();
        result.setSuccess(true);

        if (gender.intValue() == 0) {
            result.setGender("女生");
        } else {
            result.setGender("男生");
        }

        if (glass.intValue() == 0) {
            result.setGlass("未戴眼镜");
        } else {
            result.setGlass("有眼镜");
        }

        result.setAge(age.intValue());

        if (expression.intValue() == 1) {
            result.setExpression("微笑");
        } else {
            result.setExpression("没有微笑");
        }

        result.setFaceUrl(Parameter.URL_PATH +"ai/"+ paintedFileName);
        result.setResultUrl(resultURL);
        System.out.println(result.toString());
        return result;
    }

    public static void main(String[] args) {
        try {
            // 识别图片
            JSONObject jsonObject = ImageTool.recognizeFace("http://www.chaimm.com:8080/upload/1515062968");

            // 绘图
            File file = new File("/Users/chibozhou/Downloads/WechatIMG4.jpeg");
            String paintedFileName = ImageTool.paintImage(jsonObject, file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
