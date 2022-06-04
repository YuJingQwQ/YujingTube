package icu.yujing.user.feign;

import icu.yujing.common.utils.R;
import icu.yujing.user.entity.to.VerificationCodeTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Service
@FeignClient("yujingtube-product")
public interface ThirdPartyFeignService {
    @PostMapping("/api/thirdparty/send/verification/code")
    R sendTheVerificationCode(@RequestBody VerificationCodeTo verificationCodeTo);

    @PostMapping(value = "/api/thirdparty/file/simple/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    R uploadFile(@RequestPart("file") MultipartFile file, @RequestParam("path") String path) throws IOException;
}
