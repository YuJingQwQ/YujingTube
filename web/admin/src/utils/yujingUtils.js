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
    }
}


export default yujjingUtils