$(document).ready(function(){
    $.ajax({
        type: "GET",
        url: ip+"/domain/countDomains",
        data: {},
        dataType: "json",
        success: function(response){
            // console.log(data.ClassNum);
            $('#ClassNum').text("系统中目前共有"+response.data.domainNumber+"门课程");
        }
    });
});

function jumpClass(){
    // console.log("success");
}

/*function tianjiaClass(){
    var newclass=$("#newclass").val();
    // console.log(newclass);

    $.ajax({
        type: "GET",
        url: ip+"/domain/insertDomain",
        data: {domainName:$("input[name='ClassName']").val()},
        dataType: "json",
        success: function(data){
            // console.log(data);
            alert(data.success);
        }
    });
}*/

function updataClassName(){
    $.ajax({
        type: "GET",
        url: ip+"/domain/updateDomainByDomainName",
        data: {oldDomainName:nowOperateClass,newDomainName:$("input[name='NewClassName']").val()},
        dataType: "json",
        async:false,
        success: function(data){
            alert(data.data);
        }
    });
}



