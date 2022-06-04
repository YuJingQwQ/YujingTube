package icu.yujing.product.app.thirdparty.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import icu.yujing.common.constant.ExceptionContent;
import icu.yujing.common.exception.MyTopException;
import icu.yujing.common.properties.AliOssProperties;
import icu.yujing.common.utils.R;
import icu.yujing.product.app.thirdparty.service.ThirdPartyApiService;
import icu.yujing.product.app.product.entity.to.VerificationCodeTo;
import icu.yujing.product.app.product.entity.vo.SmsResponseVo;
import icu.yujing.product.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Service
public class ThirdPartyApiServiceImpl implements ThirdPartyApiService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private OSS ossClient;

    @Override
    public R sendTheVerificationCode(VerificationCodeTo verificationCodeTo) {
        String host = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "POST";
        String appcode = "e07592d9d3cc4a5cbf6474cab7fec013";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("content", "code:" + verificationCodeTo.getVerificationCode());
        bodys.put("phone_number", verificationCodeTo.getPhone());
        bodys.put("template_id", "TPL_0000");


        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
            String responseString = EntityUtils.toString(response.getEntity());
            System.out.println(responseString);
            SmsResponseVo smsResponseVo = JSON.parseObject(responseString, new TypeReference<SmsResponseVo>() {
            });
            if ("OK".equalsIgnoreCase(smsResponseVo.getStatus())) {
                return R.ok();
            } else if ("DETECTION_FAILED".equalsIgnoreCase(smsResponseVo.getStatus())) {
                return R.error(88888, "短信服务发送失败,请稍后再试");
            } else if ("RATE_LIMIT_EXCEEDED".equalsIgnoreCase(smsResponseVo.getStatus())) {
                return R.error(88888, "短信服务发送频率过高");
            } else if ("INTERNAL_ERROR".equalsIgnoreCase(smsResponseVo.getStatus())) {
                return R.error(88888, "短信服务内部错误");
            } else {
                return R.error(88888, "短信服务未知异常");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return R.error(88888, "短信服务调用失败");

    }

    @Override
    public void uploadFile(MultipartFile file, String path) {

        // 创建PutObjectRequest对象。
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(AliOssProperties.bucketName, path, file.getInputStream());
            ossClient.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new MyTopException(ExceptionContent.FILE_UPLOADING_ERROR.getCode(),
                    ExceptionContent.FILE_UPLOADING_ERROR.getMessage());
        }
    }
}
