<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@ page language="java" import="java.util.*"
	contentType="text/html;  charset=UTF-8" pageEncoding="UTF-8"%>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="pageID" content="fw-groupInfo" />
<!-- 
<link href="http://www.jq22.com/jquery/bootstrap-3.3.4.css" type="text/css"
	rel="stylesheet">
 -->
<link href="bootstrap-treeview/css/bootstrap.css" type="text/css"
	rel="stylesheet">

<style type="text/css">
.jq22-header {
	margin-bottom: 15px;
	font-family: "Segoe UI", "Lucida Grande", Helvetica, Arial,
		"Microsoft YaHei", FreeSans, Arimo, "Droid Sans",
		"wenquanyi micro hei", "Hiragino Sans GB", "Hiragino Sans GB W3",
		"FontAwesome", sans-serif;
}

.jq22-icon {
	color: #fff;
}

#disPanel {
	padding-left: 10px;
	padding-top: 5px;
	float: left;
}

#detailButton {
	padding-right: 40px;
	float: right;
}
</style>
<script src="bootstrap-treeview/jquery/jquery.min.js"></script>
<script src="bootstrap-treeview/js/transition.min.js"></script>
<script src="bootstrap-treeview/js/bootstrap.js"></script>
<script src="bootstrap-treeview/js/bootstrap-treeview.js"></script>

<script type="text/javascript">
	$(function(){
		var department;
		var disUserName;
		$(window).load(function(){
			$("#groupInfoPage").show();
			$("#UserInfoRevise").hide();
			if((${id}+"") == "1"){
				$.ajax({
					url : "/plugins/fworgnization/org/servlet",
					type : "get",
					data : {
						"method" : "getGroupInfo"
					},
					dataType : "json",
					success : function(data) {
							if(data[0].resultCode == "404")
							{
								 $("#userInfoTable").html("<p>"+data[0].info+"</p>");
							}
							if(data[0].resultCode == "1")
							{
								$("#groupDisName").val(data[0].groupInfoList[0].groupDisName);
								$("#groupName").val(data[0].groupInfoList[0].groupName);
								departmnet = data[0].groupInfoList[0].groupDisName;
								for(var i = 1;i<data[0].groupInfoList.length;i++)
								{
									var $tr = $('<tr>'+
									'<td>'+data[0].groupInfoList[i].userNickName+'</td>'+
									'<td>'+data[0].groupInfoList[i].userName+'</td>'+
									'<td><button type="button" class="btn btn-info"> <span class="glyphicon glyphicon-edit" aria-hidden="true"></span></button></td>'+
									'<td><button type="button" class="btn btn-danger"> <span class="glyphicon glyphicon-remove-circle" aria-hidden="true"></span></button></td>'+
									'</tr>');
									var $table = $("#userInfoTable");
									$table.append($tr);
									var userInfo = data[0].groupInfoList[i];
									$tr.children().eq(2).children().bind("click",userInfo,function(event){
										reviseUserInfo(event);
									});
									$tr.children().eq(3).children().bind("click",userInfo,function(event){
										
										var result = confirm("确认删除该成员？");
										if(result == true){
											deleteUserInfo(event);
										}
										else{
											return ;
										}
									});

								}
								
							}

					}
				});
			}
			else{
				return ;
			}
			
		});
		
		$("#reviseGroupInfo").click(function(){
			var temp =  $("#reviseGroupInfo").val();
			
			if(temp == "修改")
			{
				 department = $("#groupDisName").val();
				 $("#groupDisName").attr('readonly',false);
				 $("#groupName").attr('readonly',false);
				 $("#reviseGroupInfo").val("确认");
				 
			}
			else
			{
				var groupDisName = $("#groupDisName").val();
				var groupName = $("#groupName").val();
				if((groupDisName != null||groupDisName != "" )&&(groupName != null||groupName != "" )){
					

				$.ajax({
					url : "/plugins/fworgnization/org/servlet",
					type : "post",
					data : {
						"method" : "reviseGroupInfoByDisName", "disName":department,
						"groupName":groupName, "groupDisName":groupDisName
					},
					dataType : "json",
					success : function(data) {
						if(data[0].resultCode == "1"){
							window.location.href = "org/servlet?disName="+data[0].groupInfoList[0].groupDisName+"&method="+"toInfoJsp";
						}
						else{
							alert("修改失败，请检查输入的信息.")
							
						}
							
					}
				});
				
				}
			}
		});
		
		
		function deleteUserInfo(event){
			//alert(event.data.userNickName);
			$.ajax({
				url : "/plugins/fworgnization/org/servlet",
				type : "post",
				data : {
					"method" : "removeUserFromGroupInfoJsp","disName":department,"userNickName":event.data.userNickName
				},
				dataType : "json",
				success : function(data) {
					window.location.href = "org/servlet?disName="+departmnet+"&method="+"toInfoJsp";
				}
			});
			
		}
		
		$("#searchGroup").click(function(){
			alert($("#groupDisNameSearchInput").val());
			department = $("#groupDisNameSearchInput").val();
			if(department == null || department === undefined || department == "")
			{
				$("#list").html("<p>"+"请输入搜索条件."+"</p>");
				return ;
			}
			window.location.href = "org/servlet?disName="+department+"&method="+"toInfoJsp"; 
		});
		
		
		function reviseUserInfo(event){
			//alert(trNode.children().eq(0).text);
			disUserName = event.data.userNickName;
			$("#groupInfoPage").hide();
			$("#UserInfoRevise").show();
			
			$("#userNickName").val("");
			$("#shortPinYin").val("");
			$("#fullPinYin").val("");
			$("#userNickName").attr('placeholder',event.data.userNickName);
			$("#shortPinYin").attr('placeholder',event.data.shortPinYin);
			$("#fullPinYin").attr('placeholder',event.data.fullPinYin);
			
		}
		$("#reviseConfirm").click(function(){
			//window.location.href = "org/servlet?disName="+disName+"&method="+"toInfoJsp"; 
			var userNickName,shortPinYin,fullPiYin;
			
			if($("#userNickName").val() == "")
			{
				 $("#userNickName").val($("#userNickName").attr('placeholder'));
			}
			if($("#shortPinYin").val() == "")
			{
				$("#shortPinYin").val($("#shortPinYin").attr('placeholder'));
			}
			if($("#fullPinYin").val() == "")
			{
				$("#fullPinYin").val($("#fullPinYin").attr('placeholder'));
			}
			userNickName = $("#userNickName").val();
			shortPinYin = $("#shortPinYin").val();
			fullPinYin = $("#fullPinYin").val();
			
			$.ajax({
				url : "/plugins/fworgnization/org/servlet",
				type : "post",
				data : {
					"method" : "reviseUserInfoFromGroupInfoJsp","disName":department,"userNickName":userNickName,
					"shortPinYin":shortPinYin,"fullPinYin":fullPinYin,"disUserName":disUserName
				},
				dataType : "json",
				success : function(data) {
					
						if(data[0].resultCode == "404")
						{
							 $("#infoForm").html("<p>"+data[0].info+"</p>");
						}
						if(data[0].resultCode == "2")
						{
							var list = $("#list li");
							
							$("#infoForm").html("<p>"+data[0].info+"</p>");
						}
				
				}
			});
			
		});
		
		$("#buttonBack").click(function(){
			
			window.location.href = "org/servlet?disName="+departmnet+"&method="+"toInfoJsp";
		});
		
	});
	
</script>
<title>部门信息管理</title>
</head>
<body>
	<div class="container" id="groupInfoPage">

		<div>
			<form class="navbar-form navbar-left">
				<div style="float: left;">
					<div class="panel panel-default">
						<div class="panel-body">
							<div style="float: left; padding-top: 65px;">
								<div class="form-group">
									<input type="text" class="form-control" id="groupDisNameSearchInput" placeholder="部门名称">
								</div>
								<button type="submit" class="btn btn-success" id="searchGroup"
									style="margin-left: 30px;">Search</button>
							</div>
							<div style="float: left; margin-left: 50px; padding-top: 30px;">
								<div>
									<label id=""
										style="padding-left: 3%; font-size: 16px; font-weight: bold;">部门信息：</label>
								</div>
								<div id="disPanel">
									<div style="padding-bottom: 10px;">
										<label id="" style="font-size: 15px;"></label> <input
											type="text" class="form-control" id="groupDisName"
											style="width: 35%" placeholder="名称" readonly="true">
										<label id="groupDisName" style="font-size: 15px;"></label> <input
											type="text" class="form-control" id="groupName"
											style="width: 35%" placeholder="简拼" readonly="true">
									</div>
								</div>
								<div id="detailButton">
									<input class="btn btn-default" type="button" id="reviseGroupInfo"
										style="width: 120%; height: 37px;" value="修改">
								</div>

							</div>
						</div>
					</div>
				</div>
			</form>
		</div>

		<div style="width: 1000px">
			<table class="table table-hover" style="width: 50%;" ></table>
		</div>

		<div>
			<div>
				<button type="button" class="btn btn-default"
					style="margin-left: 780px">添加人员</button>
			</div>
			<hr>
			<table class="table table-hover" id="userInfoTable"
				style="width: 75%; margin-left: 20px;">
				<tr>
					<th>#用户名</th>
					<th>#简拼</th>
					<th>#修改</th>
					<th>#删除</th>
				</tr>

			</table>
		</div>
	</div>
	
	
		<div class="container" id="UserInfoRevise">
		<div style="width: 55px; margin-top: 30px">
			<input class="btn btn-default" id="buttonBack" type="button"
				style="height: 37px;" value="返回">
		</div>
		<form class="navbar-form navbar-left" id="infoForm"
			style="margin-left: 30px; margin-top: 100px;">
			<div style="float: left;">
				<div class="panel panel-default">
					<div class="panel-body">
						<div style="padding-top: 24px;">
							<div class="form-group">
								<input type="text" class="form-control" name="userNickName"
									id="userNickName" placeholder="昵称">
							</div>

						</div>
						<div style="padding-top: 20px;">
							<div class="form-group">
								<input type="text" class="form-control" name="shortPinYin"
									id="shortPinYin" placeholder="简拼">
							</div>

						</div>
						<div style="padding-top: 20px;">
							<div class="form-group">
								<input type="text" class="form-control" name="fullPinYin"
									id="fullPinYin" placeholder="全拼">
							</div>

						</div>

						<div style="float: left; margin-left: 250px; padding-top: 20px;">

							<div id="detailButton">
								<input class="btn btn-default" id="reviseConfirm" type="button"
									style="width: 110%; height: 37px;" value="确认">
							</div>

						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
</body>
</html>