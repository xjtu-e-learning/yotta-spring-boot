var dep;
var category = "";
var nodename = "";
var layer = 1; // 当前的层级
var graph; // 课程的图数据
var categories = []; // 社团的类别
var showNodeSymbolSize = 25; // 展示的标签的节点大小

//初始化界面
function init() {
    $(document).ready(function(){
        // api获取上下位关系，填入左边的表格
        $.ajax({
            type :"GET",
            url :ip+"/dependency/getDependenciesByDomainName?domainName="+getCookie("NowClass"),
            datatype :"json",
            async:false,
            success : function(response,status){
                data = response["data"]

                dep=data;
                // console.log(dep.length);
            }
        })
        //定义edges[]和nodes[]
        var edges=new Array();
        for(var i=0;i<dep.length;i++){
            edges[i]={source:Number(dep[i].startTopicId)-1,sourceName:dep[i].startTopicName,targetName:dep[i].endTopicName,target:Number(dep[i].endTopicId)-1,conf:Number(dep[i].confidence)};
        }
        //向table中添加关系
        for(var i=0;i<edges.length;i++){
            $("#table").append(
              "<tr class='tr1' id="+i+"><td>"+edges[i].sourceName+"</td><td>"+edges[i].targetName+"</td></tr>"
            );
        }



        // api获取图数据
        var xml;
        $.ajax({
            type :"POST",
            url :ip+"/dependency/getDependenciesByDomainNameSaveAsGexf?domainName=" + getCookie("NowClass"),
            datatype :"json",
            async:false,
            success : function(response, status){

                // console.log("success");
                xml = response["data"];
            }
        })
        //画力关系图
        var dom = document.getElementById("echarts1");
        var myChart = echarts.init(dom);
        var option = null;
        graph = echarts.dataTool.gexf.parse(xml);
        var communitySize = [];
        // 获取社团数量
        communityCount = 0;
        if (graph == null) {
            console.log("没有认知路径数据");
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
                        // console.log(node)
                        categories[i] = {name: node.name};
                    }
                }
            }
        });
        // 设置节点格式
        graph.nodes.forEach(function (node) {
            node.itemStyle = null;
            node.value = node.symbolSize;
            node.label = {
                normal: {
                    show: node.symbolSize > showNodeSymbolSize
                }
            };
            node.category = node.attributes.modularity_class;
        });
        option = {
            title: {
                text: getCookie("NowClass"),
                subtext: 'Default layout',
                top: 'bottom',
                left: 'right'
            },
            tooltip: {},
            legend: [{
                // selectedMode: 'single',
                data: categories.map(function (a) {
                    return a.name;
                })
            }],
            animationDuration: 1500,
            animationEasingUpdate: 'quinticInOut',
            series : [
                {
                    name: getCookie("NowClass"),
                    type: 'graph',
                    // layout: 'force',
                    layout: 'none',
                    data: graph.nodes,
                    links: graph.links,
                    edgeSymbol: ['circle', 'arrow'],
                    edgeSymbolSize: [2, 5],
                    categories: categories,
                    focusNodeAdjacency: true, // focus显示相邻节点
                    roam: true,
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
                    },
                }
            ]
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
        node.label = {
            normal: {
                show: node.symbolSize > 0
            }
        };
        node.category = node.attributes.modularity_class;
    });
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
        node.label = {
            normal: {
                show: node.symbolSize > 0
            }
        };
        node.category = node.attributes.modularity_class;
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

init();