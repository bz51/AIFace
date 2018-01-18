package com.chaimm.ai.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaimm.ai.entity.Parameter;
import com.chaimm.ai.exception.CommonExp;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;

/**
 * @author 大闲人柴毛毛
 * @date 2017/12/30 下午10:31
 * @description
 */
public class ImageTool {

    public static void main(String[] args) throws Exception {
//        //读取图片文件，得到BufferedImage对象
//        BufferedImage bimg= ImageIO.read(new FileInputStream("/Users/chibozhou/Downloads/WechatIMG4.jpeg"));
//        //得到Graphics2D 对象
//        Graphics2D g2d=(Graphics2D)bimg.getGraphics();
//        //设置颜色和画笔粗细
//        g2d.setColor(Color.RED);
//        //绘制图案或文字
//        g2d.drawString("文本", 0, 0);
//        g2d.drawRect(215, 420, 330, 510);
//        //保存新图片
//        ImageIO.write(bimg, "JPG",new FileOutputStream("/Users/chibozhou/Downloads/aaa.jpg.jpeg"));

        ImageTool.recognizeFace("http://101.132.66.131:8080/upload/aaa.jpg.jpeg");
    }

    /**
     * 解析人脸图片
     * @param url 人脸图片URL
     * @return 解析结果
     */
    public static JSONObject recognizeFace(String url) throws Exception {
        // 请求人脸识别接口
        String result = AESDecode.sendPost("http://dtplus-cn-shanghai.data.aliyuncs.com/face/attribute","{\"type\":0,\"image_url\":\""+url+"\"}");

        // 解析结果
        JSONObject json = JSONObject.parseObject(result);

//        System.out.println(json.toJSONString());
        // 判断是否存在人脸
        Integer face_num = json.getInteger("face_num");
        if (face_num == 0) {
            throw new CommonExp("未监测到人脸");
        }


        // 返回结果
        return json;
    }

    /**
     * 图像上绘点
     * @param json
     * @param file
     * @return 文件名
     */
    public static String paintImage(JSONObject json, File file) throws IOException {
        JSONArray faceRectArray = json.getJSONArray("face_rect");
        JSONArray landmarkArray = json.getJSONArray("landmark");

        // 读取图片文件，得到BufferedImage对象
        BufferedImage bimg= ImageIO.read(new FileInputStream(file));
        //得到Graphics2D 对象
        Graphics2D g2d=(Graphics2D)bimg.getGraphics();
        //设置颜色和画笔粗细
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke( 2.0f ));

        //绘制 人脸框
        Integer x = (Integer) faceRectArray.get(0);
        Integer y = (Integer) faceRectArray.get(1);
        Integer width = (Integer) faceRectArray.get(2);
        Integer height = (Integer) faceRectArray.get(3);
        g2d.drawRect(x.intValue(), y.intValue(), width.intValue(), height.intValue());

        // 绘制 特征点
        for (int i=0; i<105; i++) {
            BigDecimal pointX = (BigDecimal) landmarkArray.get(i*2);
            BigDecimal pointY = (BigDecimal) landmarkArray.get(i*2+1);
            g2d.drawString(".", pointX.intValue(), pointY.intValue());
            g2d.drawRect(pointX.intValue(), pointY.intValue(), 3,3);
        }

        //保存新图片
        File resultFile = new File(Parameter.ABS_PATH+"ai/"+System.currentTimeMillis()/1000);
        ImageIO.write(bimg, "JPG",new FileOutputStream(resultFile));
        return resultFile.getName();
    }
}
