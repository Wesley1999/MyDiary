/**
 * 将整个map转换为url参数，依赖parseParameter()函数
 * @param paramsMap 键值对形式的参数
 * @returns {string} 格式：key1=value1&key2=value2，返回值可以直接直接或为http请求的参数（GET或POST均可）
 */
function parseParameterByMap(paramsMap) {
    let data = "";
    if (typeof(paramsMap) == "undefined") {
        return data;
    }
    for (let [key, value] of paramsMap) {
        data = parseParameter(data, key, value);
    }
    return data;
}

/**
 * 转换为url参数
 * @param data 已有的数据，可以为空，或者是上次调用此函数的返回值
 * @param key 参数名
 * @param value 参数值
 * @returns {string} 格式：key1=value1&key2=value2，返回值可以直接直接或为http请求的参数（GET或POST均可）
 */
function parseParameter(data, key, value) {
    if (key == null) {
        alert("key不能为null");
    }
    if (value == null || value === "" || typeof(value) == "undefined") {
        return data;
    }
    // 对value编码
    value = encodeURIComponent(value);
    if (data == null || data === "") {
        return key + "=" + value;
    } else {
        return data + "&" + key + "=" + value;
    }
}

function dateToString(date) {
    let diaryTimeString = date.getFullYear() + "年" + (date.getMonth()+1) + "月" + date.getDate() + "日 星期"

    switch (date.getDay()) {
        case 0: diaryTimeString += "天"; break;
        case 1: diaryTimeString += "一"; break;
        case 2: diaryTimeString += "二"; break;
        case 3: diaryTimeString += "三"; break;
        case 4: diaryTimeString += "四"; break;
        case 5: diaryTimeString += "五"; break;
        case 6: diaryTimeString += "六"; break;
    }
    diaryTimeString += " ";
    if (date.getHours() < 10) {
        diaryTimeString += "0";
    }
    diaryTimeString += date.getHours() + ":"
    if (date.getMinutes() < 10) {
        diaryTimeString += "0";
    }
    diaryTimeString += date.getMinutes();
    return diaryTimeString;
}

function wordsCountFunction(str){
    sLen = 0;
    try{
        //先将回车换行符做特殊处理
        str = str.replace(/(\r\n+|\s+|　+)/g,"龘");
        //处理英文字符数字，连续字母、数字、英文符号视为一个单词
        str = str.replace(/[\x00-\xff]/g,"m");
        //合并字符m，连续字母、数字、英文符号视为一个单词
        str = str.replace(/m+/g,"*");
        //去掉回车换行符
        str = str.replace(/龘+/g,"");
        //返回字数
        sLen = str.length;
    }catch(e){

    }
    return sLen;
}

/**压缩数据**/

function compressData(data){

    var cData;
    cData= encodeURIComponent(data);
    cData= unescape(cData);
    cData= window.btoa(cData);
    return cData;
}

/**解压数据**/

function decompressData(data){

    var cData;
    cData= window.atob(data);
    cData= escape(cData);
    cData= decodeURIComponent(cData);
    return cData;
}

String.prototype.replaceAll = function(s1,s2){
    return this.replace(new RegExp(s1,"gm"),s2);
}

Date.prototype.format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}