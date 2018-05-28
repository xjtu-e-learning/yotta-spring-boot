
//杨宽添加，显示分面树函数
//
//只显示树干
function DisplayTrunk(dataset){

    //document.getElementById("RightfacetTree").innerHTML='';
   $("#facetTreeDiv").empty();
    var datas = []; 
    multiple=1;
    datas.push(dataset);
    //分面树所占空间大小
    svg = d3.select("div#facetTreeDiv")
                .append("svg")
                .attr("width", $("#facetTreeDiv").width() * multiple)
                .attr("height",$("#facetTreeDiv").height() * multiple);
    //分面树的位置
    $("svg").draggable();   
    var seed = {x: $("#facetTreeDiv").width()*0.5* multiple, y:($("#facetTreeDiv").height()-60)* multiple, name:dataset.topicName}; 
    var tree = buildTree(dataset, seed4, multiple);
    draw_trunk(tree, seed, svg, multiple);    
}
//显示树干和树枝
function DisplayBranch(dataset){
    $("#facetTreeDiv").empty();
    var datas = []; 
    multiple=0.9;
    datas.push(dataset);
    //分面树所占空间大小
    svg = d3.select("div#facetTreeDiv")
                .append("svg")
                .attr("width", $("#facetTreeDiv").width())
                .attr("height",$("#facetTreeDiv").height());
    //分面树的位置    
    $("svg").draggable();
    var seed = {x: $("#facetTreeDiv").width()*0.5, y: $("#facetTreeDiv").height()-30, name:dataset.topicName}; 
    var tree = buildBranch(dataset, seed, multiple);
    draw_tree(tree, seed, svg, multiple);
    //对分面树进行缩放
    $("div#facetTreeDiv").bind('mousewheel', function(evt) {
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
        svg = d3.select("div#facetTreeDiv")
                    .append("svg")
                    .attr("width", w * multiple)
                    .attr("height", h * multiple);
        var seed0 = {x: $("#facetTreeDiv").width()*0.5, y: $("#facetTreeDiv").height()-30, name:dataset.name};
        var tree0 = buildBranch(dataset, seed0, multiple);
        draw_tree(tree0, seed0, svg, multiple);
    }); 
    /*****************************************************/ 
}




// function addFacet(){
//     var nowtype=document.getElementById("nowtype").innerText;
//     console.log(nowtype);
//     if(nowtype=="主题"){
//         $.ajax({
//              type: "GET",
//              url: ip+"/FacetAPI/createFacet1",
//              data: {ClassName:nowOperateClass,TermName:nowOperateTopic,FacetName:$("input[name='FacetName']").val()},
//              dataType: "json",
//              async:false,
//              success: function(data){
//                          alert(data.success);
//                       }
//          });
//     }
//     else if(nowtype=="一级分面"){
//         $.ajax({
//              type: "GET",
//              url: ip+"/FacetAPI/createFacet2",
//              data: {ClassName:nowOperateClass,TermName:nowOperateTopic,Facet1Name:nowOperateFacet1,Facet2Name:$("input[name='FacetName']").val()},
//              dataType: "json",
//              async:false,
//              success: function(data){
//                          alert(data.success);
//                       }
//          });
//     }
//     else{
//         $.ajax({
//              type: "GET",
//              url: ip+"/FacetAPI/createFacet3",
//              data: {ClassName:nowOperateClass,TermName:nowOperateTopic,Facet1Name:nowOperateFacet1,Facet2Name:nowOperateFacet2,Facet3Name:$("input[name='FacetName']").val()},
//              dataType: "json",
//              async:false,
//              success: function(data){
//                          alert(data.success);
//                       }
//          });
//     }
// }



// function updateFacet(){
//     var name=document.getElementById("facet_name").innerText;
//     var layer=document.getElementById("facet_layer").innerText;
//     if(layer=="1"){
//         $.ajax({
//              type: "GET",
//              url: ip+"/FacetAPI/updataFacet1",
//              data: {ClassName:nowOperateClass,TermName:nowOperateTopic,FacetName:name,NewFacetName:$("input[name='NewFacetName']").val()},
//              dataType: "json",
//              async:false,
//              success: function(data){
//                          alert(data.success);
//                       }
//          });
//     }
//     else if(layer=="2"){
//         $.ajax({
//              type: "GET",
//              url: ip+"/FacetAPI/updataFacet2",
//              data: {ClassName:nowOperateClass,TermName:nowOperateTopic,FacetName:name,NewFacetName:$("input[name='NewFacetName']").val()},
//              dataType: "json",
//              async:false,
//              success: function(data){
//                          alert(data.success);
//                       }
//          });
//     }
//     else{
//         $.ajax({
//              type: "GET",
//              url: ip+"/FacetAPI/updataFacet3",
//              data: {ClassName:nowOperateClass,TermName:nowOperateTopic,FacetName:name,NewFacetName:$("input[name='NewFacetName']").val()},
//              dataType: "json",
//              async:false,
//              success: function(data){
//                          alert(data.success);
//                       }
//          });
//     }
// }

// function deleteFacet(){
//     var name=document.getElementById("facet_name").innerText;
//     var layer=document.getElementById("facet_layer").innerText;
//     if(layer=="1"){
//         $.ajax({
//              type: "GET",
//              url: ip+"/FacetAPI/deleteFacet1",
//              data: {ClassName:nowOperateClass,TermName:nowOperateTopic,FacetName:name},
//              dataType: "json",
//              async:false,
//              success: function(data){
//                          alert(data.success);
//                       }
//          });
//     }
//     else if(layer=="2"){
//         $.ajax({
//              type: "GET",
//              url: ip+"/FacetAPI/deleteFacet2",
//              data: {ClassName:nowOperateClass,TermName:nowOperateTopic,FacetName:name},
//              dataType: "json",
//              async:false,
//              success: function(data){
//                          alert(data.success);
//                       }
//          });
//     }
//     else{
//         $.ajax({
//              type: "GET",
//              url: ip+"/FacetAPI/deleteFacet3",
//              data: {ClassName:nowOperateClass,TermName:nowOperateTopic,FacetName:name},
//              dataType: "json",
//              async:false,
//              success: function(data){
//                          alert(data.success);
//                       }
//          });
//     }
// }


function drag(ev){
    ev.dataTransfer.setData("facet",ev.target.id);
    // console.log(ev);
    // console.log(ev.target.id);
}

function allowDrop(ev)
{
    ev.preventDefault();
}

function dropFacet1(ev,id){
    ev.preventDefault();
    var facet=ev.dataTransfer.getData("facet");
    var FacetName=facet.split("_")[0];
    // console.log(FacetName);
    var array=id.split("_");
    var TermName=array[0];
    // console.log(nowOperateClass);
    // console.log(TermName);
    $.ajax({
             type: "GET",
             url: ip+"/FacetAPI/createFacet1",
             data: {ClassName:nowOperateClass,TermName:TermName,FacetName:FacetName},
             dataType: "json",
             async:false,
             success: function(data){
                         alert(data.success);
                      }
         });
}

function dropFacet2(ev,id){
    ev.preventDefault();
    var facet2=ev.dataTransfer.getData("facet");
    var Facet2Name=facet2.split("_")[0];
    var array=id.split("_");
    var TermName=array[0];
    var Facet1Name=array[1];
    $.ajax({
             type: "GET",
             url: ip+"/FacetAPI/createFacet2",
             data: {ClassName:nowOperateClass,TermName:TermName,Facet1Name:Facet1Name,Facet2Name:Facet2Name},
             dataType: "json",
             async:false,
             success: function(data){
                         alert(data.success);
                      }
         });
}

function dropFacet3(ev,id){
    ev.preventDefault();
    var facet3=ev.dataTransfer.getData("facet");
    var Facet3Name=facet3.split("_")[0];
    var array=id.split("_");
    var TermName=array[0];
    var Facet1Name=array[1];
    var Facet2Name=array[2];
    $.ajax({
             type: "GET",
             url: ip+"/FacetAPI/createFacet3",
             data: {ClassName:nowOperateClass,TermName:TermName,Facet1Name:Facet1Name,Facet2Name:Facet2Name,Facet3Name:Facet3Name},
             dataType: "json",
             async:false,
             success: function(data){
                         alert(data.success);
                      }
         });
}