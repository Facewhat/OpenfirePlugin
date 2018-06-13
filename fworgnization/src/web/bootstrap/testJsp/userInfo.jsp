<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@ page language="java" import="java.util.*"
	contentType="text/html;  charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="pageID" content="fw-userInfo" />
<link href="bootstrap-treeview/css/bootstrap.css" type="text/css"
	rel="stylesheet">
<script src="bootstrap-treeview/jquery/jquery.min.js"></script>
<script src="bootstrap-treeview/js/transition.min.js"></script>
<script src="bootstrap-treeview/js/bootstrap.js"></script>
<script src="bootstrap-treeview/js/bootstrap-treeview.js"></script>
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
<script type="text/javascript">
	$(function(){
		
		var disName = null;
		var userInfo = null;
		$(window).load(function(){
			$("#UserInfoDis").show();
			$("#UserFormRevise").hide();

			if((${id}+"") == "2"){
				$.ajax({
					url : "/plugins/fworgnization/org/servlet",
					type : "get",
					data : {
						"method" : "getUserInfo"
					},
					dataType : "json",
					success : function(data) {
							
							if(data[0].resultCode == "404")
							{
								 $("#list").html("<p>"+data[0].info+"</p>");
							}
							if(data[0].resultCode == "2")
							{
								userInfo = data;
								var list = $("#list li");
								disName = data[0].userInfoList[0].userNickName;
								list[0].innerText = "姓名 : "+data[0].userInfoList[0].userNickName;
								list[1].innerText = "简拼 : "+data[0].userInfoList[0].shortPinYin;
								list[2].innerText = "全拼 : "+data[0].userInfoList[0].fullPinYin;
								list[3].innerText = "部门 : "+data[0].userInfoList[0].groupName;
								
							}

					}
				});
			}
			else
			{
				return ;
			}
		});
		
		$("#searchUser").click(function(){
			
			disName = $("#userNameInput").val();
			//alert(disName);
			if(disName == null || disName === undefined || disName == "")
			{
				$("#list").html("<p>"+"请输入搜索条件."+"</p>");
				return ;
			}
			window.location.href = "org/servlet?disName="+disName+"&method="+"toInfoJsp"; 
			
		});
		$("#toReviseUserInfo").click(function(){
			if(userInfo == null || userInfo === undefined || userInfo == "")
			{
				$("#list").html("<p>"+"成员信息为空."+"</p>");
				return ;
			}
			$("#UserInfoDis").hide();
			$("#UserFormRevise").show();
			
			$("#userNickName").val("");
			$("#shortPinYin").val("");
			$("#fullPinYin").val("");
			$("#userNickName").attr('placeholder',userInfo[0].userInfoList[0].userNickName);
			$("#shortPinYin").attr('placeholder',userInfo[0].userInfoList[0].shortPinYin);
			$("#fullPinYin").attr('placeholder',userInfo[0].userInfoList[0].fullPinYin);
			
		});
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
					"method" : "toReviseUserInfo","disName":disName,"userNickName":userNickName,
					"shortPinYin":shortPinYin,"fullPinYin":fullPinYin
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
							disName = userNickName;
							list[0].innerText = "姓名 : " + userNickName;
							list[1].innerText = "简拼 : " + shortPinYin;
							list[2].innerText = "全拼 : " + fullPinYin;
							$("#infoForm").html("<p>"+data[0].info+"</p>");
						}
				
				}
			});
			
		});
		
		$("#buttonBack").click(function(){
			$("#UserInfoDis").show();
			$("#UserFormRevise").hide();
		});
		
	});
	
</script>

<title>成员信息管理</title>
</head>
<body>
	<div class="container" id="UserInfoDis">
		<div style="margin-top: 30px;">
			<form class="navbar-form navbar-left">
				<div style="float: left;">
					<div class="panel panel-default">
						<div class="panel-body">
							<div style="float: left; padding-top: 32px;">
								<div class="form-group">
									<input type="text" class="form-control" id="userNameInput"
										placeholder="成员姓名">
								</div>
								<button type="button" id="searchUser" class="btn btn-success"
									style="margin-left: 30px;">Search</button>
							</div>
							<div style="float: left; margin-left: 300px; padding-top: 30px;">

								<div id="detailButton">
									<input class="btn btn-default" id="toReviseUserInfo"
										type="button" style="width: 120%; height: 37px;" value="修改信息">
								</div>

							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div style="width: 300px;"10px;">
			<label> </label>
		</div>
		<hr>
		<div
			style="width: 300px; margin-left: 29px; font-weight: bold; font-size: 18px;">
			<label>成员信息 </label>
		</div>
		<div style="width: 30%; margin-left: 27px; margin-top: 10px;">
			<ul id="list" class="list-group" style="">
				<li class="list-group-item">姓名：</li>
				<li class="list-group-item">简拼:</li>
				<li class="list-group-item">全拼：</li>
				<li class="list-group-item">部门：</li>
			</ul>
		</div>
	</div>

	<div class="container" id="UserFormRevise">
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