// 自适应程序
var zidingyi_height;
$(document).ready(function(){
 var header=$(".content-header").offset().top+$(".content-header").height()
 var footer=$(".main-footer").offset().top
 zidingyi_height=footer-header;
 // console.log(zidingyi_height);
 $("#topicClassDiv").css("height",zidingyi_height*0.85+"px");
 $("#topicAddDiv").css("height",zidingyi_height*0.15+"px");
 $("#topicInfoDiv").css("height",zidingyi_height*0.64+"px");
 $("#InfoChart").css("height",zidingyi_height*0.5+"px");
 $("#topicAllDiv").css("height",zidingyi_height*0.85+"px");
           
})

var nowOperateSubject;
var nowOperateTopic;

var app=angular.module('myApp',[]);
app.controller('myCon',function($scope,$http){
    $http.get(ip+'/domain/getDomains').success(function(response){
        data = response.data
        //console.log(getCookie("NowTopic"));
        $scope.subjects=data;
        $scope.chooseSubject(getCookie("NowClass"));
        $scope.getTopicInfo(getCookie("NowTopic"));
        $scope.detailCount();
    });
    

    $scope.chooseSubject=function(a){
       // console.log(a);
        $(".subjectName").css("background","none");
        $(".subjectName").css("color","black");
        $("#choseClass").text(a+"所有主题");
        $("#classAddTopic").text(a+"添加主题");
        //$("#"+a).css("background","steelblue");
        //$("#"+a).css("color","white");
        nowOperateSubject=a;

        $http({
            method:'GET',
            url:ip+"/topic/getTopicsByDomainName",
            params:{domainName:a}
        }).then(function successCallback(response){
            $scope.topics=response.data.data;
        }, function errorCallback(response){

        });


        // $.ajax({
        //      type: "GET",
        //      url: ip+"/DomainTopicAPI/getDomainTerm",
        //      data: {ClassName:a},
        //      dataType: "json",
        //      async:false,
        //      success: function(data){
        //                  $scope.topics=data;
        //               }
        //  });
    }

    $scope.getTopicInfo=function(a){
        if(a === undefined){
            console.log("请选择主题");
        } else {
            //console.log(a);
            nowOperateTopic=a;
            $("#choseTopic").text(a+"信息");

            $http({
                method:'GET',
                url:ip+"/topic/getTopicInformationByDomainNameAndTopicName",
                params:{domainName:nowOperateSubject,topicName:a}
            }).then(function successCallback(response){
                response = response["data"];
                $("#topic_name").text(response.data.topicName);
                $("#topic_facet_num").text(response.data.facetNumber);
            }, function errorCallback(response){

            });

            // $.ajax({
            //      type: "GET",
            //      url: ip+"/DomainTopicAPI/getDomainTermInfo",
            //      data: {ClassName:nowOperateSubject,TermName:a},
            //      dataType: "json",
            //      async:false,
            //      success: function(data){
            //                  $("#topic_name").text(data[0].TermName);
            //                  $("#topic_facet_num").text(data[0].FacetNum);
            //               }
            //  });
        }
    };

    $scope.addTopic=function(){
        var newtopic=$("#newtopicname").val();

        $http({
            method:'GET',
            url:ip+"/topic/insertTopicByNameAndDomainName",
            params:{domainName:nowOperateSubject,topicName:$("input[name='TopicName']").val().replace(/\s/g, "")}
        }).then(function successCallback(response){
            alert(response.data.data);
            $scope.chooseSubject(nowOperateSubject);
        }, function errorCallback(response){

        });

    // $.ajax({
    //          type: "GET",
    //          url: ip+"/DomainTopicAPI/createTopic",
    //          data: {ClassName:nowOperateSubject,TopicName:$("input[name='TopicName']").val()},
    //          dataType: "json",
    //          success: function(data){
    //                      alert(data.success);
    //                      $scope.chooseSubject(nowOperateSubject);
    //                   }
    //      });
    


    }

    $scope.deleteTopic=function(){

        $http({
            method:'GET',
            url:ip+"/topic/deleteTopicByNameAndDomainName",
            params:{domainName:nowOperateSubject,topicName:nowOperateTopic}
        }).then(function successCallback(response){
            response = response["data"];
            alert(nowOperateTopic+response.data);
            $scope.chooseSubject(nowOperateSubject);
        }, function errorCallback(response){

        });


        // $.ajax({
        //      type: "GET",
        //      url: ip+"/DomainTopicAPI/deleteTermName",
        //      data: {ClassName:nowOperateSubject,TermName:nowOperateTopic},
        //      dataType: "json",
        //      async:false,
        //      success: function(data){
        //                  alert(data.success);
        //                  $scope.chooseSubject(nowOperateSubject);
        //               }
        //  });
    }

    $scope.updateTopicName=function(){

        $http({
            method:'GET',
            url:ip+"/DomainTopicAPI/updateTermName",
            params:{ClassName:nowOperateSubject,TermName:nowOperateTopic,NewTermName:$("input[name='NewTopicName']").val().replace(/\s/g, "")}
        }).then(function successCallback(response){
            alert(response.data.success);
            $scope.chooseSubject(nowOperateSubject);
        }, function errorCallback(response){

        });

        // $.ajax({
        //      type: "GET",
        //      url: ip+"/DomainTopicAPI/updateTermName",
        //      data: {ClassName:nowOperateSubject,TermName:nowOperateTopic,NewTermName:$("input[name='NewTopicName']").val()},
        //      dataType: "json",
        //      success: function(data){
        //                  alert(data.success);
        //                  $scope.chooseSubject(nowOperateSubject);
        //               }
        //  });
    };

    $scope.detailCount=function(){

        $http({
            method:'GET',
            url:ip+"/topic/getTopicInformationByDomainNameAndTopicName",
            params:{domainName:nowOperateSubject,topicName:nowOperateTopic}
        }).then(function successCallback(response){
            response = response["data"];
            var myChartFacet = echarts.init(document.getElementById("InfoChart"));

            optionFacet = {
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
                                data:[response.data.firstLayerFacetNumber, response.data.secondLayerFacetNumber,response.data.thirdLayerFacetNumber]
                            }
                            ]
                        };
                        myChartFacet.setOption(optionFacet);
        }, function errorCallback(response){

        });


        // $.ajax({
        //      type: "GET",
        //      url: ip+"/DomainTopicAPI/getDomainTermInfo",
        //      data: {ClassName:nowOperateSubject,TermName:nowOperateTopic},
        //      dataType: "json",
        //      success: function(data){

        //                  var myChartFacet = echarts.init(document.getElementById("InfoChart"));

        //                  optionFacet = {
        //                     color: ['steelblue'],
        //                     tooltip : {
        //                         trigger: 'axis',
        //                         axisPointer : {            
        //                             type : 'shadow'        
        //                         }
        //                     },
        //                     grid: {
        //                         left: '3%',
        //                         right: '4%',
        //                         bottom: '3%',
        //                         containLabel: true
        //                     },
        //                     xAxis : [
        //                     {
        //                         data : ['一级分面', '二级分面', '三级分面'],
        //                         axisTick: {
        //                             alignWithLabel: true
        //                         }
        //                     }
        //                     ],
        //                     yAxis : [
        //                     {
        //                         type : 'value',
        //                         min:0
        //                     }
        //                     ],
        //                     series : [
        //                     {
        //                         name:'数量',
        //                         type:'bar',
        //                         barWidth: '60%',
        //                         data:[data[0].FirstLayerFacetNum, data[0].SecondLayerFacetNum,data[0].ThirdLayerFacetNum]
        //                     }
        //                     ]
        //                 };
        //                 myChartFacet.setOption(optionFacet);
        //               }
        //  });
    }

    $scope.jumpFacet=function(){
        setCookie("NowClass",nowOperateSubject,"d900");
        setCookie("NowTopic",nowOperateTopic,"d900");
        window.location="../facet/index.html";
    }

    
});