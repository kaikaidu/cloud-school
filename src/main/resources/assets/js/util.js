function isEmpty(item){
	if(item == null || item.trim() == ''){
		return true;
	}
	return false;
}

//判断是否为数字
function IsNum(s)
{
    if (s!=null && s!="")
    {
        return !isNaN(s);
    }
    return false;
}

function IsUrl (str_url) { 
	if (isEmpty(str_url)) {
		return false;
	}
	var strRegex = '^((https|http|ftp|rtsp|mms)?://)'
			+ '?(([0-9a-z_!~*\'().&=+$%-]+: )?[0-9a-z_!~*\'().&=+$%-]+@)?' // ftp的user@
			+ '(([0-9]{1,3}.){3}[0-9]{1,3}' // IP形式的URL- 199.194.52.184
			+ '|' // 允许IP和DOMAIN（域名）
			+ '([0-9a-z_!~*\'()-]+.)*' // 域名- www.
			+ '([0-9a-z][0-9a-z-]{0,61})?[0-9a-z].' // 二级域名
			+ '[a-z]{2,6})' // first level domain- .com or .museum
			+ '(:[0-9]{1,4})?' // 端口- :80
			+ '((/?)|' // a slash isn't required if there is no file name
			+ '(/[0-9a-z_!~*\'().;?:@&=+$,%#-]+)+/?)$';
	var re = new RegExp(strRegex);
	if (re.test(str_url)) {
		return (true);
	} else {
		return (false);
	}
}

//校验邮箱
function isEmail(email) {
    return email.search(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/) != -1;
}

//获得年月日      得到日期oTime
function msecToDate(str){
    var oDate = new Date(str),
        oYear = oDate.getFullYear(),
        oMonth = oDate.getMonth()+1,
        oDay = oDate.getDate(),
        oHour = oDate.getHours(),
        oMin = oDate.getMinutes(),
        oSen = oDate.getSeconds(),
        oTime = oYear +'-'+ getzf(oMonth) +'-'+ getzf(oDay) +' '+ getzf(oHour) +':'+ getzf(oMin) +':'+getzf(oSen);//最后拼接时间
    return oTime;
}

//获得年月日 时分
function msecToDate2(str){
    var oDate = new Date(str),
        oYear = oDate.getFullYear(),
        oMonth = oDate.getMonth()+1,
        oDay = oDate.getDate(),
        oHour = oDate.getHours(),
        oMin = oDate.getMinutes(),
        oTime = oYear +'-'+ getzf(oMonth) +'-'+ getzf(oDay) +' '+ getzf(oHour) +':'+ getzf(oMin);//最后拼接时间
    return oTime;
}
//补0操作
function getzf(num){
    if(parseInt(num) < 10){
        num = '0'+num;
    }
    return num;
}

//校验纯数字
function isNumber(str) {
    var reg = /^\d*$/;
    if (!reg.test(str)) {
        return false;
    }
    return true;
}

//校验手机号
function isPhone(str) {
    var reg=/(^1[3|4|5|7|8|9]\d{9}$)|(^09\d{8}$)/;
    return reg.test(str);
}

// 数字1、2、3、4转换为A、B、C、D、E
function numToChar(i){
    if(i >= 1 && i <= 26){
        return String.fromCharCode(65 + (i-1));
    } else {
        layer.msg('请输入1到26的整数');
        return '';
    }
}

// 数字A、B、C、D、E转换为1、2、3、4
function charToNum(i){
    if(i >= 'A' && i <= 'Z'){
        return i.charCodeAt(0) - 65 + 1;
    } else {
        layer.msg('请输入A到Z的字母');
        return '';
    }
}
//校验是否包含中文
function isChina(str) {
    if(/.*[\u4e00-\u9fa5]+.*$/.test(str)){
        return false;
    }
    return true;
}