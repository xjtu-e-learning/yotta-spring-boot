
function DisplayTrunk(dataset){

	//document.getElementById("RightfacetTree").innerHTML='';
   $("#RightfacetTree").empty();
	var datas = [];	
	multiple=1;
	datas.push(dataset);
	//分面树所占空间大小
	svg = d3.select("div#RightfacetTree")
				.append("svg")
				.attr("width", $("#RightfacetTree").width() * multiple)
				.attr("height",$("#RightfacetTree").height() * multiple);
	//分面树的位置
	//$("svg").draggable();	
	var seed4 = {x: $("#RightfacetTree").width()*0.5* multiple, y:($("#RightfacetTree").height()-60)* multiple, name:dataset.topicName}; 
	var tree4 = buildTree(dataset, seed4, multiple);
  draw_trunk(tree4, seed4, svg, multiple);	
}

function ObtainTrunk(subjectName){
	$.ajax({
             type: "POST",
             url: ip+"/topic/getCompleteTopicByNameAndDomainNameWithHasFragment",
             data: {
             	domainName:getCookie("NowClass"),
         		  topicName:subjectName,
              hasFragment:false
             },
             dataType: "json",
             success: function(response){
                  data = response['data'],
             			DisplayTrunk(data);
                     },
             error:function(XMLHttpRequest, textStatus, errorThrown){
          			//通常情况下textStatus和errorThrown只有其中一个包含信息
          			alert(textStatus);
       				}
        });
 
}

function LoadBranch(){
	$.ajax({
             type: "POST",
             url: ip+"/topic/getCompleteTopicByNameAndDomainNameWithHasFragment",
             data: {
             	domainName:getCookie("NowClass"),
             	topicName:SUBJECTNAME,
              hasFragment:false
             },
             dataType: "json",
             success: function(response){
                  data = response['data'],
             			DisplayBranch(data);
                     },
             error:function(XMLHttpRequest, textStatus, errorThrown){
          			//通常情况下textStatus和errorThrown只有其中一个包含信息
          			alert(textStatus);
       				}
        });
}

function DisplayBranch(dataset){
	document.getElementById("RightfacetTree").innerHTML='';
	var datas = [];	
	multiple=0.9;
	datas.push(dataset);
	//分面树所占空间大小
	svg = d3.select("div#RightfacetTree")
				.append("svg")
				.attr("width", $("#RightfacetTree").width())
				.attr("height",$("#RightfacetTree").height());
	//分面树的位置	
	$("svg").draggable();
	var seed = {x: $("#RightfacetTree").width()*0.5, y: $("#RightfacetTree").height()-30, name:dataset.topicName}; 
	var tree = buildBranch(dataset, seed, multiple);
  draw_tree(tree, seed, svg, multiple);
  /*****************************************************/
  //对分面树进行缩放
  
  $("div#RightfacetTree").bind('mousewheel', function(evt) {
    var temp = multiple;//判断是保持0.25或者1.25不变
    if( 0.3< multiple && multiple<1){
      multiple+=evt.originalEvent.wheelDelta/4800;
    }else if(multiple < 0.3){
      if(evt.originalEvent.wheelDelta>0){
        multiple+=evt.originalEvent.wheelDelta/4800;
      }
    }else{
      if(evt.originalEvent.wheelDelta<0){
        multiple+=evt.originalEvent.wheelDelta/4800;
      }
    }
    //if(multiple<0.25){return;}
    d3.selectAll("svg").remove(); //删除之前的svg
    svg = d3.select("div#RightfacetTree")
          .append("svg")
          .attr("width", $("#RightfacetTree").width() )
          .attr("height",$("#RightfacetTree").height() );
    //分面树根的位置 
    var root_x=$("#facetedTreeDiv").width()/2;
    var root_y=$("#facetedTreeDiv").height()-30; //
    //$("svg").draggable();
    var seed0 = {x:  $("#RightfacetTree").width()*0.5, y: ($("#RightfacetTree").height()-30), name:dataset.topicName};
    var tree0 = buildBranch(dataset, seed0, multiple);
    draw_tree(tree0, seed0, svg, multiple);

    //draw_road(multiple,svg);
  }); 
  /*****************************************************/   
}





