function updateTopicName(){
    $.ajax({
        type: "GET",
        url: ip+"/DomainTopicAPI/updateTermName",
        data: {ClassName:nowOperateSubject,TermName:nowOperateTopic,NewTermName:$("input[name='NewTopicName']").val()},
        dataType: "json",
        success: function(data){
            // console.log(data);
            alert(data.success);
        }
    });
}

function deleteTopic(){
    $.ajax({
        type: "GET",
        url: ip+"/topic/deleteTopicByNameAndDomainName",
        data: {domainName:nowOperateSubject,topicName:nowOperateTopic},
        dataType: "json",
        success: function(response){
            data = response["data"];
            // console.log(data);
            alert(nowOperateTopic+data.data);
        }
    });
}

function drag(ev){
    ev.dataTransfer.setData("topic_name",ev.target.id);
}

function allowDrop(ev)
{
    ev.preventDefault();
}

function drop(ev,id)
{
    // console.log(id);
    ev.preventDefault();
    var topic=ev.dataTransfer.getData("topic_name");
    // console.log(topic);
    $.ajax({
        type: "GET",
        url: ip+"/DomainTopicAPI/createTopic",
        data: {ClassName:id,TopicName:topic},
        dataType: "json",
        async:false,
        success: function(data){
            alert(data.success);
        }
    }); 

    
}
