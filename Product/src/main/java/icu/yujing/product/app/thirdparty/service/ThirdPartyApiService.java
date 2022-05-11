package icu.yujing.product.app.thirdparty.service;

import icu.yujing.common.utils.R;
import icu.yujing.product.app.product.entity.to.VerificationCodeTo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
public interface ThirdPartyApiService {
    R sendTheVerificationCode(VerificationCodeTo verificationCodeTo);

    void uploadFile(MultipartFile file, String path) throws IOException;
}
