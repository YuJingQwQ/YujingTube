package icu.yujing.product.app.thirdparty.controller;

import icu.yujing.common.properties.AliOssProperties;
import icu.yujing.common.utils.R;
import icu.yujing.product.app.thirdparty.service.ThirdPartyApiService;
import icu.yujing.product.app.product.entity.to.VerificationCodeTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/api/thirdparty")
public class ThirdPartyApiController {

    @Autowired
    private ThirdPartyApiService thirdPartyApiService;

    /**
     * 必须使用application/json形式发送请求
     *
     * @param verificationCodeTo
     * @return
     */
    @PostMapping("/send/verification/code")
    public R sendTheVerificationCode(@RequestBody VerificationCodeTo verificationCodeTo) {
        return thirdPartyApiService.sendTheVerificationCode(verificationCodeTo);
    }

    @PostMapping(value = "/file/simple/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public R uploadFile(@RequestPart("file") MultipartFile file, @RequestParam("path") String path) throws IOException {
        thirdPartyApiService.uploadFile(file, path);

        return R.ok().putData(AliOssProperties.aliOssAddress + path);
    }

}
