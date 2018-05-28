var myApp = angular.module("myApp", []);
myApp.controller('myCtrl', function($scope, $http) {
    // 页面加载时默认显示所有学科
    $http({
        url : ip + "/subject/getSubjectTree",
        method : 'get'
    }).success(function(response) {
        response = response["data"];
        $scope.subjects = response;
        $scope.subjects1 = response;
        $scope.subjects2 = response;
        // console.log(response);
    }).error(function(response){
        console.log('获取学科api出错...');
    });

    // bootstrap-inputfile上传文件
    // $("#file-0a").fileinput({
    //     theme: 'fa',
    //     uploadAsync: true,
    //     uploadUrl: ip + '/SpiderAPI/uploadFile', // you must set a valid URL here else you will get an error
    //     // allowedFileExtensions: ['jpg', 'png', 'gif'],
    //     enctype: 'multipart/form-data',
    //     overwriteInitial: false,
    //     maxFileSize: 1000,
    //     maxFilesNum: 1,
    //     //allowedFileTypes: ['image', 'video', 'flash'],
    //     showCaption: true,              //是否显示标题
    //     showUpload: true,               //是否显示上传按钮
    //     showRemove: true,               //是否显示移除按钮
    //     showPreview : true,             //是否显示预览按钮
    //     browseClass: "btn btn-primary", //按钮样式 
    //     dropZoneEnabled: false,         //是否显示拖拽区域
    //     allowedFileExtensions: ["xls", "xlsx"], //接收的文件后缀
    //     headers:{'Content-Type': 'application/x-www-form-urlencoded'},
    //     previewFileIcon: '<i class="glyphicon glyphicon-file"></i>',
    //     allowedPreviewTypes: ['image', 'html', 'text', 'video', 'audio', 'flash', 'object', 'application/excel', 'application/vnd.ms-excel', 'application/x-excel', 'application/x-msexcel'],
    //     previewFileIconSettings: {
    //         'doc': '<i class="fa fa-file-word-o text-primary"></i>',
    //         'xls': '<i class="fa fa-file-excel-o text-success"></i>',
    //         'ppt': '<i class="fa fa-file-powerpoint-o text-danger"></i>',
    //         'jpg': '<i class="fa fa-file-photo-o text-warning"></i>',
    //         'pdf': '<i class="fa fa-file-pdf-o text-danger"></i>',
    //         'zip': '<i class="fa fa-file-archive-o text-muted"></i>',
    //     },
    //     previewFileExtSettings: {
    //         'doc': function(ext) {
    //             return ext.match(/(doc|docx)$/i);
    //         },
    //         'xls': function(ext) {
    //             return ext.match(/(xls|xlsx)$/i);
    //         },
    //         'ppt': function(ext) {
    //             return ext.match(/(ppt|pptx)$/i);
    //         }
    //     },
    //     slugCallback: function (filename) {
    //         console.log(filename);
    //         return filename.replace('(', '_').replace(']', '_');
    //     }
    // }).on('fileuploaded', function(event, data, previewId, index) {
    //     var form = data.form, files = data.files, extra = data.extra,
    //         response = data.response, reader = data.reader;
    //     console.log('File uploaded triggered');
    // }).on('fileerror', function(event, data, msg) {  //一个文件上传失败
    //     console.log('文件上传失败！'+msg);
    // });

    // 上传excel文件
    $scope.submit = function() {
        //首先验证文件格式
        var fileName = $('#exampleInputFile').val();
        if (fileName === '') {
            alert('请上传excel文件');
            return false;
        }
        var fileType = (fileName.substring(fileName
                .lastIndexOf(".") + 1, fileName.length))
                .toLowerCase();
        if (fileType !== 'xls' && fileType !== 'xlsx') {
            alert('文件格式不正确，请上传excel文件！');
            return false;
        }

        // 开始上传文件
        var formdata = new FormData();
        formdata.append('file', document.getElementById("exampleInputFile").files[0]);
        // console.log(document.getElementById("exampleInputFile").files[0]);
        $http({
            method : 'post',
            data : formdata,
            url : ip + "/SpiderAPI/uploadFile",
            headers : {'Content-Type' : undefined},
            // 序列化 formdata object
            transformRequest : angular.identity
        }).success(function(data) {
            // console.log(data);
            alert(data.msg);
        });
    }

    // 爬取多门课程数据
    $scope.spiderMuti = function() {
        var fileName = document.getElementById("exampleInputFile").files[0].name;
        $.ajax({
            type : "GET",
            url : ip + "/SpiderAPI/startSpiders?fileName=" + fileName,
            datatype : "json",
            async : false,
            success : function(data, status){
                console.log(data);
            },
            error : function(data) {
                console.log(data.responseText);
            }
        });
    }

    // 爬取单个课程数据
    $scope.spiderSingle = function(subjectNameSpider, classNameSpider) {
        if (typeof subjectNameSpider === "undefined" || typeof classNameSpider === "undefined") {
            alert("请认真填写学科和课程！");
        } else {
            var domainStatistics;
            $.ajax({
                type : "GET",
                url : ip + "/SpiderAPI/startSingleSpider?SubjectName=" + subjectNameSpider + "&ClassName=" + classNameSpider,
                datatype : "json",
                async : false,
                success : function(data, status){
                    console.log(data);
                },
                error : function(data) {
                    alert(data.responseText);
                    console.log(data.responseText);

                }
            });
        }
    }

    /**
     * 根据维度：选择学科之后显示的课程信息
     */
    $scope.viewByDimension = function(subjectName) {
        if (typeof subjectName === "undefined") {
            alert("请选择学科！");
        } else {
            // console.log('课程统计2-选择学科：' + subjectName);
            // 通过API获取课程统计数据
            var domainStatistics; // 课程所有统计数据
            $.ajax({
                type : "GET",
                url : ip + "/statistics/getStatisticalInformationBySubjectName?subjectName=" + subjectName,
                datatype : "json",
                async : false,
                success : function(response, status){
                    data = response["data"];
                    domainStatistics = data;
                    // console.log(domainStatistics);
                }
            });

            // 计算不同维度的y轴的最大值
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
                        subtext: '学科：' + subjectName,
                        subtextStyle: {
                            color:'#000',
                        },
                    },

                    tooltip: {},
                    // legend: {
                    //     x: 'right',
                    //     data: ['第一产业', '第二产业', '第三产业', 'GDP', '金融', '房地产'],
                    //     selected: {
                    //         'GDP': false, '金融': false, '房地产': false
                    //     }
                    // },
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
                            // 'data':['数据结构', '\n计算机科学史', '3', '\n4', '5', '\n6', '7', '\n8', '9', '\n10', '11', '\n12', '13', '\n14', '15', '\n16', '17', '\n18', '19', '\n20'],
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
        }
    }
    // viewByDimension函数结束







    /**
     * 根据课程：选择学科之后显示的课程信息
     */
    $scope.viewByDomain = function(subjectName) {
        if (typeof subjectName === "undefined") {
            alert("请选择学科！");
        } else {
            // console.log('课程统计2-选择学科：' + subjectName);
            var domainStatistics; // 课程所有统计数据
            $.ajax({
                type : "GET",
                url : ip + "/statistics/getStatisticalInformationBySubjectName?subjectName=" + subjectName,
                datatype : "json",
                async : false,
                success : function(response, status){
                    data = response["data"];
                    domainStatistics = data;
                    // console.log(domainStatistics);
                }
            });

            var myChart = echarts.init(document.getElementById('classStatisticsByDomain'));
            // 指定图表的配置项和数据
            // var dataBeast = domainStatistics.topicList.slice(1);
            // var dataBeauty = [541, 513, 792, 701, 660, 729, 782, 660, 841, 521, 820, 578, 727, 598, 660, 841, 521, 820, 578, 727, 598, 792, 701, 660, 729, 513, 792, 701];
            var dataTopicList = domainStatistics.topicNumbers.slice(1);
            var dataFacetList = domainStatistics.facetNumbers.slice(1);
            var dataFragmentList = domainStatistics.assembleNumbers.slice(1);
            var dataDependencyList = domainStatistics.dependencyNumbers.slice(1);
            var xAxisData = domainStatistics.domainNames;
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
                    text: '学科：' + subjectName,
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
        }
    } 
    // viewByDomain函数结束


















    /**
     * 主题统计：选择学科和课程，显示主题统计数据
     */
    $scope.viewByTopic = function(subjectName, domainName) {
        if (typeof subjectName === "undefined") {
            alert("请选择学科！");
        } else if (typeof domainName === "undefined") {
            alert("请选择课程！");
        } else {
            // console.log('主题统计-选择学科：' + subjectName + '，选择课程：' + domainName);
            var domainStatistics; // 主题所有统计数据
            $.ajax({
                type : "GET",
                url : ip + "/statistics/getStatisticalInformationByDomainName?domainName=" + domainName,
                datatype : "json",
                async : false,
                success : function(response, status){
                    data = response["data"];
                    domainStatistics = data;
                    // console.log(domainStatistics);
                }
            });

            var myChart = echarts.init(document.getElementById('topicStatistics'));
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
                // color:['#019aba','#7a201f','#DB8E71', '#24936E', '#CC0033'],
                color: ['#24936E', '#F596AA', '#DB8E71', '#58B2DC', '#019aba','#7a201f', '#CC0033'],
                // backgroundColor:'#000',
                title: {
                    text: '学科：' + subjectName + '，课程：' + domainName,
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
        }
    }
    // viewByTopic 函数结束











    


    /**
     * 主题具体信息统计：选择学科、课程、主题，显示主题的具体信息
     */
    $scope.viewByTopicDetail = function(subjectName, domainName, topicName) {
        if (typeof topicName === "undefined") {
            alert("请选择主题！");
        } else {
            // console.log('主题统计-选择学科：' + subjectName + '，选择课程：' + domainName+ '，选择主题：' + topicName);
            // 主题所有统计数据
            var topicStatistics; 
            $.ajax({
                type : "GET",
                url : ip + "/statistics/getStatisticalInformationByDomainNameAndTopicName?domainName=" + domainName + "&topicName=" + topicName,
                datatype : "json",
                async : false,
                success : function(response, status){
                    data = response["data"];
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
                    data: topicStatistics.facetNames
                },
                title: {
                    text: '学科：' + subjectName + '，课程：' + domainName + '，主题：' + topicName,
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
                        name:'访问 来源',
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
                        name: "主题：" + topicName,
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
        }

    }
    // viewByTopicDetail 函数结束





});