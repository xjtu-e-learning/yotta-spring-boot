// 自适应程序
var zidingyi_height;
$(document).ready(function(){
 var header=$(".content-header").offset().top+$(".content-header").height()
 var footer=$(".main-footer").offset().top
 zidingyi_height=footer-header;
 // console.log(zidingyi_height);
 $("#fragmentClassDiv").css("height",zidingyi_height*0.85+"px");
 $("#fragmentUnaddDiv").css("height",zidingyi_height*0.4+"px");
 $("#fragmentInfoDiv").css("height",zidingyi_height*0.4+"px");
})

var editor = new wangEditor('wang');
editor.config.uploadImgUrl = ip+'/SpiderAPI/createImageFragment';
editor.config.uploadImgFileName="imageContent";
editor.config.hideLinkImg = true;
editor.create();

var nowOperateClass;
var nowOperateTopic;
var nowOperateFacet1;
var nowOperateFacet2;

var modify_add_flag;
var now_modify_id;
var username=getCookie('userinfo').slice(getCookie('userinfo').indexOf(':')+2,getCookie('userinfo').indexOf(',')-1);

function choosetype(){
    $("#fragmentModal").modal();
    modify_add_flag=0;
}

var app=angular.module('myApp',[
    'ui.bootstrap','ngDraggable'
    ]);
app.controller('myCon',function($scope,$http,$sce){
    $http.get(ip+'/domain/getDomains').success(function(response){
        response = response["data"];
        $scope.subjects=response;
        // console.log(nowOperateClass);
        $("#class_name").text(nowOperateClass);
        if(getCookie("NowFacetLayer")==1){
            $scope.getfacet1fragment(getCookie("NowClass"),getCookie("NowTopic"),getCookie("NowFacet"));

        }else if(getCookie("NowFacetLayer")==2){
            $scope.getfacet2fragment(getCookie("NowClass"),getCookie("NowTopic"),getCookie("NowFacet1"),getCookie("NowFacet"));

        }else{
            $scope.getfacet3(getCookie("NowClass"),getCookie("NowTopic"),getCookie("NowFacet"));

        }
    });

    $http.get(ip+'/assemble/getTemporaryAssemblesByUserName',{params:{"userName":username}}).success(function(response){
        response = response["data"];
        $scope.unaddfragments=response;
        $scope.getTopic(getCookie("NowClass"));
        for(var i=0;i<$scope.unaddfragments.length;i++){
            $scope.unaddfragments[i].assembleContent=$sce.trustAsHtml($scope.unaddfragments[i].assembleContent);
        }
    });

    $scope.isCollapsed = true;
    $scope.isCollapsedchildren = true;
    $scope.isCollapsedchildren2=true;

    $scope.getUnaddFragment=function(){
      $http.get(ip+'/assemble/getTemporaryAssemblesByUserName',{params:{"userName":username}}).success(function(response){
        response = response["data"];
        $scope.unaddfragments=response;
        for(var i=0;i<$scope.unaddfragments.length;i++){
            $scope.unaddfragments[i].assembleContent=$sce.trustAsHtml($scope.unaddfragments[i].assembleContent);
        }
    });  
    }


    $scope.dropFacetFragment=function(data,evt){
        var str=$("#fragmenttopic").text();
        var arr=str.split(" ");
        if((arr.length!=3)||(arr[1]=="")||(arr[0]=="主题")){
            alert("添加无效");
        }
        else if(arr[0]=="一级分面"){
            // console.log("1"+arr[1]);

            $http({
                method:'POST',
                url:ip+"/assemble/insertAssemble",
                params:{domainName:nowOperateClass
                    ,topicName:nowOperateTopic
                    ,facetName:arr[1]
                    ,facetLayer:1
                    ,temporaryAssembleId:data.assembleId}
            }).then(function successCallback(response){
                response = response["data"];
                alert("添加碎片成功");
                $scope.getfacet1fragment(nowOperateClass,nowOperateTopic,arr[1]);
                $scope.getUnaddFragment();
            }, function errorCallback(response){

            });

        }
        else if(arr[0]=="二级分面"){
            // console.log("2"+arr[1]);

            $http({
                method:'POST',
                url:ip+"/assemble/insertAssemble",
                params:{domainName:nowOperateClass
                    ,topicName:nowOperateTopic
                    ,facetName:arr[1]
                    ,facetLayer:2
                    ,temporaryAssembleId:data.assembleId}
            }).then(function successCallback(response){
                response = response["data"];
                alert("添加碎片成功");
                $scope.getfacet2fragment(nowOperateClass,nowOperateTopic,arr[1]);
                $scope.getUnaddFragment();
            }, function errorCallback(response){

            });
        }
        else if(arr[0]=="三级分面"){
            // console.log("3"+arr[1]);

            $http({
                method:'POST',
                url:ip+"/assemble/insertAssemble",
                params:{domainName:nowOperateClass
                    ,topicName:nowOperateTopic
                    ,facetName:arr[1]
                    ,facetLayer:3
                    ,temporaryAssembleId:data.assembleId}
            }).then(function successCallback(response){
                response = response["data"];
                alert("添加碎片成功");
                $scope.getfacet3(nowOperateClass,nowOperateTopic,arr[1]);
                $scope.getUnaddFragment();
            }, function errorCallback(response){

            });
        }
        else{
            alert("添加无效");
        }
    }
    $scope.dragFragment=function(data,evt){
        // console.log("success");
    }


    $scope.addFrag=function(){
        
        
        var html = editor.$txt.html() + "";
        if(modify_add_flag==0){
            console.log("addFragment");
            $http({
                method:'POST',
                url:ip+"/assemble/insertTemporaryAssemble",
                data : $.param({assembleContent : html, userName:username}),
                headers:{'Content-Type': 'application/x-www-form-urlencoded'},
            }).then(function successCallback(response){
                response = response["data"];
                alert("添加碎片成功");
                $scope.getUnaddFragment();
            }, function errorCallback(response){
            // console.log(html);
            alert("添加碎片失败");
        });
        }
        else if(modify_add_flag==1){
            console.log("modifyFragment_"+now_modify_id);
            $http({
                method:'POST',
                url:ip+"/assemble/updateTemporaryAssemble",
                data : $.param({assembleId:now_modify_id,
                                 assembleContent : html
                             }),
                headers:{'Content-Type': 'application/x-www-form-urlencoded'},
            }).then(function successCallback(response){
                response = response["data"];
                alert("更新碎片成功");
                $scope.getUnaddFragment();
            }, function errorCallback(response){
            console.log(response);
            alert("更新碎片失败");
        });
        }

        
    }

    
    //杨宽添加,显示分面树函数
    $scope.showFacetTreeWithLeaves=function(className,subjectName){
        $.ajax({

            type: "POST",
            url: ip+"/topic/getCompleteTopicByNameAndDomainNameWithHasFragment",
            data: $.param( {
                domainName:className,
                topicName:subjectName,
                hasFragment:false
            }),
            headers:{'Content-Type': 'application/x-www-form-urlencoded'},

            // type: "GET",
            // url: ip+"/AssembleAPI/getTreeByTopicForFragment",
            // data: {
            //     ClassName:className,
            //     TermName:subjectName
            // },
            // dataType: "json",
            
            success: function(response){
                dataset = response["data"];
                displayTree(dataset);
            },
            error:function(XMLHttpRequest, textStatus, errorThrown){
                //通常情况下textStatus和errorThrown只有其中一个包含信息
                alert(textStatus);
            }
        });
    }

    $scope.getInfo=function(){
        nowOperateClass=document.getElementById("nameofclass").value;
        $("#class_name").text(nowOperateClass);

        $http({
            method:'GET',
            url:ip+"/domain/getDomainTreeByDomainName",
            params:{domainName:nowOperateClass}
        }).then(function successCallback(response){
            response = response["data"];
            $scope.classInfo=response.data;
        }, function errorCallback(response){

        });
    }




    // $scope.getTerm=function(){
    //     nowOperateClass=document.getElementById("nameofclass").value;

    //     $http({
    //         method:'GET',
    //         url:ip+"/SpiderAPI/getDomainTerm",
    //         params:{ClassName:nowOperateClass}
    //     }).then(function successCallback(response){
    //         for(var i=0;i<response.data.length;i++){

    //             $http({
    //                 method:'GET',
    //                 url:ip+"/DomainTopicAPI/getDomainTermInfo",
    //                 params:{ClassName:nowOperateClass,TermName:response.data[i].TermName}
    //             }).then(function successCallback(response1){
    //                 if(response1.data[0].FacetNum==0){
    //                      $("#"+response1.data[0].TermName+"_a").hide();
    //                  }
    //             }, function errorCallback(response1){

    //             });
    //         }
    //     }, function errorCallback(response){

    //     });
    // }

    $scope.getTopic=function(a){
        nowOperateClass=a;

        $http({
            method:'GET',
            url:ip+"/domain/getDomainTreeByDomainName",
            params:{domainName:nowOperateClass}
        }).then(function successCallback(response){
            response = response["data"];
            $scope.classInfo=response.data;
        }, function errorCallback(response){

        });
    }


    // $scope.gettopichref=function(a,b){
    //     $http({
    //         method:'GET',
    //         url:ip+"/SpiderAPI/getDomainTermFacet1",
    //         params:{ClassName:a,TermName:b}
    //     }).then(function successCallback(response){
    //         for(var i=0;i<response.data.length;i++){

    //             $http({
    //                 method:'GET',
    //                 url:ip+"/FacetAPI/getFacet1Facet2Num",
    //                 params:{ClassName:a,TermName:b,Facet1Name:response.data[i].FacetName}
    //             }).then(function successCallback(response1){
    //                 if(response1.data.Facet2Num==0){
    //                            $("#"+b+"_"+response1.data.Facet1Name+"_a").hide();
    //                                                            }
    //             }, function errorCallback(response1){

    //             });
    //         }
    //     }, function errorCallback(response){

    //     });

        
    // }

    $scope.gettopicfragment=function(a,b){
        nowOperateClass=a;
        nowOperateTopic=b;

        $http({
            method:'GET',
            url:ip+"/assemble/getAssemblesInTopic",
            params:{domainName:a,topicName:b}
        }).then(function successCallback(response){
            response = response["data"];
            $scope.fragments=response.data;
            for(var i=0;i<$scope.fragments.length;i++){
               $scope.fragments[i].assembleContent=$sce.trustAsHtml($scope.fragments[i].assembleContent);
           }
           $("#fragmenttopic").text("主题 "+b+" 下碎片");
           $("#topictree").text("主题 "+b+" 主题树");
        }, function errorCallback(response){

        });
    }

    // $scope.getfacet1href=function(a,b,c){
        
    //     $http({
    //         method:'GET',
    //         url:ip+"/SpiderAPI/getDomainTermFacet2",
    //         params:{ClassName:a,TermName:b,Facet1Name:c}
    //     }).then(function successCallback(response){
    //         if(response.data.length!=0){
    //             for(var i=0;i<response.data.length;i++){

    //             $http({
    //                 method:'GET',
    //                 url:ip+"/FacetAPI/getFacet2Facet3Num",
    //                 params:{ClassName:a,TermName:b,Facet2Name:response.data[i].ChildFacet}
    //             }).then(function successCallback(response1){
    //                 if(response1.data.Facet3Num==0){
    //                            $("#"+b+"_"+c+"_"+response1.data.Facet2Name+"_a").hide();
    //                                                            }
    //             }, function errorCallback(response1){

    //             });
    //         }}else{
    //                 $("#"+b+"_"+c+"_info").remove();
                    
    //             }
    //     }, function errorCallback(response){

    //     });

        
    // }

    $scope.getfacet1fragment=function(a,b,c){
        nowOperateClass=a;
        nowOperateTopic=b;
        nowOperateFacet1=c;

        $http({
            method:'GET',
            url:ip+"/assemble/getAssemblesInFirstLayerFacet",
            params:{domainName:a,topicName:b,firstLayerFacetName:c}
        }).then(function successCallback(response){
            response = response["data"];
            $scope.fragments=response.data;
            for(var i=0;i<$scope.fragments.length;i++){
               $scope.fragments[i].assembleContent=$sce.trustAsHtml($scope.fragments[i].assembleContent);
           }
           $("#fragmenttopic").text("一级分面 "+c+" 下碎片");
           $("#topictree").text("主题 "+b+" 主题树");
        }, function errorCallback(response){

        });
    }

    // $scope.getfacet2href=function(a,b,c,d){
    //     $http({
    //         method:'GET',
    //         url:ip+"/SpiderAPI/getDomainTermFacet3",
    //         params:{ClassName:a,TermName:b,Facet2Name:d}
    //     }).then(function successCallback(response){
    //         if(response.data.length!=0){
    //             }else{
    //                 $("#"+b+"_"+c+"_"+d+"_info").remove();
    //             }
    //     }, function errorCallback(response){

    //     });
    // }
    $scope.getfacet2fragment=function(a,b,c){
        nowOperateClass=a;
        nowOperateTopic=b;
        nowOperateFacet2=c;

        $http({
            method:'GET',
            url:ip+"/assemble/getAssemblesInSecondLayerFacet",
            params:{domainName:a,topicName:b,secondLayerFacetName:c}
        }).then(function successCallback(response){
            response = response["data"];
            $scope.fragments=response.data;
            for(var i=0;i<$scope.fragments.length;i++){
               $scope.fragments[i].assembleContent=$sce.trustAsHtml($scope.fragments[i].assembleContent);
           }
           $("#fragmenttopic").text("二级分面 "+c+" 下碎片");
           $("#topictree").text("主题 "+b+" 主题树");
        }, function errorCallback(response){

        });
    }

    $scope.getfacet3=function(a,b,c){

        $http({
            method:'GET',
            url:ip+"/assemble/getAssemblesInThirdLayerFacet",
            params:{domainName:a,topicName:b,thirdLayerFacetName:c}
        }).then(function successCallback(response){
            response = response["data"];
            $scope.fragments=response.data;
            for(var i=0;i<$scope.fragments.length;i++){
               $scope.fragments[i].assembleContent=$sce.trustAsHtml($scope.fragments[i].assembleContent);
           }
           $("#fragmenttopic").text("三级分面 "+c+" 下碎片");
           $("#topictree").text("主题 "+b+" 主题树");
        }, function errorCallback(response){

        });
    }

    $scope.modifyFragment=function(a){
        modify_add_flag=1;
        now_modify_id=a;
        $("#fragmentModal").modal();

        $http({
            method:'GET',
            url:ip+"/assemble/getTemporaryAssembleById",
            params:{assembleId:a}
        }).then(function successCallback(response){
            response = response["data"];
            // console.log(response.data[0].FragmentContent);
            $("#wang").html(response.data.assembleContent);
        }, function errorCallback(response){

        });
    }

    $scope.deleteUnaddFragment=function(a){
        // console.log(a);

        $http({
            method:'GET',
            url:ip+"/assemble/deleteTemporaryAssemble",
            params:{assembleId:a}
        }).then(function successCallback(response){
            response = response["data"];
            alert(response.data);
        }, function errorCallback(response){

        });
    }

    $scope.deleteFragment=function(a){

        $http({
            method:'GET',
            url:ip+"/assemble/deleteAssemble",
            params:{assembleId:a}
        }).then(function successCallback(response){
            response = response["data"];
            alert(response.data);
        }, function errorCallback(response){

        });
    }
});