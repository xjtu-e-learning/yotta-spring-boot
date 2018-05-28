
function getDate(){
    var date = new Date(),
    nowYear = date.getFullYear(),
    nowMonth = date.getMonth() + 1,  //注意getMonth从0开始，getDay()也是(此时0代表星期日)
    nowDay = date.getDate(),
    nowHour = date.getHours(),
    nowMinute = date.getMinutes(),
    nowSecond = date.getSeconds(),
    weekday = ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"],
    nowWeek = weekday[date.getDay()];
    return nowYear + '年' + nowMonth + '月' + nowDay + '日' + nowHour + '时' + nowMinute + '分' + nowSecond + '秒-' + nowWeek;
}
$(document).ready(function() {

    console.log(cip)
    console.log(cname)
    console.log(getDate())


    $('#login').click(function() {
        //登录验证
         $.ajax({
            type: "post",
            url: ip + "/user/login",
            data: {userName:$("#u").val(),password:$("#p").val(),ip:cip,place:cname,date:getDate()},
            dataType: 'json',
            contentType:'application/x-www-form-urlencoded',
            success: function(result) {
                if (result["code"]==200) {
                    //写cookie，记录
                    var userinfo={};
                    userinfo.userName=$("#u").val();
                    userinfo.password=$("#p").val();
                    userinfo=JSON.stringify(userinfo);
                    setCookie("userinfo",userinfo ,  "d1"); 
                    alert("点击确定将自动跳转，无跳转请自己打开原来的页面")

                    window.location.href=document.referrer;
                } else {
                    alert("用户名或密码错误！")
                } 
            },
            error:function(result) {
                console.log(result);
                    alert("服务器响应失败，请联系管理员！")
            }
        });

    });

});



// setCookie("name","hayden");
// alert(getCookie("name"));
//如果需要设定自定义过期时间
//那么把上面的setCookie　函数换成下面两个函数就ok;
//程序代码
function setCookie(name,value,time)
{
    var strsec = getsec(time);
    var exp = new Date();
    exp.setTime(exp.getTime() + strsec*1);
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString()+";path=/";
}
function getsec(str)
{
    // alert(str);
    var str1=str.substring(1,str.length)*1;
    var str2=str.substring(0,1);
    if (str2=="s")
    {
        return str1*1000;
    }
    else if (str2=="h")
    {
        return str1*60*60*1000;
    }
    else if (str2=="d")
    {
        return str1*24*60*60*1000;
    }
}
//这是有设定过期时间的使用示例：
//s20是代表20秒
//h是指小时，如12小时则是：h12
//d是天数，30天则：d30
// setCookie("name","hayden","s20");
// 
function getCookie(name)
{
var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
if(arr=document.cookie.match(reg))
return unescape(arr[2]);
else
return "";
}