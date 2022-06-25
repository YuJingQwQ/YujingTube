const yujjingUtils = {
    checkFileSize: (fileSize, limit, unit) => {
        //TODO 抛出自定义异常
        if (fileSize == null || fileSize == 0) {
            throw new FileNotEmptyException("文件为空");
        }
        let result = true;
        if ("MB" === unit.toUpperCase()) {
            if (fileSize / 1024 / 1024 > limit)
                result = false;
        } else if ("KB" === unit.toUpperCase()) {
            if (fileSize / 1024 > limit)
                result = false;
        } else if ("BYTE" === unit.toUpperCase()) {
            if (fileSize > limit)
                result = false;
        } else if ("GB" === unit.toUpperCase()) {
            if (fileSize / 1024 / 1024 / 1024 > limit)
                result = false;
        } else {
            throw new RuntimeException("文件单位异常");
        }
        return result;
    },
    checkFileSuffix: (fileName, validSuffixes) => {
        let suffix = fileName.substr(fileName.lastIndexOf(".") + 1);
        return validSuffixes.includes(suffix);
    }, dateFilter(value) {
        let uploadingDate = new Date(value);
        let currentDate = new Date();

        //TODO 此日期算法待改进,比如视频上传日期是今年2月26,现在日期是今年3月1,那么比较月份为true,将显示一个月前,但实际天数没有一个月
        // 比较年份
        if (currentDate.getFullYear() != uploadingDate.getFullYear()) {
            return (
                Math.abs(uploadingDate.getFullYear() - currentDate.getFullYear()) +
                "年前"
            );
        } else {
            // 比较月份
            if (currentDate.getMonth() != uploadingDate.getMonth()) {
                return (
                    Math.abs(uploadingDate.getFullYear() - currentDate.getFullYear()) +
                    "个月前"
                );
            } else {
                // 比较天数
                if (currentDate.getDate() != uploadingDate.getDate()) {
                    return (
                        Math.abs(uploadingDate.getDate() - currentDate.getDate()) + "天前"
                    );
                } else {
                    // 比较小时
                    if (currentDate.getHours() != uploadingDate.getHours()) {
                        return (
                            Math.abs(uploadingDate.getHours() - currentDate.getHours()) +
                            "小时前"
                        );
                    } else {
                        // 比较分钟
                        if (currentDate.getMinutes() != uploadingDate.getMinutes()) {
                            return (
                                Math.abs(
                                    uploadingDate.getMinutes() - currentDate.getMinutes()
                                ) + "分钟前"
                            );
                        } else {
                            return "0分钟前";
                        }
                    }
                }
            }
        }
    },
}


export default yujjingUtils