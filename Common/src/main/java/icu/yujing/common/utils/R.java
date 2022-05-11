package icu.yujing.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R<T> extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;
    public static String CODE = "code";
    public static String Message = "msg";
    // 响应状态码

    public R() {
        put(CODE, 200);
        put(Message, "success");
    }

    public static R error() {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put(CODE, code);
        r.put(Message, msg);
        return r;
    }

    public static R ok(String msg) {
        R r = new R();
        r.put(Message, msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R();
    }

    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public T getJsonObject(String key, TypeReference<T> typeReference) {
        return JSON.parseObject(JSON.toJSONString(super.get(key)), typeReference);
    }

    public <k> k getDataOfJsonObject(TypeReference<k> typeReference) {
        return JSON.parseObject(JSON.toJSONString(super.get("data")), typeReference);
    }

    public R putData(Object value) {
        this.put("data", value);
        return this;
    }

    public Object getData() {
        return this.get("data");
    }

    public Integer getCode() {
        return (Integer) super.get(CODE);
    }

    public void setCode(Integer code) {
        super.put(CODE, code);
    }
}
