

//杨宽添加，显示分面树函数
//显示完整的一棵树（树干、树枝、树叶）
function displayTree(dataset){
    $("#fragmentTreeDiv").empty();
    var datas = []; 
    multiple=0.7;
    datas.push(dataset);
    //分面树所占空间大小
    svg = d3.select("div#fragmentTreeDiv")
                .append("svg")
                .attr("width", $("#fragmentTreeDiv").width())
                .attr("height",$("#fragmentTreeDiv").height());
    //分面树的位置    
    $("svg").draggable();
    var seed = {x: $("#fragmentTreeDiv").width()*0.5, y: $("#fragmentTreeDiv").height()-30, name:dataset.topicName}; 
    var tree = buildTree(dataset, seed, multiple);
    draw_tree(tree, seed, svg, multiple);
}   
