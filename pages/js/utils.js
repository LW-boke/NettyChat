let Utils = function () {
    /**
     * 时间格式化
     * @param {格式类型} fmt 
     * @param {时间} date 
     */
    let dateFormat = (fmt, date) => {
        let ret;
        let opt = {
            "Y+": date.getFullYear().toString(), // 年
            "m+": (date.getMonth() + 1).toString(), // 月
            "d+": date.getDate().toString(), // 日
            "H+": date.getHours().toString(), // 时
            "M+": date.getMinutes().toString(), // 分
            "S+": date.getSeconds().toString() // 秒
            // 有其他格式化字符需求可以继续添加，必须转化成字符串
        };
        for (let k in opt) {
            ret = new RegExp("(" + k + ")").exec(fmt);
            if (ret) {
                fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
            };
        };
        return fmt;
    }

    return {
        simpleDateFormat: (date) => {
            return dateFormat("YYYY-mm-dd HH:MM:SS", date);
        },
        handlerDateFormat: (fmt, date) => {
            return dateFormat(fmt,date);
        }
    }

}();