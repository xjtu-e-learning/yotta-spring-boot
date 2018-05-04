//点确定键之后的初始化显示函数
function showDivNum(){ 
    var fragmentNum=document.getElementsByClassName("fragment").length;
    // console.log(fragmentNum);
    var count=document.getElementById("numsel").value;//每页显示个数
    var pageNum=Math.ceil(fragmentNum/count);
    // console.log(pageNum);
    $("#page").text(pageNum);
    $("#nowpage").text(1);
    var source=document.getElementById("topsel").value;
    $("#sourcefrom").text(source);
    if(fragmentNum>count){
        for(var i=0;i<count;i++){
             $(".fragment")[i].style.display="block";
         }
         for(var i=count;i<fragmentNum;i++){
            $(".fragment")[i].style.display="none";
         }
    }
    else{
         for(var i=0;i<fragmentNum;i++){
             $(".fragment")[i].style.display="block";
         }
     }

}
//根据页码跳转函数
function go(){
    var gopage=$("#gopage").val();
    var sumpage=$("#page").text();
    // console.log(gopage);
    // console.log(sumpage);
    var fragmentNum=document.getElementsByClassName("fragment").length;
    
    var count=document.getElementById("numsel").value;//每页显示个数
    var index=Math.ceil(fragmentNum/count);
    if(sumpage==1){
        alert("无效页码");
    }
    else{
    if(gopage>index||gopage<0){
        alert("无效页码");
    }
    else{
    $("#nowpage").text(gopage);
    var begin=count*(gopage-1);
    var end=Number(begin)+Number(count);
    // console.log(begin);
    // console.log(end);
    if(begin<fragmentNum&&end<fragmentNum){
        for(var i=0;i<begin;i++)
            $(".fragment")[i].style.display="none";
        for(var i=begin;i<end;i++)
            $(".fragment")[i].style.display="block";
        for(var i=end;i<fragmentNum;i++)
            $(".fragment")[i].style.display="none";
    }
    if(begin<fragmentNum&&end>fragmentNum){
        for(var i=0;i<begin;i++)
            $(".fragment")[i].style.display="none";
        for(var i=begin;i<fragmentNum;i++)
            $(".fragment")[i].style.display="block";
    }
    if(begin>fragmentNum){
        for(var i=0;i<tu;i++)
            $(".fragment")[i].style.display="none";
    }
    
}
}
}
//跳转上一页函数
function pre(){
    var nowpage=$("#nowpage").text();
    // console.log(nowpage);
    var fragmentNum=document.getElementsByClassName("fragment").length;
    // var wen=document.getElementsByClassName("wenbensuipian").length;
    // var tu=document.getElementsByClassName("img").length;
    // var sum=wen>tu?wen:tu;
    var count=document.getElementById("numsel").value;//每页显示个数
    var index=Math.ceil(fragmentNum/count);
    if(nowpage==1){
        alert("当前已经是第一页");
    }
    else{
    var begin=count*(nowpage-2);
    var end=Number(begin)+Number(count);
    if(begin<fragmentNum&&end<fragmentNum){
        for(var i=0;i<begin;i++)
            $(".fragment")[i].style.display="none";
        for(var i=begin;i<end;i++)
            $(".fragment")[i].style.display="block";
        for(var i=end;i<fragmentNum;i++)
            $(".fragment")[i].style.display="none";
    }
    if(begin<fragmentNum&&end>fragmentNum){
        for(var i=0;i<begin;i++)
            $(".fragment")[i].style.display="none";
        for(var i=begin;i<fragmentNum;i++)
            $(".fragment")[i].style.display="block";
    }
    
    nowpage=nowpage-1;
    $("#nowpage").text(nowpage);
    }
}
//跳转下一页函数
function nex(){
    var nowpage=$("#nowpage").text();
    var sumpage=$("#page").text();
    var fragmentNum=document.getElementsByClassName("fragment").length;
    
    var count=document.getElementById("numsel").value;//每页显示个数
    var index=Math.ceil(fragmentNum/count);
     if(nowpage==sumpage){
         alert("当前已经是最后一页");
     }
     else{
    var begin=count*nowpage;
    var end=Number(begin)+Number(count);
    if(begin<fragmentNum&&end<fragmentNum){
        for(var i=0;i<begin;i++)
            $(".fragment")[i].style.display="none";
        for(var i=begin;i<end;i++)
            $(".fragment")[i].style.display="block";
        for(var i=end;i<fragmentNum;i++)
            $(".fragment")[i].style.display="none";
    }
    if(begin<fragmentNum&&end>fragmentNum){
        for(var i=0;i<begin;i++)
            $(".fragment")[i].style.display="none";
        for(var i=begin;i<fragmentNum;i++)
            $(".fragment")[i].style.display="block";
    }
    
    nowpage=Number(nowpage)+1;
    $("#nowpage").text(nowpage);
    }
}
//主题全选函数
function select_all(){
    var inputs=document.getElementsByTagName("input");
    for(var i=0;i<inputs.length;i++){
        if(inputs[i].getAttribute("type")=="checkbox"){
            inputs[i].checked="true";
        }
    }
}

function clear_all(){
    $(":checkbox").attr("checked",false);

}


