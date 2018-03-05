package com.mmall.common;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * 服务响应对象<泛型类型/>
 * @param <T>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)  //JsonSerialize表示 序列化处理  内容物：不要空的字段 null的对象key会消失
public class ServerResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    /*
        构造函数
     */
    private ServerResponse(int status){
        this.status = status;
    }

    private ServerResponse(int status,T data){
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status, String msg , T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }


    @JsonIgnore   //此注解JsonIgnore表示不会被Json序列化
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode(); //status == 0
    }

    /*
    对外只提供get
     */

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    /*
    只传输状态码
     */
    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T> (ResponseCode.SUCCESS.getCode());
    }
    /*
    传输状态码和消息
     */
    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T> (ResponseCode.SUCCESS.getCode(),msg);
    }
    /*
    传输状态码和数据
     */
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    /*
    传输状态码、消息和数据
     */
    public static <T> ServerResponse<T> createBySuccess(String msg ,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg ,data);
    }


    /*
    错误时返回
     */
    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int erroeCode , String errorMessage){
        return new ServerResponse<T>(erroeCode ,errorMessage);
    }




}
