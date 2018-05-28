// 自适应程序
var zidingyi_height
$(document).ready(function(){
    var header=$(".content-header").offset().top+$(".content-header").height();
    var footer=$(".main-footer").offset().top;
    zidingyi_height=footer-header;
    $("#div1").css("height",zidingyi_height*0.9+"px");
    $("#div2").css("height",zidingyi_height*0.9+"px");
    $("#div3").css("height",zidingyi_height*0.8+"px");
    $("#RightfacetTree").css("height",zidingyi_height*0.85+"px");
    // console.log(zidingyi_height);
    // console.log($("#div1").css("height"));

})


var app=angular.module('facetTreeApp',[]);
app.controller("facetTreeController",function($scope,$http){
    console.log('当前学科为：' + getCookie("NowSubject") + '，课程为：' + getCookie("NowClass"));
    $scope.NowSubject = getCookie("NowSubject");
    $scope.NowClass = getCookie("NowClass");
    $http.get(ip+'/topic/getTopicsByDomainName?domainName='+getCookie("NowClass")).success(function(data){
        response = data["data"]

        $scope.topics=response;
        // 默认加载显示
        $scope.fenmianshow(response[0].topicName);
        $scope.Branch();
    });
    $scope.subjectName="字符串";
    $scope.treeFlag="trunk";
    $scope.fenmianshow=function(subjectName){
        // console.log(subjectName);
        $scope.subjectName=subjectName;
        $.ajax({
            type:"GET",
            url:ip+"/facet/getSecondLayerFacetGroupByFirstLayerFacet?domainName="+getCookie("NowClass")+"&topicName="+subjectName,
            data:{},
            dataType:"json",
            async:false,
            success:function(data){
                $scope.facets=data["data"];
                // console.log(data);
            }
        });

    }
    $scope.setBranch=function(){
         $scope.treeFlag="branch";
         $("#all-build-state").html("全部生成成功！");
    }
    $scope.BuildTrunkorBranch=function(){
        if($scope.treeFlag==="trunk"){
            ObtainTrunk($scope.subjectName);
        }
        else{
            $.ajax({
                type: "POST",
                url: ip+"/topic/getCompleteTopicByNameAndDomainNameWithHasFragment",
                data: $.param( {
                    domainName:getCookie("NowClass"),
                    topicName:$scope.subjectName,
                    hasFragment:false
                }),
                headers:{'Content-Type': 'application/x-www-form-urlencoded'},
                // type: "GET",
                //     url: ip+"/AssembleAPI/getTreeByTopicForFragment",
                //     data: {
                //     ClassName:getCookie("NowClass"),
                //     TermName:$scope.subjectName
                // },
                // dataType: "json",
                success: function(response){
                    data = response['data'];
                    DisplayBranch(data);
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    console.log(textStatus);
                }
            });
        }
    }
    $scope.Branch=function(){
        $.ajax({
            type: "POST",
            url: ip+"/topic/getCompleteTopicByNameAndDomainNameWithHasFragment",
            data: $.param( {
                domainName:getCookie("NowClass"),
                topicName:$scope.subjectName,
                hasFragment:false
            }),
            headers:{'Content-Type': 'application/x-www-form-urlencoded'},
            // type: "GET",
            //     url: ip+"/AssembleAPI/getTreeByTopicForFragment",
            //     data: {
            //     ClassName:getCookie("NowClass"),
            //     TermName:$scope.subjectName
            // },
            // dataType: "json",
            success: function(response){
                data = response['data'];
                DisplayBranch(data);
            },
            error:function(XMLHttpRequest, textStatus, errorThrown){
                //通常情况下textStatus和errorThrown只有其中一个包含信息
                console.log(textStatus);
            }
        });
    }
});

function important(div){
    $(".top").css("background","white");
    $(".top").css("color","black");
    // console.log(div);
    div.style.background="#d9edf7";
    div.style.color="red";

    // $(".top").css("background","white");
    // $(".top").css("color","black");
    // console.log(div);
    // div.style.background="#428bca";
    // div.style.color="white";

    var fenmian=document.getElementsByClassName("fenmian");
    // console.log(fenmian.length);
    for(var i=0;i<fenmian.length;i++){
        var div=fenmian[i].getElementsByTagName("div");
        // console.log(div.length);
        if(div.length==0){
            fenmian[i].style.display="none";
        }
    }
}

