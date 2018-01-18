package com.chaimm.ai.entity;

/**
 * @author 大闲人柴毛毛
 * @date 2017/12/31 上午10:03
 * @description
 */
public class Result {
    private boolean isSuccess;
    private String message;
    private String faceUrl;
    private String resultUrl;
    private int age;
    private String gender;
    private String glass;
    private String expression;

    public static Result newFailResult(String errorMsg){
        Result result = new Result();
        result.isSuccess = false;
        result.setMessage(errorMsg);
        return result;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    public void setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGlass() {
        return glass;
    }

    public void setGlass(String glass) {
        this.glass = glass;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "Result{" +
                "isSuccess=" + isSuccess +
                ", message='" + message + '\'' +
                ", faceUrl='" + faceUrl + '\'' +
                ", resultUrl='" + resultUrl + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", glass='" + glass + '\'' +
                ", expression='" + expression + '\'' +
                '}';
    }
}
