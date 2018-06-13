<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@ page language="java" import="java.util.*"
	contentType="text/html;  charset=UTF-8" pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="pageID" content="fw-org-view" />

<link href="bootstrap/css/bootstrap.css" type="text/css"
	rel="stylesheet">
<link href="bootstrap/css/fileinput.min.css" type="text/css"
	rel="stylesheet">
<style>
#uploadModal {
	left: 65%;
	top: 50%;
	transform: translate(-50%, -50%);
	min-width:80%;/*这个比例可以自己按需调节*/
	
}
</style>
<script src="bootstrap/jquery/jquery.min.js"></script>
<script src="bootstrap/js/bootstrap.js"></script>
<script src="bootstrap/js/bootbox.min.js"></script>
<script src="bootstrap/js/bootstrap-treeview.js"></script>
<script src="bootstrap/js/fileinput.min.js"></script>
<script src="bootstrap/js/zh.js"></script>
<script type="text/javascript">
	var globalName;
	var globalLevel;
	var globalOrgTreeJson;
	var globalNode;
	function showModal(method) {
		if ("addDepartment" == method)
		{
			$('#addDepartment').modal('show');
		}
		else if("reviseDepartment" == method)
		{
			var groupDisplayName = globalName;
			$.ajax({
				url : "/plugins/fworgnization/orgnization/servlet",
				type : "post",
				data : {
					"requestMethod" : "getDepartmentInfo",
					"groupDisplayName" : groupDisplayName
				},
				async:false,
				dataType : "json",
				success : function(data) {
					if (data[0].resultCode == "1") {
						$("#name_reviseDepartment").val(data[0].groupInfoList[0].groupDisName);
						$("#spelling_reviseDepartment").val(data[0].groupInfoList[0].groupName);
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
			
			$('#reviseDepartment').modal('show');

		}
		else if("deleteDepartment" == method)
		{
			var groupDisplayName = globalName;	
			bootbox.confirm({ 
				size: "small",
				title: "delete department",
				message: 'Are you sure to delete the department [ ' + groupDisplayName + ' ] ?', 
				callback: function(result){
					/* result is a boolean; true = OK, false = Cancel*/ 
					if(result == true){
						window.location.href = "orgnization/servlet?groupDisplayName=" + groupDisplayName + "&requestMethod="+"deleteDepartment";
					}
					else{
						return;
					}
				}
					
			});
		}
		else if("reviseUser" == method)
		{
			
			var userNickName = globalName;
			$.ajax({
				url : "/plugins/fworgnization/orgnization/servlet",
				type : "post",
				data : {
					"requestMethod" : "getUserInfo",
					"userNickName" : userNickName
				},
				async:false,
				dataType : "json",
				success : function(data) {
					if (data[0].resultCode == "1") {
						$("#name_reviseUser").val(data[0].userInfoList[0].userNickName);
						$("#shortSpelling_reviseUser").val(data[0].userInfoList[0].shortPinYin);
						$("#fullSpelling_reviseUser").val(data[0].userInfoList[0].fullPinYin);

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
			
			$('#reviseUser').modal('show');
		}else if("moveUser" == method)
		{
			var level = globalLevel;
			
			// 为了 扩展更多层次考虑暂时好这么写
			if(level == "2")
			{
				level = "1";
			}
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
			
			$('#moveUserTip').text('移动 [ ' + globalName + ' ] 至 :');
			$('#moveUser').modal('show');
			
		}else if("deleteUser" == method)
		{
			var userNickName = globalName;	
			bootbox.confirm({ 
				size: "small",
				title: "delete user",
				message: 'Are you sure to delete the user [ ' + userNickName + ' ] ?', 
				callback: function(result){
					/* result is a boolean; true = OK, false = Cancel*/ 
					if(result == true){
						
						window.location.href = "orgnization/servlet?userNickName=" + userNickName + "&requestMethod="+"deleteUser";
					}
					else{
						return ;
					}
				}
					
			});
			
		}
	}
	
	$(function() {
		
		var $searchableTree;
		function updateOrgTree(orgTreeData){
			$searchableTree = $('#tree').treeview({
				collapseIcon : "glyphicon glyphicon-collapse-up",
				expandIcon : "glyphicon glyphicon-collapse-down",
				nodeIcon : "glyphicon glyphicon-user",
				showTags : true,
				onNodeSelected : function(event, node) {
					
					globalName = node.text;
					globalLevel = getNodeLevel(globalName);
					globalNode = node;
					if (globalLevel == "0")
						loadLevel0();
					else if (globalLevel == "1")
						loadLevel1();
					else if (globalLevel == "2")
						loadLevel2();
					else if (globalLevel == "-1")
						displayError(data[0].info);
					else
						return;
				},
				data : orgTreeData
			});
		}
		$(window).load(function() {

			$.ajax({
				url : "/plugins/fworgnization/orgnization/servlet",
				type : "post",
				data : {
					"requestMethod" : "loadOrgTree"
				},
				dataType : "json",
				success : function(data) {

					if (data[0].resultCode == "1") {
						globalOrgTreeJson = data[0].orgTreeJson;
						updateOrgTree(globalOrgTreeJson);
					}
					if (data[0].resultCode == "-1") {
						// 本该显示出错信息  暂时没做。
						console.log(data[0].info);
						return;
					}
				}
			});
		});
		
		function loadLevel0() {
			$("#disPlay")
					.html(
							'<div><a href="javascript:void(0);" onclick=showModal("addDepartment") >添加部门</a></div>');

		}

		function loadLevel1() {

			$("#disPlay")
					.html(
							'<div><a href="javascript:void(0);" onclick=showModal("reviseDepartment") >部门信息修改</a></div>'
									+ '<div style="margin-top:8px;"><a href="javascript:void(0);" onclick=showModal("deleteDepartment") >删除部门</a></div>');

		}

		function loadLevel2() {

			$("#disPlay")
					.html(
							'<div><a href="javascript:void(0);" onclick=showModal("reviseUser") >成员信息修改</a></div>'
									+ '<div style="margin-top:8px;"><a href="javascript:void(0);" onclick=showModal("moveUser") >移动成员</a></div>'
									+ '<div style="margin-top:8px;"><a href="javascript:void(0);" onclick=showModal("deleteUser") >删除成员</a></div>');

		}
		
		function displayError(err) {
			$("#disPlay")
					.html(
							'<p>' + err + '</p>');

		}

		function getNodeLevel(globalName) {
			// ajax 请求得到 level，bootstrap-treeview 没有获得 level 的方法，只能异步请求从后台得到层次
			$.ajax({
				url : "/plugins/fworgnization/orgnization/servlet",
				type : "post",
				data : {
					"requestMethod" : "getOrgTreeLevel",
					"nodeName" : globalName
				},
				async:false,
				dataType : "json",
				success : function(data) {
					if (data[0].resultCode == "1") {
						globalLevel = data[0].nodeLevel;
					} 
					else 
					{
						console.log(data[0].info);
					}

				},
				error:function(result){
					console.log(result);
					
				}
			});

			return globalLevel;
		}
		
		$("#orgTab a").click(function(e){
			
			$(this).tab('show');
			$('#orgTab a').not(this).css('display','');
		});
		
		function isNullString(str){
			if(str == null || str == "" || str === undefined)
				return true;
			return false;
		}
		
		$("#submit_reviseDepartment").click(function(){
			var newGroupdisplayName = $("#name_reviseDepartment").val();
			var newGroupName = $("#spelling_reviseDepartment").val();
			var groupDisplayName = globalName;
			if(!isNullString(newGroupdisplayName) && !isNullString(newGroupName))
			{
				$.ajax({
					url : "/plugins/fworgnization/orgnization/servlet",
					type : "post",
					data : {
						"requestMethod" : "reviseDepartment",
						"newGroupdisplayName" : newGroupdisplayName,
						"newGroupName" : newGroupName,
						"groupDisplayName" : groupDisplayName
					},
					dataType : "json",
					success : function(data) {
						if (data[0].resultCode == "1") {
							// js 修改json数据
							var jsonObj = JSON.parse(globalOrgTreeJson);
							var nodes = jsonObj[0].nodes;
							for(var i=0;i<nodes.length;i++){
								if(nodes[i].text == groupDisplayName)
									nodes[i].text = newGroupdisplayName;
							}
							jsonObj[0].nodes = nodes;
							// bootstrap tree-view并没有addnode方法，只能重新加载树.
							updateOrgTree(jsonObj);
							
							// 添加数据成功 关闭模态框
							$('#reviseDepartment').modal('hide');
						} 
						else 
						{
							$("#errorInfo_reviseDepartment")
							.html(
									'<p>' + data[0].info + '</p>');
						}
					},
					error:function(result){
						console.log(result);
						
					}
				});
			}
		});
		
		$("#submit_addDepartment").click(function(){
			
			var groupdisplayName = $("#name_addDepartment").val();
			var groupName = $("#spelling_addDepartment").val();
			
			if(!isNullString(groupdisplayName) && !isNullString(groupName)){
				
				$.ajax({
					url : "/plugins/fworgnization/orgnization/servlet",
					type : "post",
					data : {
						"requestMethod" : "addDepartment",
						"groupdisplayName" : groupdisplayName,
						"groupName" : groupName
					},
					
					dataType : "json",
					success : function(data) {
						if (data[0].resultCode == "1") {
							// js 修改json数据
							var node = {text:"",nodes:""};
							node.text = groupdisplayName;
							node.nodes = new Array();
							var jsonObj = JSON.parse(globalOrgTreeJson);
							jsonObj[0].nodes.push(node);
							
							// bootstrap tree-view并没有addnode方法，只能重新加载树.
							updateOrgTree(jsonObj);
							// 添加数据成功 关闭模态框
							$('#addDepartment').modal('hide');
						} 
						else 
						{
							$("#errorInfo_addDepartment")
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
				$("#errorInfo_addDepartment")
				.html(
						'<p>' + '输入不能为空.' + '</p>');
			}
		});
		
	$("#submit_reviseUser").click(function(){
			
			var newUserNickName = $("#name_reviseUser").val();
			var newShortPinYin = $("#shortSpelling_reviseUser").val();
			var newFullPinYin = $("#fullSpelling_reviseUser").val();
			var userNickName = globalName;
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
							var parentNose = $('#tree').treeview('getParent', globalNode);
							var arr = new Array();
							var node = {text:"",nodes:""};
							node.text = newUserNickName;
							node.nodes = arr;
							var jsonObj = JSON.parse(globalOrgTreeJson);
							
							var nodes = jsonObj[0].nodes; 
							for(var i=0 ;i <nodes.length; i++)
							{
								if(nodes[i].text == parentNose.text)
								{
									for(var j=0;j < nodes[i].nodes.length;j++){
										if(globalNode.text == nodes[i].nodes[j].text){
											nodes[i].nodes[j] = node;
										}
									}
								}
							}
							// bootstrap tree-view并没有addnode方法，只能重新加载树.
							jsonObj[0].nodes = nodes;
							updateOrgTree(jsonObj);
							// 添加数据成功 关闭模态框
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
	
	$("#submit_moveUser").click(function(){
		var toGroupDisplayName = $("#departmentSelect").val(); //获取Select选择的Value
		var parentNode = $('#tree').treeview('getParent', globalNode);
		var groupDisplayName = parentNode.text;
		var userNickName = globalName;
		window.location.href = "orgnization/servlet?groupDisplayName=" + groupDisplayName + "&toGroupDisplayName=" + toGroupDisplayName
				+ "&userNickName=" + userNickName + "&requestMethod="+"moveUser";
		
	});
	
	 $("#fileInput").fileinput({
	        showPreview: false,
			language: 'zh', //设置语言
			showUpload: true, //是否显示上传按钮 
			uploadAsync:true,
	        elErrorContainer: '#kartik-file-errors',
	        enctype : "multipart/form-data",
	        allowedFileExtensions: ["csv"],
	        uploadUrl: '/plugins/fworgnization/orgnization/servlet?requestMethod=uploadCSV'
	 });
	 $("#fileInput").on('fileuploaded',function(event,data,previewId,index){
			// var response = data.response;
			//console.log();
			$('#fileInput').fileinput('clear');
			$.ajax({
				url : "/plugins/fworgnization/orgnization/servlet",
				type : "post",
				data : {
					"requestMethod" : "loadOrgTree"
				},
				dataType : "json",
				success : function(data) {

					if (data[0].resultCode == "1") {
						globalOrgTreeJson = data[0].orgTreeJson;
						updateOrgTree(globalOrgTreeJson);
					}
					if (data[0].resultCode == "-1") {
						// 本该显示出错信息  暂时没做。
						console.log(data[0].info);
						return;
					}
				}
			});
	 });
	 
	 $('#fileInput').on('fileerror', function(event, data, msg) {
		 alert("error.");
	 });
	 
	 $("#downloadCSV").click(function(){
		 window.location.href = '/plugins/fworgnization/orgnization/servlet?requestMethod=downloadCSV';
	 });
		
	});
</script>

<title>企业通讯录</title>
</head>
<body>
	<div class="container">
		<div class="row clearfix">
			<div class="col-md-12 column">
				<div class="tabbable" id="tabs-52800">
					<ul class="nav nav-tabs" id="orgTab">
						<li class="active"><a href="#treeTab" data-toggle="tab">组织通讯树</a></li>
						<li><a href="#modalTab" data-toggle="tab">导入/导出</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="treeTab">
							<div class="row" style="margin-left: 10px; margin-top: 50px;">

								<div class="col-sm-3">
									<div id="tree" class=""></div>
								</div>

								<div class="col-sm-3">
									<div style="margin-top: 20px;">
										<div id="disPlay"></div>
									</div>
								</div>

							</div>
						</div>
						<div class="tab-pane" id="modalTab" style="width: 90%">
							<div class="panel panel-default" style="margin-top: 10px">
								<div class="page-header">
									<h3>企业通讯录CSV模板</h3>
									<div style="margin-left: 80%;">
										<button class="btn btn-primary" data-toggle="modal"
											data-target="#uploadModal">上传CSV</button>
										<button class="btn btn-primary" id="downloadCSV"
											>下载CSV</button>
									</div>
								</div>
								<div class="panel-body" style="height: 300px; width: 70%">
									<pre>
John,Doe,120 jefferson st.,Riverside, NJ, 08075</br>
Jack,McGinnis,220 hobo Av.,Phila, PA,09119</br>
"John ""Da Man""",Repici,120 Jefferson St.,Riverside, NJ,08075</br>
Stephen,Tyler,"7452 Terrace ""At the Plaza"" road",SomeTown,SD, 91234</br>
,Blankman,,SomeTown, SD, 00298</br>
"Joan ""the bone"", Anne",Jet,"9th, at Terrace plc",Desert City,CO,00123</br>
</pre>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade" tabindex="-1" role="dialog" id="addDepartment">
		<div class="modal-dialog" role="document">
			<div class="modal-content" style="width: 70%;">

				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">添加部门</h4>
				</div>

				<div class="modal-body">

					<form>
						<div class="form-group">
							<label for="">名称：</label> <input type="text" class="form-control"
								style="width: 50%;" id="name_addDepartment" placeholder="名称">
						</div>
						<div class="form-group">
							<label for="">简拼：</label> <input type="text" class="form-control"
								style="width: 50%;" id="spelling_addDepartment" placeholder="简拼">
						</div>
					</form>

					<p id="errorInfo_addDepartment"></p>
				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary"
						id="submit_addDepartment">保存</button>
				</div>

			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->


	<div class="modal fade" tabindex="-1" role="dialog"
		id="reviseDepartment">
		<div class="modal-dialog" role="document">
			<div class="modal-content" style="width: 70%;">

				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">部门信息修改</h4>
				</div>

				<div class="modal-body">
					<form>
						<div class="form-group">
							<label for="">名称：</label> <input type="text" class="form-control"
								style="width: 50%;" id="name_reviseDepartment" placeholder="名称">
						</div>
						<div class="form-group">
							<label for="">简拼：</label> <input type="text" class="form-control"
								style="width: 50%;" id="spelling_reviseDepartment"
								placeholder="简拼">
						</div>
						<p id="errorInfo_reviseDepartment"></p>
					</form>
				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary"
						id="submit_reviseDepartment">保存</button>
				</div>

			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<div class="modal fade" tabindex="-1" role="dialog" id="reviseUser">
		<div class="modal-dialog" role="document">
			<div class="modal-content" style="width: 70%;">

				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">成员信息修改</h4>
				</div>

				<div class="modal-body">
					<form>
						<div class="form-group">
							<label for="exampleInputEmail1">名称：</label> <input type="text"
								class="form-control" style="width: 50%;" id="name_reviseUser"
								placeholder="名称">
						</div>
						<div class="form-group">
							<label for="exampleInputPassword1">简拼：</label> <input type="text"
								class="form-control" style="width: 50%;"
								id="shortSpelling_reviseUser" placeholder="简拼">
						</div>
						<div class="form-group">
							<label for="exampleInputEmail1">全拼：</label> <input type="text"
								class="form-control" style="width: 50%;"
								id="fullSpelling_reviseUser" placeholder="全拼">
						</div>
						<p id="errorInfo_reviseUser"></p>
					</form>
				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary"
						id="submit_reviseUser">保存</button>
				</div>

			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<div class="modal fade" tabindex="-1" role="dialog" id="moveUser">
		<div class="modal-dialog" role="document">
			<div class="modal-content" style="width: 70%;">

				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">部门成员移动</h4>
				</div>

				<div class="modal-body">
					<form>
						<p id="moveUserTip" style="margin-top: 15px; margin-left: 10px;"></p>
						<div class="row">
							<div class="col-xs-6">
								<div class="form-group">
									<select class="selectpicker form-control" id="departmentSelect"
										data-width="auto">

									</select>
								</div>
							</div>
						</div>
						<p id="errorInfo_reviseMove"></p>
					</form>
				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="submit_moveUser">保存</button>
				</div>

			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<!-- Modal -->
	<div class="modal fade" id="uploadModal" tabindex="-1" role="dialog"
		aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content" style="width: 45%">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h5 class="modal-title" id="exampleModalLabel"
						style="font-weight: bold; margin-top: 20px">上传企业通讯录CSV</h5>

				</div>
				<div class="modal-body">
					<div class="file-loading">
						<input id="fileInput" name="fileCSV" type="file">
					</div>
					<div id="kartik-file-errors"></div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>

</body>
</html>