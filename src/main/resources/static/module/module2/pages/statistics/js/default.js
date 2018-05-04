/**
 * 各个表格默认显示的信息 
 */
$(document).ready(function(){

    /**
     * 课程统计1：根据不同维度统计 
     */
    // 通过API获取课程统计数据
    var domainStatistics; // 课程所有统计数据
    $.ajax({
        type : "GET",
        url : ip + "/statistics/getStatisticalInformation",
        datatype : "json",
        async : false,
        success : function(response, status){
            data = response["data"];
            domainStatistics = data;
            // console.log(domainStatistics);
        }
    });

    // 计算不同维度的y轴的最大值
    //分别是 主题 上下位关系 分面 分面关系 依赖关系 碎片
    var maxNumber = [0, 0, 0, 0, 0, 0]; 
    for (var i = 1, len = domainStatistics.topicNumbers.length; i < len; i++) {
        if (maxNumber[0] < domainStatistics.topicNumbers[i]) {
            maxNumber[0] = domainStatistics.topicNumbers[i];
        }
        if (maxNumber[1] < domainStatistics.relationNumbers[i]) {
            maxNumber[1] = domainStatistics.relationNumbers[i];
        }
        if (maxNumber[2] < domainStatistics.facetNumbers[i]) {
            maxNumber[2] = domainStatistics.facetNumbers[i];
        }
        if (maxNumber[3] < domainStatistics.facetRelationNumbers[i]) {
            maxNumber[3] = domainStatistics.facetRelationNumbers[i];
        }
        if (maxNumber[4] < domainStatistics.dependencyNumbers[i]) {
            maxNumber[4] = domainStatistics.dependencyNumbers[i];
        }
        if (maxNumber[5] < domainStatistics.assembleNumbers[i]) {
            maxNumber[5] = domainStatistics.assembleNumbers[i];
        }
    }
    // 将最大值转化为靠近它的最小整数，如：2126 -> 3000
    for(var k = 0, len = maxNumber.length; k < len; k++) {
        var count = 0;
        while(maxNumber[k] / 10 >= 1) {
            count++;
            maxNumber[k] = maxNumber[k] / 10;
        }
        maxNumber[k] = Math.ceil(maxNumber[k]);
        for (var i = 0; i < count; i++) {
            maxNumber[k] = maxNumber[k] * 10;
        }
        // console.log(maxNumber[k]);
    }
    // 获取坐标轴x轴的领域名格式
    var domainListX = domainStatistics.domainNames; 
    for (var i = 0, len = domainStatistics.domainNames.length; i < len; i++) {
        if (i % 2 == 1) {
            domainListX[i] = '\n' + domainListX[i];
        }
    }
    // console.log(domainListX);
  
    var myChart = echarts.init(document.getElementById('classStatisticsByFiveDimension'));
    // 指定图表的配置项和数据
    dataMap = {};
    function dataFormatter(obj) {
        var pList = domainStatistics.domainNames;
        var temp;
        for (var month = 1; month <= 6; month++) {
            temp = obj[month.toString()];
            obj[month + 'sum'] = temp[0];
            for (var i = 1, len = temp.length; i < len; i++) {
                obj[month.toString()][i] = {
                    name : pList[i],
                    value : temp[i]
                }
            }
            obj[month.toString()].shift()
        }
        return obj;
    }
    // 设置数据为api返回的数据
    dataMap.dataMonth = dataFormatter({
        //max : 60000,
        '1': domainStatistics.topicNumbers, 
        '2': domainStatistics.relationNumbers, 
        '3': domainStatistics.facetNumbers, 
        '4': domainStatistics.facetRelationNumbers, 
        '5': domainStatistics.dependencyNumbers,
        '6': domainStatistics.assembleNumbers,
    });
    // 开始画图
    option = {
        baseOption: {
            timeline: {
                axisType: 'category',
                // realtime: false,
                // loop: false,
                autoPlay: true,
                // currentIndex: 2,
                playInterval: 1000,
                data: ['主题',
                    {
                        value: '上下位关系',
                        tooltip: {
                            formatter: '{b} 主题的上下位关系，根据维基目录结构获取'
                        },
                        symbol: 'diamond',
                        symbolSize: 18
                    },
                    '分面', 
                    {
                        value: '分面关系',
                        tooltip: {
                            formatter: '{b} 主题中分面的层级关系'
                        },
                        symbol: 'diamond',
                        symbolSize: 18
                    }, 
                    {
                        value: '认知关系',
                        tooltip: {
                            formatter: '{b} 主题的认知关系，根据LDA算法计算主题之间的认知关系'
                        },
                        symbol: 'diamond',
                        symbolSize: 18
                    },
                    '碎片',
                ],
            },
            title: {
                // subtext: '作者：从零开始',
                subtextStyle: {
                    color:'#000',
                },
            },
            tooltip: {},
            calculable : true,
            grid: {
                top: 80,
                bottom: 100,
                containLabel:true,
            },
            xAxis: [
                {
                    name : "课程",
                    'type':'category',
                    'axisLabel':{'interval':0},
                    'data': domainListX,
                    splitLine: {show: false}
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    name: '数量',
                    max : 10000
                }
            ],
            series: [
                // 柱状图
                {name: '课程', type: 'bar'},
                // 圆饼图
                {
                    name: '所有课程主题/分面等信息统计',
                    type: 'pie',
                    color: ['#24936E', '#F596AA', '#DB8E71', '#58B2DC','#81C7D4', '#CC0033'],
                    center: ['30%', '20%'],
                    radius: [20, '28%'],
                    type: 'pie',
                    data:[{name: "主题",value : dataMap.dataMonth["1sum"]},
                        {name: "上下位关系",value : dataMap.dataMonth["2sum"]},
                        {name: "分面",value : dataMap.dataMonth["3sum"]},
                        {name: "分面关系",value : dataMap.dataMonth["4sum"]},
                        {name: "认知关系",value : dataMap.dataMonth["5sum"]},
                        {name: "碎片",value : dataMap.dataMonth["6sum"]},
                    ]
                }
            ],
            toolbox: {
                show: true,
                orient: 'vertical',
                x: 'right',
                y: 'center',
                feature: {
                    dataZoom: {
                        yAxisIndex: 'none'
                    },
                    dataView: {
                        readOnly: false
                    },
                    magicType: {
                        type: ['line', 'bar']
                    },
                    restore: {},
                    saveAsImage: {}
                }
            },
        },
        options: [
            {
                title: {text: '主题'},
                series: [
                    {
                        data: dataMap.dataMonth['1'],itemStyle:{
                        normal:{color: "#24936E"}
                    },},
                ],
                yAxis: [{max : maxNumber[0]}],
            },
            {
                
                title : {text: '上下位关系'},
                series : [{data: dataMap.dataMonth['2'],itemStyle:{
                        normal:{color: "#F596AA"}
                    },},],
                yAxis: [{max : maxNumber[1]}],
            },
            {
                title : {text: '分面'},
                series : [{data: dataMap.dataMonth['3'],itemStyle:{
                        normal:{color: "#DB8E71"}
                    },},],
                yAxis: [{max : maxNumber[2]}],
            },
            {
                title : {text: '分面关系'},
                series : [{data: dataMap.dataMonth['4'],itemStyle:{
                        normal:{color: "#58B2DC"}
                    },},],
                yAxis: [{max : maxNumber[3]}],
            },
            {
                title : {text: '认知关系'},
                series : [{data: dataMap.dataMonth['5'],itemStyle:{
                        normal:{color: "#81C7D4"}
                    },},],
                yAxis: [{max : maxNumber[4]}],
            },
            {
                title : {text: '碎片'},
                series : [{data: dataMap.dataMonth['6'],itemStyle:{
                        normal:{color: "#CC0033"}
                    },},],
                yAxis: [{max : maxNumber[5]}],
            }
        ]
    };
    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
    /**
     * 课程统计1：根据不同维度统计（结束）
     */












    /**
     * 课程统计2：根据课程统计 
     */
var myChart = echarts.init(document.getElementById('classStatisticsByDomain'));
    // 指定图表的配置项和数据
    // var dataBeast = domainStatistics.topicList.slice(1);
    // var dataBeauty = [541, 513, 792, 701, 660, 729, 782, 660, 841, 521, 820, 578, 727, 598, 660, 841, 521, 820, 578, 727, 598, 792, 701, 660, 729, 513, 792, 701];
    var dataTopicList = domainStatistics.topicList.slice(1);
    var dataFacetList = domainStatistics.facetList.slice(1);
    var dataFragmentList = domainStatistics.fragmentList.slice(1);
    var dataDependencyList = domainStatistics.dependencyList.slice(1);
    var xAxisData = domainStatistics.domainList;
    // 设置x轴初始显示的主题个数为10个
    var topicLength = xAxisData.length;
    var end = 100; // 显示百分之end的x轴数据
    if (topicLength > 10) {
        end = (10 / topicLength) * 100;
    }
    // 得到数据的和
    var dataTotal = function() {
        var data = [];
        for(var i = 0; i < dataTopicList.length; i++){
            data.push(dataTopicList[i] + dataFacetList[i] + dataFragmentList[i] + dataDependencyList[i]);
        }
        return data;
    }

    // console.log(dataTotal());

    option = {
        // color:['#019aba','#7a201f','#DB8E71', '#24936E', '#CC0033'],
        color: ['#24936E', '#F596AA', '#DB8E71', '#58B2DC', '#CC0033'],
        // backgroundColor:'#000',
        title: {
            text: '所有课程',
            textStyle: {
                color:'#000',
                fontSize:18,
                fontWeight:'bold',
                
            },
            // subtext:'作者：从零开始',
            subtextStyle: {
                color:'#000',
            },
        },
        legend:{
            right:'20%',
            textStyle: {
                color: '#000',
                fontSize: 12,
            },
            data:['主题','分面','认知关系','碎片', '总数量'],
        },
        tooltip:{
            show:true,
            trigger: 'axis',
            axisPointer: {
                type:'cross',
                crossStyle:{
                   color:'#000',
                   
               },

            },
        },
        toolbox:{
            // right:20,
            // feature:{
            //     saveAsImage: {},
            //     restore: {},
            //     dataView: {},
            //     dataZoom: {},
            //     magicType: {
            //         type:['line','bar']
            //     },
            //     // brush: {},
            // }
            show: true,
            orient: 'vertical',
            x: 'right',
            y: 'center',
            feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                dataView: {
                    readOnly: false
                },
                magicType: {
                    type: ['line', 'bar']
                },
                restore: {},
                saveAsImage: {}
            }
        },
        
        grid:{
            // left:5,
            // right:20,
            top:80,
            bottom:50,
            containLabel:true,
        },
        xAxis: {
          show:true,
          
          axisLabel:{
             interval:0,
             rotate:-20,
             margin: 30,
             textStyle:{
                  color:'#000',
                  align: 'center'
                 
             },
          },
          axisTick:{
              alignWithLabel:true,
              lineStyle:{
                  color:'#000',
                  
              },
          },
          data:xAxisData,
        },
        yAxis: [
            {
                type:'value',
                name:'数量',
                nameTextStyle:{
                    color:'#000',
                },
                axisLabel:{
                 textStyle:{
                    color:'#000',
                 },
                },
                axisTick:{
                  alignWithLabel:true,
                  lineStyle:{
                      color:'#000',
                      
                  },
                },
                splitLine:{
                    show:false,
                },
            },
            {
                type:'value',
                name:'总数量',
                nameTextStyle:{
                    color:'#000',
                },
                axisLabel:{
                 textStyle:{
                      color:'#000',
                 },
                },
                axisTick:{
                  alignWithLabel:true,
                  lineStyle:{
                      color:'#000',
                      
                  },
                },
                splitLine:{
                    show:false,
                },
            },
        ],
        dataZoom: [{
            show: true,
            height:20,
            bottom:10,
            start: 0,
            end: end,
            handleIcon: 'path://M306.1,413c0,2.2-1.8,4-4,4h-59.8c-2.2,0-4-1.8-4-4V200.8c0-2.2,1.8-4,4-4h59.8c2.2,0,4,1.8,4,4V413z',
            handleSize: '110%',
            handleStyle:{ // borderColor:"#5476c2"
                color:"#7a201f",
                borderColor:"#7a201f"
            },
            textStyle:{color:"#000"}, 
            borderColor:"#90979c",
            backgroundColor:"#f7f7f7", /*背景 */
            fillerColor:"rgba(220,210,230,0.6)", /*被start和end遮住的背景*/
            dataBackground:{ /*数据背景*/
                lineStyle:{color:"#dfdfdf"},
                areaStyle:{color:"#dfdfdf"}
            },
            }, 
            { type: "inside"}
        ],
        series: [
            {
                type: 'bar',
                name:'主题',
                stack:'数量',
                data:dataTopicList,
                label: {
                    normal: {
                        show:false,
                        position: 'insideTop',
                        offset:[0,20],
                        textStyle:{
                           color:'#000',
                        },
                    },
                    emphasis: {
                         textStyle:{
                           color:'#000',
                        }, 
                    },
                },
            },
            {
                type: 'bar',
                name:'分面',
                stack:'数量',
                data:dataFacetList,
                label: {
                    normal: {
                        show:false,
                        position: 'insideTop',
                        offset:[0,20],
                        textStyle:{
                           color:'#000',
                        },
                    },
                    emphasis: {
                       textStyle:{
                           color:'#000',
                        }, 
                    },
                },
            },
            {
                type: 'bar',
                name:'认知关系',
                stack:'数量',
                data:dataDependencyList,
                label: {
                    normal: {
                        show:false,
                        position: 'insideTop',
                        offset:[0,20],
                        textStyle:{
                           color:'#000',
                        },
                    },
                    emphasis: {
                       textStyle:{
                           color:'#000',
                        }, 
                    },
                },
            },
            {
                type: 'bar',
                name:'碎片',
                stack:'数量',
                data:dataFragmentList,
                label: {
                    normal: {
                        show:false,
                        position: 'insideTop',
                        offset:[0,20],
                        textStyle:{
                           color:'#000',
                        },
                    },
                    emphasis: {
                       textStyle:{
                           color:'#000',
                        }, 
                    },
                },
            },
            {
                type: 'line',
                name:'总数量',
                yAxisIndex:1,
                stack:'数量',
                data:dataTotal(),
                label: {
                    normal: {
                        show:true,
                        position: 'insideTop',
                        offset: [0,-30],
                         textStyle:{
                           color:'#000',
                        }, 
                    },
                    emphasis: {
                        textStyle:{
                           color:'#000',
                        },  
                    },
                },
                // symbol:'image://../imgs/point1.png',
                symbolSize:8,
                itemStyle: {
                    normal: {
                        // "color": "#01B3D7",
                        barBorderRadius: 0,
                        label: {
                            show:false,
                            position: "top",
                            formatter: function(p) {
                                return p.value > 0 ? (p.value) : '';
                            }
                        }
                    }
                },
                lineStyle: {
                    normal: {
                    color: '#01B3D7',
                    width: 1,
                
                    },
                },
            },
        ]
    };

    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
    /**
     * 课程统计2：根据课程统计（结束）
     */









    /**
     * 主题统计：根据主题统计 
     */
// console.log('主题统计-选择学科：' + subjectName + '，选择课程：' + domainName+ '，选择主题：' + topicName);
        var domainStatistics; // 主题所有统计数据
        $.ajax({
            type : "GET",
            url : ip + "/StatisticsAPI/getTopicInfoByDomain?domainName=数据结构",
            datatype : "json",
            async : false,
            success : function(data, status){
                domainStatistics = data;
                // console.log(domainStatistics);
            }
        });

        var myChart = echarts.init(document.getElementById('topicStatistics'));
        // 指定图表的配置项和数据
        var dataFacetList = domainStatistics.facetList.slice(1);
        var dataFacetFirstList = domainStatistics.facetFirstList.slice(1);
        var dataFacetSecondList = domainStatistics.facetSecondList.slice(1);
        var dataFacetThirdList = domainStatistics.facetThirdList.slice(1);
        var dataDependencyList = domainStatistics.dependencyList.slice(1);
        var dataFragmentList = domainStatistics.fragmentList.slice(1);
        var xAxisData = domainStatistics.topicList;
        // 设置x轴初始显示的主题个数为10个
        var topicLength = xAxisData.length;
        var end = 100; // 显示百分之end的x轴数据
        if (topicLength > 10) {
            end = (10 / topicLength) * 100;
        }
        // 得到数据的和
        var dataTotal = function() {
            var data = [];
            for(var i = 0; i < dataFacetList.length; i++){
                data.push(dataFacetList[i] + dataFacetFirstList[i] + dataFacetSecondList[i] + dataFacetThirdList[i] + dataDependencyList[i] + dataFragmentList[i]);
            }
            return data;
        }

        // console.log(dataTotal());

        option = {
            // color:['#019aba','#7a201f','#DB8E71', '#24936E', '#CC0033'],
            color: ['#24936E', '#F596AA', '#DB8E71', '#58B2DC', '#019aba','#7a201f', '#CC0033'],
            // backgroundColor:'#000',
            title: {
                text: '学科：计算机学科，课程：数据结构',
                textStyle: {
                    color:'#000',
                    fontSize:18,
                    fontWeight:'bold',
                    
                },
                // subtext:'作者：从零开始',
                subtextStyle: {
                    color:'#000',
                },
            },
            legend:{
                right:'20%',
                textStyle: {
                    color: '#000',
                    fontSize: 12,
                },
                data:['分面','一级分面','二级分面','三级分面','认知关系','碎片','总数量'],
            },
            tooltip:{
                show:true,
                trigger: 'axis',
                axisPointer: {
                    type:'cross',
                    crossStyle:{
                       color:'#000',
                       
                   },

                },
            },
            toolbox:{
                // right:20,
                // feature:{
                //     saveAsImage: {},
                //     restore: {},
                //     dataView: {},
                //     dataZoom: {},
                //     magicType: {
                //         type:['line','bar']
                //     },
                //     // brush: {},
                // }
                show: true,
                orient: 'vertical',
                x: 'right',
                y: 'center',
                feature: {
                    dataZoom: {
                        yAxisIndex: 'none'
                    },
                    dataView: {
                        readOnly: false
                    },
                    magicType: {
                        type: ['line', 'bar']
                    },
                    restore: {},
                    saveAsImage: {}
                }
            },
            
            grid:{
                // left:5,
                // right:20,
                top:80,
                bottom:50,
                containLabel:true,
            },
            xAxis: {
              show:true,
              
              axisLabel:{
                 interval:0,
                 rotate:-20,
                 margin: 30,
                 textStyle:{
                      color:'#000',
                      align: 'center'
                     
                 },
              },
              axisTick:{
                  alignWithLabel:true,
                  lineStyle:{
                      color:'#000',
                      
                  },
              },
              data:xAxisData,
            },
            yAxis: [
                {
                    type:'value',
                    name:'数量',
                    nameTextStyle:{
                        color:'#000',
                    },
                    axisLabel:{
                     textStyle:{
                        color:'#000',
                     },
                    },
                    axisTick:{
                      alignWithLabel:true,
                      lineStyle:{
                          color:'#000',
                          
                      },
                    },
                    splitLine:{
                        show:false,
                    },
                },
                {
                    type:'value',
                    name:'总数量',
                    nameTextStyle:{
                        color:'#000',
                    },
                    axisLabel:{
                     textStyle:{
                          color:'#000',
                     },
                    },
                    axisTick:{
                      alignWithLabel:true,
                      lineStyle:{
                          color:'#000',
                          
                      },
                    },
                    splitLine:{
                        show:false,
                    },
                },
            ],
            dataZoom: [{
                show: true,
                height:20,
                bottom:10,
                start: 0,
                end: end,
                handleIcon: 'path://M306.1,413c0,2.2-1.8,4-4,4h-59.8c-2.2,0-4-1.8-4-4V200.8c0-2.2,1.8-4,4-4h59.8c2.2,0,4,1.8,4,4V413z',
                handleSize: '110%',
                handleStyle:{ // borderColor:"#5476c2"
                    color:"#7a201f",
                    borderColor:"#7a201f"
                },
                textStyle:{color:"#000"}, 
                borderColor:"#90979c",
                backgroundColor:"#f7f7f7", /*背景 */
                fillerColor:"rgba(220,210,230,0.6)", /*被start和end遮住的背景*/
                dataBackground:{ /*数据背景*/
                    lineStyle:{color:"#dfdfdf"},
                    areaStyle:{color:"#dfdfdf"}
                },
                }, 
                { type: "inside"}
            ],
            series: [
                {
                    type: 'bar',
                    name:'分面',
                    stack:'数量',
                    data:dataFacetList,
                    label: {
                        normal: {
                            show:false,
                            position: 'insideTop',
                            offset:[0,20],
                            textStyle:{
                               color:'#000',
                            },
                        },
                        emphasis: {
                             textStyle:{
                               color:'#000',
                            }, 
                        },
                    },
                },
                {
                    type: 'bar',
                    name:'一级分面',
                    stack:'数量',
                    data:dataFacetFirstList,
                    label: {
                        normal: {
                            show:false,
                            position: 'insideTop',
                            offset:[0,20],
                            textStyle:{
                               color:'#000',
                            },
                        },
                        emphasis: {
                           textStyle:{
                               color:'#000',
                            }, 
                        },
                    },
                },
                {
                    type: 'bar',
                    name:'二级分面',
                    stack:'数量',
                    data:dataFacetSecondList,
                    label: {
                        normal: {
                            show:false,
                            position: 'insideTop',
                            offset:[0,20],
                            textStyle:{
                               color:'#000',
                            },
                        },
                        emphasis: {
                           textStyle:{
                               color:'#000',
                            }, 
                        },
                    },
                },
                {
                    type: 'bar',
                    name:'三级分面',
                    stack:'数量',
                    data:dataFacetThirdList,
                    label: {
                        normal: {
                            show:false,
                            position: 'insideTop',
                            offset:[0,20],
                            textStyle:{
                               color:'#000',
                            },
                        },
                        emphasis: {
                           textStyle:{
                               color:'#000',
                            }, 
                        },
                    },
                },
                {
                    type: 'bar',
                    name:'认知关系',
                    stack:'数量',
                    data:dataDependencyList,
                    label: {
                        normal: {
                            show:false,
                            position: 'insideTop',
                            offset:[0,20],
                            textStyle:{
                               color:'#000',
                            },
                        },
                        emphasis: {
                           textStyle:{
                               color:'#000',
                            }, 
                        },
                    },
                },
                {
                    type: 'bar',
                    name:'碎片',
                    stack:'数量',
                    data:dataFragmentList,
                    label: {
                        normal: {
                            show:false,
                            position: 'insideTop',
                            offset:[0,20],
                            textStyle:{
                               color:'#000',
                            },
                        },
                        emphasis: {
                           textStyle:{
                               color:'#000',
                            }, 
                        },
                    },
                },
                {
                    type: 'line',
                    name:'总数量',
                    yAxisIndex:1,
                    stack:'数量',
                    data:dataTotal(),
                    label: {
                        normal: {
                            show:true,
                            position: 'insideTop',
                            offset: [0,-30],
                             textStyle:{
                               color:'#000',
                            }, 
                        },
                        emphasis: {
                            textStyle:{
                               color:'#000',
                            },  
                        },
                    },
                    // symbol:'image://../imgs/point1.png',
                    symbolSize:8,
                    itemStyle: {
                        normal: {
                            // "color": "#01B3D7",
                            barBorderRadius: 0,
                            label: {
                                show:false,
                                position: "top",
                                formatter: function(p) {
                                    return p.value > 0 ? (p.value) : '';
                                }
                            }
                        }
                    },
                    lineStyle: {
                        normal: {
                        color: '#01B3D7',
                        width: 1,
                    
                        },
                    },
                },
            ]
        };

        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
        /**
        * 主题统计：根据主题统计 （结束）
        */















    /**
     * 主题详细信息统计：统计主题下的每个分面的碎片信息等
     */
// console.log('主题统计-选择学科：' + subjectName + '，选择课程：' + domainName+ '，选择主题：' + topicName);
        // 主题所有统计数据
        var topicStatistics; 
        $.ajax({
            type : "GET",
            url : ip + "/StatisticsAPI/getTopicDetail?domainName=数据挖掘&topicName=DBSCAN",
            datatype : "json",
            async : false,
            success : function(data, status){
                topicStatistics = data;
                // console.log(topicStatistics);
            }
        });
        // echarts画图
        var myChart = echarts.init(document.getElementById('toipicDetailStatistics'));
        option = {
            tooltip: {
                trigger: 'item',
                formatter: "{a} <br/>{b}: {c} ({d}%)"
            },
            legend: {
                // orient: 'vertical',
                orient: 'horizontal',
                x: 'center',
                y: 'bottom',
                // itemWidth: 14,
                // itemHeight: 14,
                data: topicStatistics.facets
            },
            title: {
                text: '学科：计算机科学，课程：数据挖掘，主题：DBSCAN',
                textStyle: {
                    color:'#000',
                    fontSize:18,
                    fontWeight:'bold',
                    
                },
                // subtext:'作者：从零开始',
                subtextStyle: {
                    color:'#000',
                },
            },
            toolbox:{
                show: true,
                orient: 'vertical',
                x: 'right',
                y: 'center',
                feature: {
                    dataZoom: {
                        yAxisIndex: 'none'
                    },
                    dataView: {
                        readOnly: false
                    },
                    magicType: {
                        type: ['line', 'bar']
                    },
                    restore: {},
                    saveAsImage: {}
                }
            },
            series: [
                {
                    name:'DBSCAN',
                    type:'pie',
                    // hoverAnimation: false,
                    // legendHoverLink:false,
                    selectedMode: 'single',
                    radius: [0, '30%'],

                    label: {
                        normal: {
                            position: 'inner'
                        }
                    },
                    labelLine: {
                        normal: {
                            show: false
                        }
                    },
                    data: topicStatistics.totals
                },
                {
                    name: "主题：DBSCAN",
                    type:'pie',
                    radius: ['40%', '55%'],

                    label: {
                        normal: {
                            formatter: '{a|{a}}{abg|}\n{hr|}\n  {b|{b}：}{c|{c}}  {per|{d}%}  ',
                            backgroundColor: '#eee',
                            borderColor: '#aaa',
                            borderWidth: 1,
                            borderRadius: 4,
                            // shadowBlur:3,
                            // shadowOffsetX: 2,
                            // shadowOffsetY: 2,
                            // shadowColor: '#999',
                            // padding: [0, 7],
                            rich: {
                                a: {
                                    color: '#999',
                                    fontSize: 12,
                                    lineHeight: 22,
                                    align: 'center'
                                },
                                // abg: {
                                //     backgroundColor: '#333',
                                //     width: '100%',
                                //     align: 'right',
                                //     height: 22,
                                //     borderRadius: [4, 4, 0, 0]
                                // },
                                hr: {
                                    borderColor: '#aaa',
                                    width: '100%',
                                    borderWidth: 0.5,
                                    height: 0
                                },
                                b: {
                                    fontSize: 14,
                                    lineHeight: 33
                                },
                                c: {
                                    fontSize: 14,
                                    lineHeight: 33
                                },
                                per: {
                                    color: '#eee',
                                    backgroundColor: '#334455',
                                    fontSize: 13,
                                    padding: [2, 4],
                                    borderRadius: 2
                                }
                            }
                        }
                    },

                    data: topicStatistics.details
                }
            ]
        };
        myChart.setOption(option);
        /**
         * 主题详细信息统计：统计主题下的每个分面的碎片信息等（结束）
         */




});