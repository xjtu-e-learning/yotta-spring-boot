// 自适应程序
var zidingyi_height;
$(document).ready(function(){
    var header=$(".content-header").offset().top+$(".content-header").height()
    var footer=$(".main-footer").offset().top
    zidingyi_height=footer-header;
    //  var header=$(".content-header").height();
    // var mainheader=$(".main-header").height();
    // var footer=$(".main-footer").height();
    // zidingyi_height=window.innerHeight-footer-header-mainheader;
    // console.log(zidingyi_height);
    $("#classNumDiv").css("height",zidingyi_height*0.07+"px");
    $("#classInfoDiv").css("height",zidingyi_height*0.9+"px");
    $("#classAddDiv").css("height",zidingyi_height*0.1+"px");
    $("#classQueryDiv").css("height",zidingyi_height*0.74+"px");
    $("#Select_result").css("height",zidingyi_height*0.58+"px");
})





var app=angular.module('myApp',[]);
app.controller('myCon',function($scope,$http){
    $http.post(ip+'/statistics/getDomainDistribution').success(function(response){
        data = response["data"];
        $scope.kechengs=data;
    });
    $http.get(ip+'/domain/countDomains').success(function(response){
        $('#ClassNum').text("系统中目前共有"+response.data.domainNumber+"门课程");
    });

    $scope.openModal=function(a){
        //console.log(a);
        nowOperateClass=a;
    }

 $scope.styleFuc=function(a){
    var length=(parseInt(a)/20).toFixed(2)+"%";
       // console.log(length);
        return {
            "width":length
        }
    }

    $scope.showClass=function(){
        $http({
            method:'POST',
            url:ip+"/statistics/getDomainDistribution"
        }).then(function successCallback(response){
            response = response["data"];
            console.log(response);
            $scope.kechengs=response.data;
        }, function errorCallback(response){

        });
    
    }

    $scope.showNum=function(){

        $http.get(ip+'/domain/countDomains').success(function(response){
            $('#ClassNum').text("系统中目前共有"+response.data.domainNumber+"门课程");
        });

    }

    $scope.tianjiaClass=function(){

    var newclass=$("#newclass").val();

    $http({
            method:'GET',
            url:ip+"/domain/insertDomain",
            params:{domainName:$("input[name='ClassName']").val()}
        }).then(function successCallback(response){
            alert(response.data.data);
            $scope.showClass();
            $scope.showNum();
        }, function errorCallback(response){

        });
        // console.log(newclass);
    }

    $scope.updataClassName=function(){
        $http({
            method:'GET',
            url:ip+"/domain/updateDomainByDomainName",
            params:{oldDomainName:nowOperateClass,newDomainName:$("input[name='NewClassName']").val()}
        }).then(function successCallback(response){
            response = response["data"];
            alert(response.data);
            $scope.showClass();
        }, function errorCallback(response){

        });
    }

    $scope.queryByKeyword=function(){
        $http({
            method:'GET',
            url:ip+"/statistics/queryKeyword",
            params:{keyword:$("input[name='KeyWord']").val()}
        }).then(function successCallback(response){
            response = response["data"];
            var subjectArray=[];
            var topicArray=[];
            var facetArray=[];
            for(var i=0;i<response.data.length;i++){
                if(response.data[i].type=="domain"){

                    subjectArray.push(response.data[i]);
                }
                else if(response.data[i].type=="topic"){
                    topicArray.push(response.data[i]);
                }
                else{
                    facetArray.push(response.data[i]);
                }
            }

            $scope.querysubjects=subjectArray;
            $scope.querytopics=topicArray;
            $scope.queryfacets=facetArray;
        }, function errorCallback(response){

        });
    }

    $scope.jumpClass=function(a){
       // console.log(a);
        var res=[];
        $http({
            method:'POST',
            url:ip+"/statistics/getDomainDistribution"
        }).then(function successCallback(response){
            response = response["data"];
            for(var i=0;i<response.data.length;i++){
                if(response.data[i].domainName==a){
                    res.push(response.data[i]);
                }
            }
            $scope.kechengs=res;
        }, function errorCallback(data){

        });

}

$scope.jumpTopic=function(a,b){
    setCookie("NowClass",a,"d900");
    setCookie("NowTopic",b,"d900");
    window.location="../topic/index.html";
}

$scope.jumpFacet=function(a,b,c,d){
    setCookie("NowClass",a,"d900");
    setCookie("NowTopic",b,"d900");
    setCookie("NowFacet",c,"d900");
    setCookie("NowFacetLayer",d,"d900");
    window.location="../facet/index.html";
}

    



    $scope.getDetailInfo=function(a){
        var nowOperateClass;
        var nowFirstFacetNum;
        var nowSecondFacetNum;
        var nowThirdFacetNum;
        // console.log(a);
        $http({
            method:'POST',
            url:ip+"/statistics/getDomainDistribution"
        }).then(function successCallback(response){
            response = response["data"];
            for(var i=0;i<response.data.length;i++){
                if(response.data[i].domainName==a){
                    nowOperateClass=a;
                    nowFirstFacetNum=response.data[i].firstLayerFacetNumber;
                    nowSecondFacetNum=response.data[i].secondLayerFacetNumber;
                    nowThirdFacetNum=response.data[i].thirdLayerFacetNumber;
                    
                    $("#"+a+"modal").modal();


                    var myChartFacet = echarts.init(document.getElementById(a+"FacetInfo"));

                    optionFacet = {

                        title : {
                            text:a+'各级分面统计结果',
                            x:'center'
                        },

                        color: ['steelblue'],
                        tooltip : {
                            trigger: 'axis',
                            axisPointer : {            
                                type : 'shadow'        
                            }
                        },
                        grid: {
                            left: '3%',
                            right: '4%',
                            bottom: '3%',
                            containLabel: true
                        },
                        xAxis : [
                        {
            //type : 'category',
            data : ['一级分面', '二级分面', '三级分面'],
            axisTick: {
                alignWithLabel: true
            }
        }
        ],
        yAxis : [
        {
            type : 'value',
            min:0
        }
        ],
        series : [
        {
            name:'数量',
            type:'bar',
            barWidth: '60%',
            data:[nowFirstFacetNum, nowSecondFacetNum,nowThirdFacetNum]
        }
        ]
    };
        // 使用刚指定的配置项和数据显示图表。
        myChartFacet.setOption(optionFacet);
                }
            }
        }, function errorCallback(response){

        });
    
}

    
});

