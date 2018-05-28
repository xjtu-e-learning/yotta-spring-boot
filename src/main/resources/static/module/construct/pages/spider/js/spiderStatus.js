/**
* 课程下每个主题的爬取情况
*/
$(document).ready(function(){ 
    var domainStatistics; // 主题所有统计数据
    $.ajax({
        type : "GET",
        url : ip + "/statistics/getStatisticalInformationByDomainName?domainName=" + getCookie("NowClass"),
        datatype : "json",
        async : false,
        success : function(response, status){
            data = response["data"];
            domainStatistics = data;
            // console.log(domainStatistics);
        }
    });

    var myChart = echarts.init(document.getElementById('pic0'));
    // 指定图表的配置项和数据
    var dataFacetList = domainStatistics.facetNumbers.slice(1);
    var dataFacetFirstList = domainStatistics.firstLayerFacetNumbers.slice(1);
    var dataFacetSecondList = domainStatistics.secondLayerFacetNumbers.slice(1);
    var dataFacetThirdList = domainStatistics.thirdLayerFacetNumbers.slice(1);
    var dataDependencyList = domainStatistics.dependencyNumbers.slice(1);
    var dataFragmentList = domainStatistics.assembleNumbers.slice(1);
    var xAxisData = domainStatistics.topicNames;
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
        color: ['#DB8E71', '#019aba','#7a201f', '#CC0033'],
        // color: ['#24936E', '#F596AA', '#DB8E71', '#58B2DC', '#019aba','#7a201f', '#CC0033'],
        // backgroundColor:'#000',
        title: {
            text: '领域：' + getCookie("NowClass"),
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
            data:['分面','认知关系','碎片','总数量'],
            // data:['分面','一级分面','二级分面','三级分面','认知关系','碎片','总数量'],
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
            // {
            //     type: 'bar',
            //     name:'一级分面',
            //     stack:'数量',
            //     data:dataFacetFirstList,
            //     label: {
            //         normal: {
            //             show:false,
            //             position: 'insideTop',
            //             offset:[0,20],
            //             textStyle:{
            //                color:'#000',
            //             },
            //         },
            //         emphasis: {
            //            textStyle:{
            //                color:'#000',
            //             }, 
            //         },
            //     },
            // },
            // {
            //     type: 'bar',
            //     name:'二级分面',
            //     stack:'数量',
            //     data:dataFacetSecondList,
            //     label: {
            //         normal: {
            //             show:false,
            //             position: 'insideTop',
            //             offset:[0,20],
            //             textStyle:{
            //                color:'#000',
            //             },
            //         },
            //         emphasis: {
            //            textStyle:{
            //                color:'#000',
            //             }, 
            //         },
            //     },
            // },
            // {
            //     type: 'bar',
            //     name:'三级分面',
            //     stack:'数量',
            //     data:dataFacetThirdList,
            //     label: {
            //         normal: {
            //             show:false,
            //             position: 'insideTop',
            //             offset:[0,20],
            //             textStyle:{
            //                color:'#000',
            //             },
            //         },
            //         emphasis: {
            //            textStyle:{
            //                color:'#000',
            //             }, 
            //         },
            //     },
            // },
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
})
// viewByTopic 函数结束
