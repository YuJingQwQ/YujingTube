package icu.yujing.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import icu.yujing.common.constant.ExceptionContent;
import icu.yujing.common.constant.UserModuleConstant;
import icu.yujing.common.exception.MyTopException;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
public class YujingUtils {

//    public static UserPo getUserFromJwt(HttpServletRequest request){
//    }

    /**
     * 将obj转换成json字符串再解析json字符串得到泛型T对象
     *
     * @param obj
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T getObjectFromJson(Object obj, TypeReference<T> typeReference) {
        return JSON.parseObject(JSON.toJSONString(obj), typeReference);
    }

    /**
     * @param size     要检验的数据,单位:byte
     * @param limit    允许的最大值
     * @param sizeUnit size和limit的单位
     * @return
     */
    public static boolean checkFileSize(Long size, Long limit, SizeUnit sizeUnit) {
        if ("MB".equals(sizeUnit.UNIT)) {
            return limit >= size / 1024L / 1024L;
        } else if ("KB".equals(sizeUnit.UNIT)) {
            return limit >= size / 1024L;
        } else if ("BYTE".equals(sizeUnit.UNIT)) {
            return limit >= size;
        } else if ("GB".equals(sizeUnit.UNIT)) {
            return limit >= size / 1024L / 1024L / 1024L;
        } else {
            // TB
            return limit >= size / 1024L / 1024L / 1024L / 1024L;
        }
    }

    /**
     * @param size     要检验的数据,单位:byte
     * @param limit    允许的最大值
     * @param sizeUnit size和limit的单位
     * @return
     */
    public static void checkFileSizeThrowException(Long size, Long limit, SizeUnit sizeUnit) {
        if ("MB".equals(sizeUnit.UNIT)) {
            if (limit < size / 1024L / 1024L) {
                throw new MyTopException(ExceptionContent.WRONG_FILE_SIZE.getCode(), "文件大小限制" + limit + "MB");
            }
        } else if ("KB".equals(sizeUnit.UNIT)) {
            if (limit < size / 1024L) {
                throw new MyTopException(ExceptionContent.WRONG_FILE_SIZE.getCode(), "文件大小限制" + limit + "KB");
            }
        } else if ("BYTE".equals(sizeUnit.UNIT)) {
            if (limit < size) {
                throw new MyTopException(ExceptionContent.WRONG_FILE_SIZE.getCode(), "文件大小限制" + limit + "BYTE");
            }
        } else if ("GB".equals(sizeUnit.UNIT)) {
            if (limit < size / 1024L / 1024L / 1024L) {
                throw new MyTopException(ExceptionContent.WRONG_FILE_SIZE.getCode(), "文件大小限制" + limit + "GB");
            }
        } else {
            // TB
            if (limit < size / 1024L / 1024L / 1024L / 1024L) {
                throw new MyTopException(ExceptionContent.WRONG_FILE_SIZE.getCode(), "文件大小限制" + limit + "TB");
            }
        }
    }

    public static boolean checkFileType(String filename, List<String> allowedTypes) {
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        for (String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(suffix)) {
                return true;
            }
        }
        return false;
    }

    public static void checkFileTypeThrowException(String filename, List<String> allowedTypes) {
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        boolean safe = false;
        for (String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(suffix)) {
                safe = true;
                break;
            }
        }
        if (!safe) {
            StringBuilder builder = new StringBuilder(20);
            builder.append("文件类型只支持").append(allowedTypes);
            throw new MyTopException(ExceptionContent.WRONG_FILE_TYPE.getCode(), builder.toString());
        }

    }

    /**
     * Date: 当前日期(yyyy-MM-dd)
     * suffix:文件后缀名(.jpg .png等等)
     * 最终结果 prefix/Date/UUID.suffix
     *
     * @param prefix
     * @param filename
     * @return
     */
    public static String getPath(String prefix, String filename) {

        String randomFilename = getRandomFilename(filename);
        StringBuilder pathBuilder = new StringBuilder(100);
        if (prefix.startsWith("/")) {
            pathBuilder.append(prefix.substring(1));
        } else {
            pathBuilder.append(prefix);
        }

        if (prefix.endsWith("/")) {
            pathBuilder.append(LocalDate.now().format(UserModuleConstant.OSS_TIME_FORMATTER));
        } else {
            pathBuilder.append("/").append(LocalDate.now().format(UserModuleConstant.OSS_TIME_FORMATTER));
        }

        pathBuilder.append("/").append(randomFilename);
        return pathBuilder.toString();
    }

    public static String getRandomFilename(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1) {
            throw new MyTopException(ExceptionContent.WRONG_FILE_TYPE.getCode(),
                    ExceptionContent.WRONG_FILE_TYPE.getMessage());
        }

        String suffix = filename.substring(dotIndex);
        if (suffix.length() == 1) {
            throw new MyTopException(ExceptionContent.WRONG_FILE_TYPE.getCode(),
                    ExceptionContent.WRONG_FILE_TYPE.getMessage());
        }

        return UUID.randomUUID().toString() + suffix;
    }

    public static void createPath(String path) {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

    }

    public static File createFile(String dirPath, String filename) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dirPath + filename);

    }

    public enum SizeUnit {
        /**
         * 字节
         */
        BYTE("BYTE"),
        /**
         * 千字节
         */
        KB("KB"),
        /**
         * 兆字节
         */
        MB("MB"),
        /**
         * 1024兆字节
         */
        GB("GB");
        public final String UNIT;

        SizeUnit(String unit) {
            this.UNIT = unit;
        }
    }


}
