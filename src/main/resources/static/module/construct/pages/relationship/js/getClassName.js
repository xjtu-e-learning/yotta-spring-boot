var app=angular.module('relationshipApp',[]);
app.controller("relationshipController",function($scope,$http){
    console.log('当前学科为：' + getCookie("NowSubject") + '，课程为：' + getCookie("NowClass"));
	$scope.NowSubject = getCookie("NowSubject");
	$scope.NowClass = getCookie("NowClass");
});