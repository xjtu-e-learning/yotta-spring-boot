text_1='领域术语是指特定领域的专业概念，具有领域特色；从维基百科、百度百科等高质量数据源中抽取出具有多分面特性的领域术语作为知识主题，为后续的分面树和知识森林构建提供支撑。'
text_0='为了缓解碎片化知识SOD三特性引发的问题，我们课题组提出了一种知识聚合新模式——“知识森林”，将多源、片面、无序的碎片化知识聚合成符合人类认知学习特点的森林结构。知识森林概念层次为“知识主题——主题分面树——知识森林”。'
text_2=' 一个知识主题相关内容的知识涉及多个维度，每个维度称为一个分面（如堆栈的存储结构），分面及其从属关系构成树状结构称为主题分面树。主题分面树有助于形成对特定知识主题各个分面的全方位展示，避免碎片化知识的内容片面性导致的认知片面与偏差问题。'
text_3='挖掘出知识主题间的因果、参考、对比等认知关系，为用户规划一条由认知关系组成的认知路径，实现导航学习。'
text_4=' 以中文开放域知识源为对象，对多源分布的碎片化知识进行爬取，并通过碎片化质量分析抽取高质量的知识碎片。'
text_5='将不同分面的碎片化知识装配到主题分面树对应的叶节点上，避免碎片化知识物理上分散引发的认知过载问题。'
text_6='知识森林是“分面聚合+主题导航”的新型知识聚合模式。由实例化的主题分面树通过主题间认知关系联结而成，如同显示森林中树之间的路径。'
text=[text_0,text_1,text_2,text_3,text_4,text_5,text_6]


var app = angular.module('myApp', [ ]);

app.controller('menu', function($scope, $http) {
 
    var img=$("#ImgNavigation")
    var step=getCookie("stepNumber")
    if (step=='') {
        step=1;
        setCookie("stepNumber",1,"d900")
    }
    img.attr('width', "");
    img.attr('src', 'dist/img/daohang.jpg');
    img.load(function(){
        // img.addClass('img-responsive')
        setImgArea()
        display(0)
    })

    // 获取学科和课程数据
    $http.get(ip+"/domain/getDomainsGroupBySubject").success(
        function(response) { 
            //响应response相对，增加状态信息和编码
            data = response["data"];
            $scope.subjects = response["data"];

            var classSum = 0;
            // 切回导航页面时，读取现有课程并更新两个框的值
            for(i = 0; i < data.length; i++) {
                classSum = classSum + data[i].domains.length;
                if(data[i].subjectName == getCookie("NowSubject")) {
                    $scope.subject = data[i];
                    for(j = 0; j < data[i].domains.length; j++) {
                        if(data[i].domains[j].domainName == getCookie("NowClass")) {
                            $scope.domain = data[i].domains[j];
                        }
                    }
                }
            }
            $scope.subjectNum = data.length;
            $scope.classSum = classSum;
        }
    );

    $scope.change = function(){  
        //获取被选中的值  
        var chengeitem = $scope.domain.domainName;
        setCookie("NowClass", $scope.domain.domainName, "d900");
        setCookie("NowSubject", $scope.subject.subjectName, "d900");
        //js代码实现option值变化后的查询等操作      
    } 

})
function resetCookie(){
    setCookie("stepNumber",1,"s900")
    display(0)
    // console.log(getCookie("stepNumber"))
     // window.location.reload();
}


 // <!-- 设置图片 点击事件  跳转到不同的地方 -->
 function pageto(number){
    imagehide(number)
     if (number==0) {
        // setCookie("stepNumber",number+1,"d900")
        // setImgSrc()

        return 
    }
    else if (number==1) {
        // setCookie("stepNumber",number+1,"d900")
        // setImgSrc()

        window.location= "pages/extraction/index.html"
    }else if (number==2) {
        // setCookie("stepNumber",number+1,"d900")
        // setImgSrc()
       window.location= "pages/facet/index.html"
    }
    else if (number==3) {
        // setCookie("stepNumber",number+1,"d900")
        // setImgSrc()
        window.location= "pages/relationship/index.html"
    }
    else if (number==4) {
        // setCookie("stepNumber",number+1,"d900")
        // setImgSrc()
        window.location= "pages/spider/index.html"
    }else if (number==5) {
        // setCookie("stepNumber",number+1,"d900")
        // setImgSrc()
        window.location= "pages/add/index.html"
    }
    else if (number==6) {
        // setCookie("stepNumber",number,"d900")
        // setImgSrc()
       window.location= "pages/kg/index.html"
    }
 }

function display(i){
    // var i=getCookie("stepNumber");
    setCookie("stepNumber",i,"d900")
    $("#text").html(text[i])
    if (i==0) {
        $('#image').attr('src',"")
        return
    }
    $('#image').attr('src',"dist/img/tishi/step"+i+".png")
}

function imagehide(i){
    if (i==0) {
         $('#imagehref').hide();
    } else {
        $('#imagehref').show();
    }
}



 // <!-- 设置图片热区 -->
function setImgArea(){
    var img=$("#ImgNavigation")

    var area1=$('#area1')
    area1.attr("coords",""+397/1948*img.width()+","+409/520*img.height()+","+98/1948*img.width())
    var area2=$('#area2')
    area2.attr("coords",""+831/1948*img.width()+","+405/520*img.height()+","+98/1948*img.width())
    var area3=$('#area3')
    area3.attr("coords",""+1257/1948*img.width()+","+401/520*img.height()+","+98/1948*img.width())
    var area4=$('#area4')
    area4.attr("coords",""+839/1948*img.width()+","+105/520*img.height()+","+98/1948*img.width())
    var area5=$('#area5')
    area5.attr("coords",""+1257/1948*img.width()+","+101/520*img.height()+","+98/1948*img.width())
    var area6=$('#area6')
    area6.attr("coords",""+1743/1948*img.width()+","+295/520*img.height()+","+98/1948*img.width())
};

$(document).ready(function(){
  
});
