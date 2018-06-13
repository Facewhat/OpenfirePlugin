<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@ page language="java" import="java.util.*"
	contentType="text/html;  charset=UTF-8" pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="pageID" content="fw-manage_user" />
<link href="bootstrap/css/bootstrap.css" type="text/css"
	rel="stylesheet">
<link href="bootstrap/css/fileinput.min.css" type="text/css"
	rel="stylesheet">
<link rel="stylesheet" href="bootstrap/css/bootstrap-table.css">
<script src="bootstrap/jquery/jquery.min.js"></script>
<script src="bootstrap/js/bootstrap.js"></script>
<script src="bootstrap/js/bootbox.min.js"></script>
<script src="bootstrap/js/bootstrap-treeview.js"></script>
<script src="bootstrap/js/fileinput.min.js"></script>
<script src="bootstrap/js/bootstrap-table.js"></script>
<script src="bootstrap/js/bootstrap-table-zh-CN.js"></script>
<script type="text/javascript">

	var globalData = null;
	var oldUserNickName = null;
	function showModal(method) {
		if ("addUser" == method)
		{
			var level = "1";
			$.ajax({
				url : "/plugins/fworgnization/orgnization/servlet",
				type : "post",
				data : {
					"requestMethod" : "getDepartments",
					"treeNodeLevel" : level
				},
				async:false,
				dataType : "json",
				success : function(data) {
					if (data[0].resultCode == "1") {
						$("#departmentSelect").empty();
						var list = data[0].groupInfoList;
						for(var i = 0; i < list.length; i++)
						{
							$("#departmentSelect").append('<option value="' + list[i].groupDisplayName + '">' + list[i].groupDisplayName + '</option>');
						}
					} 
					else 
					{
						console.log(data[0].info);
					}
				},
				error:function(result){
					//console.log(result);
				}
			});
			$('#moveUserTip').text('添加至部门：');
			$('#addUser').modal('show');
			
		}
	}

	$(function(){
		 
		var $result = $('#eventsResult');
		
		$('#table').on('expand-row.bs.table', (e, index, row, $detail) => {
			$detail.html('<p>这里是详细信息.</p>');
		});
		
		$("#toAddUser").click(function(){
			showModal("addUser");
			
		});
		
		$('#table').on('search.bs.table', function(e, searchText) {
			document.onkeyup = function(e) {//按键信息对象以函数参数的形式传递进来了，就是那个e  
				var code = e.charCode || e.keyCode; //取出按键信息中的按键代码(大部分浏览器通过keyCode属性获取按键代码，但少部分浏览器使用的却是charCode)  
				if (code == 13) {
					$.ajax({
						url : "/plugins/fworgnization/orgnization/servlet",
						type : "post",
						data : {
							"requestMethod" : "searchGroupOrUser",
							"searchCondition" : searchText
						},
						async:false,
						dataType : "json",
						success : function(data) {
							if (data[0].resultCode == "1") {
								globalData = data[0].groupAndUserInfo;
							} 
							else 
							{
								// 错误信息
							}
						},
						error:function(result){
							console.log(result);
						}
					});
					$('#table').bootstrapTable('resetSearch', '');
					$('#table').bootstrapTable('load', globalData);
					
				}
			}
			
		});
		
		function isNullString(str){
			if(str == null || str == "" || str === undefined)
				return true;
			return false;
			
		}
		$('#submit_addUser').click(function(){
			var nickName_addUser = $("#nickName_addUser").val();
			var shortSpelling_addUser = $("#shortSpelling_addUser").val();
			var fullSpelling_addUser = $("#fullSpelling_addUser").val();
			var displayname = $('#departmentSelect').find("option:selected").text();
			
			if(!isNullString(nickName_addUser) && !isNullString(shortSpelling_addUser)&& !isNullString(fullSpelling_addUser)&& !isNullString(displayname))
			{
				$.ajax({
				url : "/plugins/fworgnization/orgnization/servlet",
				type : "post",
				data : {
					"requestMethod" : "addUser",
					"nickName" : nickName_addUser,
					"shortSpelling" : shortSpelling_addUser,
					"fullSpelling" : fullSpelling_addUser,
					"displayname" : displayname
				},
				async:false,
				dataType : "json",
				success : function(data) {
					if (data[0].resultCode == "1") {
						console.log(data[0].info);
						$('#addUser').modal('hide');
					} 
					else 
					{
						// 错误信息
					}
				},
				error:function(result){
					console.log(result);
				}
			});
			}
			$("#errorInfo_addUser").$("#disPlay")
			.html(
					'<p>' + '输入不能为空' + '</p>');
		});
		
		$('#submit_reviseUser').click(function(){
			
			var newUserNickName = $("#name_reviseUser").val();
			var newShortPinYin = $("#shortSpelling_reviseUser").val();
			var newFullPinYin = $("#fullSpelling_reviseUser").val();
			var userNickName = oldUserNickName;
			if(!isNullString(newUserNickName) && !isNullString(newShortPinYin) && !isNullString(newFullPinYin)){
				
				$.ajax({
					url : "/plugins/fworgnization/orgnization/servlet",
					type : "post",
					data : {
						"requestMethod" : "reviseUser",
						"newUserNickName" : newUserNickName,
						"newShortPinYin" : newShortPinYin,
						"newFullPinYin" : newFullPinYin,
						"userNickName" : userNickName
					},
					async:false,
					dataType : "json",
					success : function(data) {
						if (data[0].resultCode == "1") {
							// js 修改json数据
							
							for(var i = 0;i < globalData.length ;i++){
								if(globalData[i].userNickName == userNickName){
									globalData[i].userNickName = newUserNickName;
									globalData[i].userName = newShortPinYin;
									globalData[i].fullPinYin = newShortPinYin;
									globalData[i].shortPinYin = newFullPinYin;
								}
							}
							$('#table').bootstrapTable('resetSearch', '');
							$('#table').bootstrapTable('load', globalData);
							$('#reviseUser').modal('hide');
						} 
						else 
						{
							$("#errorInfo_reviseUser")
							.html(
									'<p>' + data[0].info + '</p>');
						}
					},
					error:function(result){
						console.log(result);
					}
				});
			}
			else{
				$("#errorInfo_reviseUser")
				.html(
						'<p>' + '输入不能为空.' + '</p>');
			}
			
		});
		
	});
	
	function actionFormatter(value, row, index) {

		return [

				'<a class="edit ml10" href="javascript:void(0)" title="Edit">',
				'<i class="glyphicon glyphicon-edit"></i>',
				'</a>&nbsp;&nbsp;',
				'<a class="remove ml10" href="javascript:void(0)" title="Remove">',
				'<i class="glyphicon glyphicon-remove"></i>', '</a>' ].join('');
	}

	window.actionEvents = {

		'click .edit' : function(e, value, row, index) {
			$("#name_reviseUser").val(row.userNickName);
			$("#shortSpelling_reviseUser").val(row.shortPinYin);
			$("#fullSpelling_reviseUser").val(row.fullPinYin);
			
			oldUserNickName = row.userNickName;
			$('#reviseUser').modal('show');
			console.log(value, row, index);
		},
		'click .remove' : function(e, value, row, index) {
			bootbox.confirm({ 
				size: "small",
				title: "delete user",
				message: 'Are you sure to delete the user [ ' + row.userNickName + ' ] ?', 
				callback: function(result){
					/* result is a boolean; true = OK, false = Cancel*/ 
					if(result == true){
						$('#table').bootstrapTable('remove',{field:'action',values : [row.action]});
						$.ajax({
							url : "/plugins/fworgnization/orgnization/servlet",
							type : "post",
							data : {
								"requestMethod" : "deleteUser",
								"userNickName" : row.userNickName,
								"fromUserJsp" : "fromUserJsp"
							},
							async:false,
							dataType : "json",
							success : function(data) {
								if (data[0].resultCode == "1") {
									console.log(data[0].info);
								} 
								else 
								{
									console.log(data[0].info);
								}
							},
							error:function(result){
								//console.log(result);
							}
						});
						
					}
					else{
						
						return ;
					}
					
				}
			});
			console.log(value, row, index);
		}
	};
</script>
<title>通讯录管理</title>
</head>
<body>
	<div class="container">
		<div class="row clearfix">
			<div class="col-md-12 column">
				<div class="tabbable" id="tabs-52800">
					<ul class="nav nav-tabs" id="orgTab">
						<li class="active"><a href="#modalTab" data-toggle="tab"
							style="">人员管理</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="modalTab" style="width: 80%">
							<div class="panel panel-default" style="margin-top: 10px">
								<div class="panel-body" style="height: 400;">
									<div class="alert alert-info" id="eventsResult"
										style="padding-left: 45%;">User Manage</div>
									<div id="toolbar">
										<button id="toAddUser" class="btn btn-default">
											<i class="glyphicon glyphicon-plus">添加人员</i>
										</button>
									</div>
									<table id="table" data-toggle="table" data-height="300"
										data-search="true" data-toolbar="#toolbar"
										data-show-refresh="true" data-show-toggle="true"
										data-detail-view="true" data-sort-name="groupDisplayName"
										data-sort-order="desc">
										<thead>
											<tr>
												<th data-field="groupDisplayName" data-align='center'
													data-sortable="true">GroupName</th>
												<th data-field="userNickName" data-align='center'
													data-sortable="true">UserName</th>
												<th data-field="userName" data-align='center'
													data-sortable="true">Spelling</th>
												<th data-field="action" data-formatter="actionFormatter"
													data-events="actionEvents" data-align='center'>Action</th>
											</tr>
										</thead>
										<tbody>
										</tbody>
									</table>

								</div>

							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>


	<div class="modal fade" tabindex="-1" role="dialog" id="addUser">
		<div class="modal-dialog" role="document">
			<div class="modal-content" style="width: 80%;">

				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">添加人员</h4>
				</div>

				<div class="modal-body">
					<form>
						<div class="form-group">
							<input type="text" class="form-control" style="width: 50%;"
								id="nickName_addUser" placeholder="人员姓名">
						</div>
						<div class="form-group">
							<input type="text" class="form-control" style="width: 50%;"
								id="shortSpelling_addUser" placeholder="简拼">
						</div>
						<div class="form-group">
							<input type="text" class="form-control" style="width: 50%;"
								id="fullSpelling_addUser" placeholder="全拼">
						</div>
					</form>
					<div class="row">
						<div class="col-xs-6">
							<div class="form-group">
								<p id="moveUserTip" style="margin-top: 15px; margin-left: 10px;"></p>
								<select class="selectpicker form-control" id="departmentSelect"
									data-width="50%" style="width: 109%">

								</select>
							</div>
						</div>
					</div>
				</div>
				
				<p id="errorInfo_addUser"></p>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="submit_addUser">保存</button>
				</div>

			</div>
		</div>
	</div>


		<div class="modal fade" tabindex="-1" role="dialog" id="reviseUser">
		<div class="modal-dialog" role="document">
			<div class="modal-content" style="width: 80%;">

				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">修改信息</h4>
				</div>

				<div class="modal-body">
					<form>
						<div class="form-group">
							<label>名称：</label> <input type="text"
								class="form-control" style="width: 50%;" id="name_reviseUser"
								placeholder="名称">
						</div>
						<div class="form-group">
							<label>简拼：</label> <input type="text"
								class="form-control" style="width: 50%;"
								id="shortSpelling_reviseUser" placeholder="简拼">
						</div>
						<div class="form-group">
							<label>全拼：</label> <input type="text"
								class="form-control" style="width: 50%;"
								id="fullSpelling_reviseUser" placeholder="全拼">
						</div>
						<p id="errorInfo_reviseUser"></p>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="submit_reviseUser">保存</button>
				</div>

			</div>
		</div>
	</div>

</body>
</html>