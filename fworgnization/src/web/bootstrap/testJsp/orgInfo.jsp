<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@ page language="java" import="java.util.*"
	contentType="text/html;  charset=UTF-8" pageEncoding="UTF-8"%>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="pageID" content="fw-orgInfo" />
<link href="http://www.jq22.com/jquery/bootstrap-3.3.4.css"
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
<script type="text/javascript">
	$(function(){
		$(window).load(function(){
			if(){
				alert();
			}
			else{
				
			}
			
		});
		
	});
	
</script>
<script src="bootstrap-treeview/js/transition.min.js"></script>
<script src="bootstrap-treeview/js/bootstrap.js"></script>
<script src="http://www.jq22.com/jquery/2.1.1/jquery.min.js"></script>
<script src="bootstrap-treeview/js/bootstrap-treeview.js"></script>
<title>企业信息管理</title>
</head>
<body>
	<div class="container">

		

		<div>
			<form class="navbar-form navbar-left">
				<div style="float: left;">
					<div class="panel panel-default">
						<div class="panel-body">
							<div style="float: left; padding-top: 32px;">
								<div class="form-group">
									<input type="text" class="form-control" placeholder="部门名称">
								</div>
								<button type="submit" class="btn btn-success"
									style="margin-left: 30px;">Search</button>
							</div>
							<div style="float: left; margin-left: 300px; padding-top: 30px;">

								<div id="detailButton">
									<input class="btn btn-info" type="button"
										style="width: 120%; height: 37px;" value="添加部门">
								</div>

							</div>
						</div>
					</div>
				</div>
			</form>
		</div>



		<div style="width: 1000px">
			<table class="table table-hover" style="width: 50%;"></table>
		</div>
		<div>
			<hr>
			<table class="table table-hover"
				style="width: 65%; margin-left: 20px;">
				<tr>
					<th>#名称</th>
					<th>#简拼</th>
					<th>#人数</th>
					<th>#修改</th>
					<th>#删除</th>
				</tr>
				<tr>

					<td>???</td>
					<td>???</td>
					<td>???</td>
					<td><button type="button" class="btn btn-primary">
							<span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
						</button></td>
					<td><button type="button" class="btn btn-danger">
							<span class="glyphicon glyphicon-remove-circle"
								aria-hidden="true"></span>
						</button></td>
				</tr>
				<tr>

					<td>???</td>
					<td>???</td>
					<td>???</td>
					<td>???</td>
					<td>???</td>
				</tr>
				<tr>

					<td>???</td>
					<td>???</td>
					<td>???</td>
					<td>???</td>
					<td>???</td>
				</tr>

			</table>
		</div>



	</div>
</body>
</html>