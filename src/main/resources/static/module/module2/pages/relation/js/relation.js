var category = "";
var nodename = "";
var svg2;
var layer = 1; // 当前的层级
var graph; // 课程的图数据
var categories = []; // 社团的类别
var showNodeSymbolSize = 15; // 展示的标签的节点大小

// 自适应程序
var zidingyi_height;
$(document).ready(function(){
 var header=$(".content-header").offset().top+$(".content-header").height()
 var footer=$(".main-footer").offset().top
 zidingyi_height=footer-header;
 // console.log(zidingyi_height);
 $("#relationClassDiv").css("height",zidingyi_height*0.1+"px");
 $("#relationInfoDiv").css("height",zidingyi_height*0.8+"px");
 $("#relationTreeDiv").css("height",zidingyi_height*0.8+"px");
})

var nowOperateClass;

var app=angular.module('myApp',[]);
app.controller('myCon',function($scope,$http){
    // 获取课程信息
    $http.get(ip+'/domain/getDomains').success(function(response){
        response = response["data"];
        $scope.subjects=response;
    });

    // 获取认知关系和知识森林
    $scope.getDependence=function(){
        nowOperateClass=document.getElementById("nameofclass").value;
        init(); // 知识森林初始化
        $http({
            method:'GET',
            url:ip+"/dependency/getDependenciesByDomainName",
            params:{domainName:nowOperateClass}
        }).then(function successCallback(response){
            response = response["data"];
            $scope.dependences=response.data;
            $("#DependenceNum").text(nowOperateClass+"共有"+response.data.length+"条认知关系");
        }, function errorCallback(response){

        });
        $http({
            method:'GET',
            url:ip+"/topic/getTopicsByDomainName",
            params:{domainName:nowOperateClass}
        }).then(function successCallback(response){
            response = response["data"];
            $scope.topics=response.data;
        }, function errorCallback(response){

        });
    }
    // 删除认知关系
    $scope.deleteDependence=function(a,b){
        $http({
            method:'POST',
            url:ip+"/dependency/deleteDependency",
            params:{domainName:nowOperateClass,startTopicId:a,endTopicId:b}
        }).then(function successCallback(response){
            alert(response.data.data);
            $scope.getDependence();
        }, function errorCallback(response){

        });
    }
    // 查询认知关系
    $scope.queryByKeyword=function(){
        $http({
            method:'GET',
            url:ip+"/dependency/getDependenciesByKeyword",
            params:{domainName:nowOperateClass,keyword:$("input[name='chaxun']").val()}
        }).then(function successCallback(response){
            response = response["data"];
            $scope.dependences=response.data;
        }, function errorCallback(response){

        });
    }
    // 增加认知关系
    $scope.addDependence=function(){
        var topic1=document.getElementById("Topic1").value;
        var topic2=document.getElementById("Topic2").value;
        $http({
            method:'POST',
            url:ip+"/dependency/insertDependency",
            params:{domainName:nowOperateClass,startTopicName:topic1,endTopicName:topic2}
        }).then(function successCallback(response){
            response = response["data"];
            alert(response.data);
            $scope.getDependence();
        }, function errorCallback(response){

        });
    }

});






/**
 * 知识森林程序
 */
//初始化界面
function init() {
    $(document).ready(function () {
        nowOperateClass=document.getElementById("nameofclass").value;
        // api获取图数据
        var xml;
        $.ajax({
            type :"POST",
            url :ip + "/dependency/getDependenciesByDomainNameSaveAsGexf?domainName=" + nowOperateClass,
            datatype :"json",
            async:false,
            success : function(response, status){
                data = response["data"];
                xml = data;
            }
        });
        //画力关系图
        var dom = document.getElementById("echarts1");
        var myChart = echarts.init(dom);
        var option = null;
        graph = echarts.dataTool.gexf.parse(xml);
        var communitySize = [];
        // 获取社团数量
        communityCount = 0;
        if (graph == null) {
            console.log("没有认知路径");
        } else {
            graph.nodes.forEach(function (node) {
            communityCount = Math.max(communityCount, node.attributes.modularity_class);
        });
        // 设置社团初始名字，设置节点size最大的节点为社团名字
        for (var i = 0; i <= communityCount; i++) {
            categories[i] = {name: '社团' + (i+1)};
            communitySize[i] = 0;
        }
        graph.nodes.forEach(function (node) {
            size = node.symbolSize;
            community = node.attributes.modularity_class;
            for (var i = 0; i <= communityCount; i++) {
                if (community == i) {
                    if (size > communitySize[i]) {
                        communitySize[i] = size;
                        categories[i] = {name: node.name};
                    }
                }
            }
        });
        // 设置节点格式
        graph.nodes.forEach(function (node) {
            node.itemStyle = null;
            node.value = node.symbolSize;
            node.symbol = "path://M537.804,174.688c0-44.772-33.976-81.597-77.552-86.12c-12.23-32.981-43.882-56.534-81.128-56.534   c-16.304,0-31.499,4.59-44.514,12.422C319.808,17.949,291.513,0,258.991,0c-43.117,0-78.776,31.556-85.393,72.809   c-3.519-0.43-7.076-0.727-10.71-0.727c-47.822,0-86.598,38.767-86.598,86.598c0,2.343,0.172,4.638,0.354,6.933   c-24.25,15.348-40.392,42.333-40.392,73.153c0,27.244,12.604,51.513,32.273,67.387c-0.086,1.559-0.239,3.107-0.239,4.686   c0,47.822,38.767,86.598,86.598,86.598c14.334,0,27.817-3.538,39.723-9.696c16.495,11.848,40.115,26.67,51.551,23.715   c0,0,4.255,65.905,3.337,82.64c-1.75,31.843-11.303,67.291-18.025,95.979h104.117c0,0-15.348-63.954-16.018-85.307   c-0.669-21.354,6.675-60.675,6.675-60.675l36.118-37.36c13.903,9.505,30.695,14.908,48.807,14.908   c44.771,0,81.597-34.062,86.12-77.639c32.98-12.23,56.533-43.968,56.533-81.214c0-21.994-8.262-41.999-21.765-57.279   C535.71,195.926,537.804,185.561,537.804,174.688z M214.611,373.444c6.942-6.627,12.766-14.372,17.212-22.969l17.002,35.62   C248.816,386.096,239.569,390.179,214.611,373.444z M278.183,395.438c-8.798,1.597-23.782-25.494-34.416-47.517   c11.791,6.015,25.102,9.477,39.254,9.477c3.634,0,7.201-0.296,10.72-0.736C291.006,374.286,286.187,393.975,278.183,395.438z    M315.563,412.775c-20.35,5.651-8.167-36.501-2.334-60.904c4.218-1.568,8.301-3.413,12.183-5.604   c2.343,17.786,10.069,33.832,21.516,46.521C337.011,401.597,325.593,409.992,315.563,412.775z";
            node.symbolOffset = [0, '-100%'];
            node.label = {
                normal: {
                    show: node.symbolSize > showNodeSymbolSize
                }
            };
            node.category = node.attributes.modularity_class;
        });
        graph.links.forEach(function (link) {

        })
        option = {
            title: {
                text: nowOperateClass,
                subtext: 'Default layout',
                top: 'bottom',
                left: 'right'
            },
            tooltip: {},
            legend: [{
                data: categories.map(function (a) {
                    return a.name;
                })
            }],
            animationDuration: 1500,
            animationEasingUpdate: 'quinticInOut',

            series: [{
                name: nowOperateClass,
                type: 'graph',
                layout: 'none',
                data: graph.nodes,
                links: graph.links,
                edgeSymbol: ['circle', 'arrow'],
                edgeSymbolSize: [2, 5],
                categories: categories,
                roam: true,
                focusNodeAdjacency: true,
                label: {
                    normal: {
                        position: 'right',
                        formatter: '{b}'
                    }
                },
                lineStyle: {
                    normal: {
                        curveness: 0.3,
                        color: 'source',
                    }
                }
            }]
        };
        myChart.setOption(option);
        // 点击节点跳转到社团结构
        myChart.on('click', function (params) {
            if (params.dataType == 'node') {
                // 当前层数加1，将当前社团编号传到下一层
                layer++;
                category = params.data.category; // 社团编号
                // console.log('社团category: ' + category + ', 社团name: ' + categories[category].name);
                secondLayer(category);
            }
        });
        }
        
    })
}


// 跳转到第二层社团结构的操作
function secondLayer(category) {
    var cluster = JSON.parse(JSON.stringify(graph)); // 读取图数据
    // console.log("整个图的数据规模：", cluster);
    // 删除社团外的节点：输入是社团的所有节点和目前的社团编号。根据节点的社团id进行删除
    removeByCategory(cluster.nodes, category);
    // 删除社团外的边：输入是原图的所有边和社团现有的节点。
    removeLinks(cluster.links, cluster.nodes);
    // console.log("该社团的数据规模：", cluster);
    //画力关系图
    var dom = document.getElementById("echarts1");
    var myChart = echarts.init(dom);
    var option = null;
    cluster.nodes.forEach(function (node) {
        node.itemStyle = null;
        node.symbolSize = node.symbolSize * 1.5;
        node.value = node.symbolSize;
        node.symbol = "path://M537.804,174.688c0-44.772-33.976-81.597-77.552-86.12c-12.23-32.981-43.882-56.534-81.128-56.534   c-16.304,0-31.499,4.59-44.514,12.422C319.808,17.949,291.513,0,258.991,0c-43.117,0-78.776,31.556-85.393,72.809   c-3.519-0.43-7.076-0.727-10.71-0.727c-47.822,0-86.598,38.767-86.598,86.598c0,2.343,0.172,4.638,0.354,6.933   c-24.25,15.348-40.392,42.333-40.392,73.153c0,27.244,12.604,51.513,32.273,67.387c-0.086,1.559-0.239,3.107-0.239,4.686   c0,47.822,38.767,86.598,86.598,86.598c14.334,0,27.817-3.538,39.723-9.696c16.495,11.848,40.115,26.67,51.551,23.715   c0,0,4.255,65.905,3.337,82.64c-1.75,31.843-11.303,67.291-18.025,95.979h104.117c0,0-15.348-63.954-16.018-85.307   c-0.669-21.354,6.675-60.675,6.675-60.675l36.118-37.36c13.903,9.505,30.695,14.908,48.807,14.908   c44.771,0,81.597-34.062,86.12-77.639c32.98-12.23,56.533-43.968,56.533-81.214c0-21.994-8.262-41.999-21.765-57.279   C535.71,195.926,537.804,185.561,537.804,174.688z M214.611,373.444c6.942-6.627,12.766-14.372,17.212-22.969l17.002,35.62   C248.816,386.096,239.569,390.179,214.611,373.444z M278.183,395.438c-8.798,1.597-23.782-25.494-34.416-47.517   c11.791,6.015,25.102,9.477,39.254,9.477c3.634,0,7.201-0.296,10.72-0.736C291.006,374.286,286.187,393.975,278.183,395.438z    M315.563,412.775c-20.35,5.651-8.167-36.501-2.334-60.904c4.218-1.568,8.301-3.413,12.183-5.604   c2.343,17.786,10.069,33.832,21.516,46.521C337.011,401.597,325.593,409.992,315.563,412.775z";
        node.symbolOffset = [0, '-100%'];
        node.label = {
            normal: {
                show: node.symbolSize > 0
            }
        };
        node.category = node.attributes.modularity_class;
    });
    cluster.links.forEach(function (link) {

    })
    option = {
        title: {
            text: categories[parseInt(category)].name,
            subtext: 'Default layout',
            top: 'bottom',
            left: 'right'
        },
        tooltip: {},
        animationDuration: 1500,
        animationEasingUpdate: 'quinticInOut',

        series: [{
            name: category,
            type: 'graph',
            layout: 'none',
            data: cluster.nodes,
            links: cluster.links,
            edgeSymbol: ['circle', 'arrow'],
            edgeSymbolSize: [4, 10],
            //categories: categories,
            roam: true,
            focusNodeAdjacency: true,
            label: {
                normal: {
                    position: 'right',
                    formatter: '{b}'
                }
            },
            itemStyle: {
                normal: {
                    color: {
                        type: 'linear',
                        x: 0,
                        y: 0,
                        x2: 0,
                        y2: 1,
                        colorStops: [{
                            offset: 0,
                            color: 'green' // 0% 处的颜色
                        }, {
                            offset: 1,
                            color: 'brown' // 100% 处的颜色
                        }],
                        globalCoord: false // 缺省为 false
                    }
                }
            },
            lineStyle: {
                normal: {
                    curveness: 0.3,
                    color: 'source',
                }
            }
        }]
    };
    myChart.setOption(option);
    myChart.on('click', function (params) {
        if (params.dataType == 'node') {
            // 当前层数加1，将当前社团编号传到下一层
            layer++;
            nodename = params.data.name;
            nodeId = params.data.id;
            // console.log('进入第三层社团：', nodename, nodeId);
            thirdLayer(nodename, nodeId);
        }
    });
}

function thirdLayer(name, id) {
    var cluster = JSON.parse(JSON.stringify(graph));
    // 删除社团外的边：输入是原图的所有边和点击的节点
    removeLinksBySelected(cluster.links, id);
    // 删除社团外的节点：输入是社团的所有节点和目前社团的边
    removeNodesBySelected(cluster.links, cluster.nodes);
    // console.log(cluster);
    //画力关系图
    var dom = document.getElementById("echarts1");
    var myChart = echarts.init(dom);
    var option = null;
    cluster.nodes.forEach(function (node) {
        node.itemStyle = null;
        node.symbolSize = node.symbolSize * 1.5;
        node.value = node.symbolSize;
        node.symbol = "path://M537.804,174.688c0-44.772-33.976-81.597-77.552-86.12c-12.23-32.981-43.882-56.534-81.128-56.534   c-16.304,0-31.499,4.59-44.514,12.422C319.808,17.949,291.513,0,258.991,0c-43.117,0-78.776,31.556-85.393,72.809   c-3.519-0.43-7.076-0.727-10.71-0.727c-47.822,0-86.598,38.767-86.598,86.598c0,2.343,0.172,4.638,0.354,6.933   c-24.25,15.348-40.392,42.333-40.392,73.153c0,27.244,12.604,51.513,32.273,67.387c-0.086,1.559-0.239,3.107-0.239,4.686   c0,47.822,38.767,86.598,86.598,86.598c14.334,0,27.817-3.538,39.723-9.696c16.495,11.848,40.115,26.67,51.551,23.715   c0,0,4.255,65.905,3.337,82.64c-1.75,31.843-11.303,67.291-18.025,95.979h104.117c0,0-15.348-63.954-16.018-85.307   c-0.669-21.354,6.675-60.675,6.675-60.675l36.118-37.36c13.903,9.505,30.695,14.908,48.807,14.908   c44.771,0,81.597-34.062,86.12-77.639c32.98-12.23,56.533-43.968,56.533-81.214c0-21.994-8.262-41.999-21.765-57.279   C535.71,195.926,537.804,185.561,537.804,174.688z M214.611,373.444c6.942-6.627,12.766-14.372,17.212-22.969l17.002,35.62   C248.816,386.096,239.569,390.179,214.611,373.444z M278.183,395.438c-8.798,1.597-23.782-25.494-34.416-47.517   c11.791,6.015,25.102,9.477,39.254,9.477c3.634,0,7.201-0.296,10.72-0.736C291.006,374.286,286.187,393.975,278.183,395.438z    M315.563,412.775c-20.35,5.651-8.167-36.501-2.334-60.904c4.218-1.568,8.301-3.413,12.183-5.604   c2.343,17.786,10.069,33.832,21.516,46.521C337.011,401.597,325.593,409.992,315.563,412.775z";
        node.symbolOffset = [0, '-100%'];
        node.label = {
            normal: {
                show: node.symbolSize > 0
            }
        };
        node.category = node.attributes.modularity_class;
    });
    cluster.links.forEach(function (link) {

    })
    myChart.on('click', function (params) {
        if (params.dataType == 'node') {
            
        }
    });
    option = {
        title: {
            text: nodename.toString(),
            subtext: 'Default layout',
            top: 'bottom',
            left: 'right'
        },
        tooltip: {},
        animationDuration: 1500,
        animationEasingUpdate: 'quinticInOut',

        series: [{
            name: category,
            type: 'graph',
            layout: 'none',
            data: cluster.nodes,
            links: cluster.links,
            edgeSymbol: ['circle', 'arrow'],
            edgeSymbolSize: [4, 10],
            //categories: categories,
            roam: true,
            focusNodeAdjacency: true,
            label: {
                normal: {
                    position: 'right',
                    formatter: '{b}'
                }
            },
            itemStyle: {
                normal: {
                    color: {
                        type: 'linear',
                        x: 0,
                        y: 0,
                        x2: 0,
                        y2: 1,
                        colorStops: [{
                            offset: 0,
                            color: 'green' // 0% 处的颜色
                        }, {
                            offset: 1,
                            color: 'brown' // 100% 处的颜色
                        }],
                        globalCoord: false // 缺省为 false
                    }
                }
            },
            lineStyle: {
                normal: {
                    curveness: 0.3,
                    color: 'source',
                }
            }
        }]
    };
    myChart.setOption(option);
}

// 删除社团外的节点：输入是社团的所有节点(nodeArr)和目前的社团编号(category)。根据节点的社团id进行删除
function removeByCategory(nodeArr, category) {
    for (var i = 0; i < nodeArr.length; i++) {
        if (nodeArr[i].category != category) {
            nodeArr.splice(i, 1);
            i--;
        }
    }
}

// 删除社团外的边：输入是原图的所有边(arrLink)和社团现有的节点(arrNode)。
function removeLinks(arrLink, arrNode) {
    // 遍历所有边，删除边的头结点或者尾节点不是目前社团节点中结点的边
    for (var i = 0; i < arrLink.length; i++) {
        var sourceflag = 0;
        var targetflag = 0;
        for (var j = 0; j < arrNode.length; j++) {
            // arrLink[i].source 是边的起始节点id和节点的id有相同的说明边的起始节点在社团内
            if (arrLink[i].source === arrNode[j].id) {
                // console.log(arrLink[i].source);
                sourceflag = 1;
                break;
            }
        }
        // 判断边的终止节点是不是也在社团内，为1表示在社团内
        if (sourceflag == 1) {
            for (var j = 0; j < arrNode.length; j++) {
                if (arrLink[i].target == arrNode[j].id) {
                    targetflag = 1;
                }
            }
        }
        // 当边的起始节点和终止节点都是社团内得节点时，保留这条边，否则删除该边
        if (sourceflag != 1 && targetflag != 1) {
            arrLink.splice(i, 1);
            i--;
        }
    }
}

// 删除社团外的边：输入是原图的所有边(arrLink)和点击的节点(id)
function removeLinksBySelected(arrLink, id) {
    // 删除边的节点不包含点击节点的边
    for (var i = 0; i < arrLink.length; i++) {
        if (arrLink[i].source != id && arrLink[i].target != id) {
            arrLink.splice(i, 1);
            i--;
        } else {
            // console.log(arrLink[i]);
        }
    }
}

// 删除社团外的节点：输入是所有的节点(arrNode)和目前社团中的边(arrLink)
function removeNodesBySelected(arrLink, arrNode) {
    for (var i = 0; i < arrNode.length; i++) {
        var flag = 0;
        for (var j = 0; j < arrLink.length; j++) {
            if (arrNode[i].id == arrLink[j].source || arrNode[i].id == arrLink[j].target) {
                flag = 1;
                break;
            }
        }
        if (flag == 0) {
            arrNode.splice(i, 1);
            i--;
        }
    }
}

$(document).ready(function () {
    $("#mydiv2").dblclick(function () {
        //init();
        if (layer > 1) {
            layer--;
        }
        if (layer == 1) {
            init()
        }
        if (layer == 2) {
            secondLayer(category);
        }
        if (layer == 3) {

        }
    });
})
