//变量
var SUBJECTNAME = "抽象资料型别";


//文本图片碎片栏滚动条设置
$(function() {
    $(".fragmentSlimscroll").slimScroll({
        width: 'auto', //可滚动区域宽度
        height: '500px', //可滚动区域高度
        size: '10px', //组件宽度
        color: '#000', //滚动条颜色
        position: 'right', //组件位置：left/right
        distance: '0px', //组件与侧边之间的距离
        start: 'top', //默认滚动位置：top/bottom
        opacity: .4, //滚动条透明度
        alwaysVisible: true, //是否 始终显示组件
        disableFadeOut: true, //是否 鼠标经过可滚动区域时显示组件，离开时隐藏组件
        railVisible: true, //是否 显示轨道
        railColor: '#333', //轨道颜色
        railOpacity: .2, //轨道透明度
        railDraggable: true, //是否 滚动条可拖动
        railClass: 'slimScrollRail', //轨道div类名 
        barClass: 'slimScrollBar', //滚动条div类名
        wrapperClass: 'slimScrollDiv', //外包div类名
        allowPageScroll: false, //是否 使用滚轮到达顶端/底端时，滚动窗口
        wheelStep: 20, //滚轮滚动量
        touchScrollStep: 200, //滚动量当用户使用手势
        borderRadius: '7px', //滚动条圆角
        railBorderRadius: '7px' //轨道圆角
    });
});


$(function() {
    $(".pictureSlimscroll").slimScroll({
        width: 'auto', //可滚动区域宽度
        height: '130px', //可滚动区域高度
        size: '10px', //组件宽度
        color: '#000', //滚动条颜色
        position: 'right', //组件位置：left/right
        distance: '0px', //组件与侧边之间的距离
        start: 'top', //默认滚动位置：top/bottom
        opacity: .4, //滚动条透明度
        alwaysVisible: true, //是否 始终显示组件
        disableFadeOut: true, //是否 鼠标经过可滚动区域时显示组件，离开时隐藏组件
        railVisible: true, //是否 显示轨道
        railColor: '#333', //轨道颜色
        railOpacity: .2, //轨道透明度
        railDraggable: true, //是否 滚动条可拖动
        railClass: 'slimScrollRail', //轨道div类名 
        barClass: 'slimScrollBar', //滚动条div类名
        wrapperClass: 'slimScrollDiv', //外包div类名
        allowPageScroll: false, //是否 使用滚轮到达顶端/底端时，滚动窗口
        wheelStep: 20, //滚轮滚动量
        touchScrollStep: 200, //滚动量当用户使用手势
        borderRadius: '7px', //滚动条圆角
        railBorderRadius: '7px' //轨道圆角
    });
});

//主题单选框、分面复选框滚动条设置
$(function() {
    $(".model-slimscroll").slimScroll({
        width: 'auto', //可滚动区域宽度
        height: '200px', //可滚动区域高度
        size: '10px', //组件宽度
        color: '#000', //滚动条颜色
        position: 'right', //组件位置：left/right
        distance: '0px', //组件与侧边之间的距离
        start: 'top', //默认滚动位置：top/bottom
        opacity: .4, //滚动条透明度
        alwaysVisible: true, //是否 始终显示组件
        disableFadeOut: true, //是否 鼠标经过可滚动区域时显示组件，离开时隐藏组件
        railVisible: true, //是否 显示轨道
        railColor: '#333', //轨道颜色
        railOpacity: .2, //轨道透明度
        railDraggable: true, //是否 滚动条可拖动
        railClass: 'slimScrollRail', //轨道div类名 
        barClass: 'slimScrollBar', //滚动条div类名
        wrapperClass: 'slimScrollDiv', //外包div类名
        allowPageScroll: false, //是否 使用滚轮到达顶端/底端时，滚动窗口
        wheelStep: 20, //滚轮滚动量
        touchScrollStep: 200, //滚动量当用户使用手势
        borderRadius: '7px', //滚动条圆角
        railBorderRadius: '7px' //轨道圆角
    });
});



/*一、页面加载要做的事*/
//1、选择主题模态框加载
//2、显示第一个主题的树干
//3、右侧文本碎片栏初始化【碎片内容、碎片时间】
//4、右侧图片栏初始化显示标准图片

//1
//添加主题单选框进模态框
function AppendSubjectNameIntoModal(subjectName){
    var div_head='<div class="col-md-4" style="padding:10px;">';
    var div_tail='</div>';
    var input='<input type="radio" name="subject" class="subjectRadio" value='+subjectName+'>'+subjectName;

    var div=div_head+input+div_tail;
    $("#subjectModalBody").append(div);
}

var ykapp = angular.module('subjectApp', []);
ykapp.controller('subjectController', function($scope, $http) {
    
    console.log('当前学科为：' + getCookie("NowSubject") + '，课程为：' + getCookie("NowClass"));
    $scope.NowSubject = getCookie("NowSubject");
    $scope.NowClass = getCookie("NowClass");
    
    $http.get(ip+'/topic/getTopicsByDomainName?domainName='+getCookie("NowClass")).success(function(data){
        response = data["data"]

        $scope.Topics = response;

        //console.log($("#rightDiv").height());
        //console.log($('.box-header').height());
        var height=$("#rightDiv").height()-$('.box-header').height()-8;
        //var height=$(window).height()*0.9;
        $("#facetedTreeDiv").css("height",height*0.93+"px")
        //console.log($(window).height());

        // 每次选择一门新的课程时，展示这门新的课程的第一个主题的分面树
        // console.log(response[0].TermName);
        SUBJECTNAME = response[0].topicName;
        LoadBranch();
    });


});

//2
function DisplayTrunk(dataset){

    document.getElementById("facetedTreeDiv").innerHTML='';
    var datas = []; 
    multiple=1;
    datas.push(dataset);
    //分面树所占空间大小
    svg = d3.select("div#facetedTreeDiv")
                .append("svg")
                .attr("width", "100%")
                .attr("height","100%");
    //分面树的位置
    $("svg").draggable();
    var root_x=$("#facetedTreeDiv").width()/2;
    var root_y=$("#facetedTreeDiv").height()-30;
    var seed4 = {x: root_x* multiple, y: root_y* multiple, name:dataset.name}; 
    var tree4 = buildTree(dataset, seed4, multiple);
    draw_trunk(tree4, seed4, svg, multiple);    
}


function ObtainTrunk(subjectName){
    $.ajax({
        type: "POST",
        url: ip+"/topic/getCompleteTopicByNameAndDomainNameWithHasFragment",
        data: $.param( {
            domainName:getCookie("NowClass"),
            topicName:subjectName,
            hasFragment:false
        }),
        headers:{'Content-Type': 'application/x-www-form-urlencoded'},

        // type: "GET",
        // url: ip+"/AssembleAPI/getTreeByTopicForFragment1",
        // data: {
        //    ClassName:getCookie("NowClass"),
        //    TermName:subjectName,
        //    HasFragment:false
        // },
        // dataType: "json",
        success: function(response){
            data = response.data;
            DisplayTrunk(data);
        },
        error:function(XMLHttpRequest, textStatus, errorThrown){
        //通常情况下textStatus和errorThrown只有其中一个包含信息
            alert(textStatus);
        }
    });
 
}

//二、提交所选主题
//
$(document).ready(function(){
    $("button#subjectSubmit").click(function(){
        //获取被选中主题
        $("input.subjectRadio").each(function(index,value){
            if($(this).prop("checked")===true){
                SUBJECTNAME=$(this).val();
                // console.log($(this));

                //提交主题，获取对应主题的分面及json数据
                //画树干
                //ObtainTrunk(SUBJECTNAME);
                //LoadFacetModal(SUBJECTNAME);
      
                //生成树枝
                LoadBranch();
                //关闭主题模态框
                $("#subjectModal").modal("hide");
                return;
            }
        });
    });
});

function LoadFacetModal(subjectName){
    // 清空分面模态框
    $("#facetModalBody").empty();

    $.ajax({
             type: "GET",
             url: ip+"/FacetAPI/getFacet",
             data: {
                ClassName:getCookie("NowClass"),
                TermName:subjectName,
                FacetLayer:1
             },
             dataType: "json",
             success: function(data){
                        $.each(data,function(index,value){
                            AppendFacetNameIntoModal(value.facetName);
                        });
                     },
             error:function(XMLHttpRequest, textStatus, errorThrown){
                    //通常情况下textStatus和errorThrown只有其中一个包含信息
                    alert(textStatus);
                    }
        });
}

//添加分面复选框进模态框
function AppendFacetNameIntoModal(facetName){
    var div_head='<div class="col-md-3" style="padding:5px;">';
    var div_tail='</div>';
    var input='<input type="checkbox" class="facetCheckbox" checked="true" value='+facetName+'>'+facetName;

    var div=div_head+input+div_tail;
    $("#facetModalBody").append(div);
}


//三、提交分面选择信息
//


//分面复选框的全选
$(document).ready(function(){
    $("button#selectAll").click(function(){
        $("input.facetCheckbox").each(function(){
            $(this).prop("checked",true);
        }); 
    });
});

$(document).ready(function(){
    var facetList=[];
    $("button#facetSubmit").click(function(){
        //获取被选中主题
        $("input.facetCheckbox").each(function(index,value){
            if($(this).prop("checked")===true){
                facetList.push($(this).val());

                //提交所选分面，获取对应的json数据
                //生成树枝
                LoadBranch();

                //关闭主题模态框
                $("#facetModal").modal("hide");
            }
        });
        // console.log(facetList);
    });
});

function LoadBranch(){
    $.ajax({
        type: "POST",
        url: ip+"/topic/getCompleteTopicByNameAndDomainNameWithHasFragment",
        data: $.param( {
            domainName:getCookie("NowClass"),
            topicName:SUBJECTNAME,
            hasFragment:false
        }),
        headers:{'Content-Type': 'application/x-www-form-urlencoded'},

        // type: "GET",
        // url: ip+"/AssembleAPI/getTreeByTopicForFragment1",
        // data: {
        //    ClassName:getCookie("NowClass"),
        //    TermName:SUBJECTNAME,
        //    HasFragment:false
        // },
        // dataType: "json",
        success: function(response){
            data = response.data;
            DisplayBranch(data);
        },
        error:function(XMLHttpRequest, textStatus, errorThrown){
        //通常情况下textStatus和errorThrown只有其中一个包含信息
            alert(textStatus);
        }
    });
}

function DisplayBranch(dataset){
    document.getElementById("facetedTreeDiv").innerHTML='';
    var datas = []; 
    multiple=1;
    datas.push(dataset);
    //分面树所占空间大小
    svg = d3.select("div#facetedTreeDiv")
                .append("svg")
                .attr("width", "100%")
                .attr("height","100%");
    //分面树的位置    
    var root_x=$("#facetedTreeDiv").width()/2;
    var root_y=$("#facetedTreeDiv").height()-30; //
    //$("svg").draggable();
    var seed = {x: root_x, y: root_y, name:dataset.topicName}; 
    var tree = buildBranch(dataset, seed, multiple);
    draw_tree(tree, seed, svg, multiple);
        /*****************************************************/
    //对分面树进行缩放
    $("div#facetedTreeDiv").bind('mousewheel', function(evt) {
        var temp = multiple;//判断是保持0.25或者1.25不变
        if( 0.3< multiple && multiple<1){
            multiple+=evt.originalEvent.wheelDelta/5000;
        }else if(multiple < 0.3){
            if(evt.originalEvent.wheelDelta>0){
                multiple+=evt.originalEvent.wheelDelta/5000;
            }
        }else{
            if(evt.originalEvent.wheelDelta<0){
                multiple+=evt.originalEvent.wheelDelta/5000;
            }
        }
        d3.selectAll("svg").remove(); //删除之前的svg
        svg = d3.select("div#facetedTreeDiv")
                    .append("svg")
                    .attr("width", w * multiple)
                    .attr("height", h * multiple);
        var seed0 = {x: root_x, y: root_y, name:dataset.topicName};
        var tree0 = buildBranch(dataset, seed0, multiple);
        draw_tree(tree0, seed0, svg, multiple);
    }); 
    /*****************************************************/ 
}



//四、点击装配按钮，在右侧栏显示所有文本碎片和图片碎片
//显示文本、图片碎片比例
function DisplayAllFragment(){
    //清空文本和图片碎片
    // $("#textFragmentDiv").empty();
    // $("#pictureFragmentDiv").empty();
    // var picNum=0;
    // var textNum=0;
    $("#fragmentDiv").empty();
    var fragmentNum=0; 

    $.ajax({
        type: "POST",
        url: ip+"/topic/getCompleteTopicByNameAndDomainNameWithHasFragment",
        data: $.param( {
            domainName:getCookie("NowClass"),
            topicName:SUBJECTNAME,
            hasFragment:true
        }),
        headers:{'Content-Type': 'application/x-www-form-urlencoded'},

        // type: "GET",
        // url: ip+"/AssembleAPI/getTreeByTopicForFragment1",
        // data: {
        //     ClassName:getCookie("NowClass"),
        //     TermName:SUBJECTNAME,
        //     HasFragment:true
        // },
        // dataType: "json",
        success: function(response){
            data = response.data
            //进入一级分面
            $.each(data.children,function(index1,value1){
                //进入二级分面
                $.each(value1.children,function(index2,value2){
                    if (value2.type==="branch"){
                        //遍历树叶
                        $.each(value2.children,function(index3,value3){
                            // 碎片api返回的api接口形式为：2017-10-28 15:29:02.0。需要去除最后的不用的时间字段
                            fragmentScratchTime = value3.assembleScratchTime.split('.')[0];
                            appendFragment(value3.assembleContent, fragmentScratchTime);
                            fragmentNum++;
                        });
                    } 
                    else{
                        // 碎片api返回的api接口形式为：2017-10-28 15:29:02.0。需要去除最后的不用的时间字
                        fragmentScratchTime = value2.assembleScratchTime.split('.')[0];
                        appendFragment(value2.assembleContent, fragmentScratchTime);
                        fragmentNum++;
                    }
                });
                showFragmentRatio(fragmentNum);
                //找到所有叶子，结束
                //return;
            });
        },
        error:function(XMLHttpRequest, textStatus, errorThrown){
            //通常情况下textStatus和errorThrown只有其中一个包含信息
            alert(textStatus);
        }
    });
}

//展示文本碎片和图形碎片的数量
function showFragmentRatio(num){
    //获得文本碎片和图片碎片数量
    // var text_ratio=text/(text+picture)*100;
    // var piture_ratio=picture/(text+picture)*100;
    //显示碎片
    document.getElementById("textCount").innerHTML=num; 
    // document.getElementById('textRatio').style.width=""+text_ratio+"%";
    // document.getElementById("pictureCount").innerHTML=picture; 
    // document.getElementById('pictureRatio').style.width=""+piture_ratio+"%";
}


