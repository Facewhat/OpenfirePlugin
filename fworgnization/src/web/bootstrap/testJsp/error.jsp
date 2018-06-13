<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@ page language="java" import="java.util.*" contentType="text/html;  charset=UTF-8"
    pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="pageID" content="fw-error"/>

<style type="text/css">
#disPanel {
	padding-left: 10px;
	padding-top: 5px;
	float: left;
}

#detailButton {
	padding-right: 10px;
	float: right;
}
</style>
<link href="http://www.jq22.com/jquery/bootstrap-3.3.4.css" rel="stylesheet">
<script src="http://www.jq22.com/jquery/2.1.1/jquery.min.js"></script>
<script src="bootstrap-treeview/js/bootstrap-treeview.js"></script>
<script type="text/javascript">

	$(function() {
		$("#errortest").click(function() {
			alert("this is test.");
		});
		$("#errortestajax").click(function() {
			var name = "admin";
			var pass = "admin";

			$.ajax({
				url : "/plugins/fworgnization/user/servlet",
				type : "get",
				data : {
					"name" : name,
					"pass" : pass
				},
				dataType : "json",
				success : function(data) {
					
					alert(data[0].nodeId);
					$('#tree').treeview({
						color : "#428bca",
						collapseIcon:"glyphicon glyphicon-collapse-up",
						expandIcon:"glyphicon glyphicon-collapse-down",
						nodeIcon : "glyphicon glyphicon-user",
						showTags : true,
						data : data
					});
				}
			});
		});

		var defaultData = [ {
			text : 'Parent 1',
			href : '#parent1',
			tags : [ '4' ],
			nodes : [ {
				text : 'Child 1',
				href : '#child1',
				tags : [ '2' ],
				nodes : [ {
					text : 'Grandchild 1',
					href : '#grandchild1',
					tags : [ '0' ]
				}, {
					text : 'Grandchild 2',
					href : '#grandchild2',
					tags : [ '0' ]
				} ]
			}, {
				text : 'Child 2',
				href : '#child2',
				tags : [ '0' ]
			} ]
		}, {
			text : 'Parent 2',
			href : '#parent2',
			tags : [ '0' ]
		}, {
			text : 'Parent 3',
			href : '#parent3',
			tags : [ '0' ]
		}, {
			text : 'Parent 4',
			href : '#parent4',
			tags : [ '0' ]
		}, {
			text : 'Parent 5',
			href : '#parent5',
			tags : [ '0' ]
		} ];

		
		var json = '[' + '{' + '"text": "Parent 1",' + '"nodes": [' + '{'
				+ '"text": "Child 1",' + '"nodes": [' + '{'
				+ '"text": "Grandchild 1"' + '},' + '{'
				+ '"text": "Grandchild 2"' + '}' + ']' + '},' + '{'
				+ '"text": "Child 2"' + '}' + ']' + '},' + '{'
				+ '"text": "Parent 2"' + '},' + '{' + '"text": "Parent 3"'
				+ '},' + '{' + '"text": "Parent 4"' + '},' + '{'
				+ '"text": "Parent 5"' + '}' + ']';

		$('#tree').treeview({
			color : "#428bca",
			expandIcon : "glyphicon glyphicon-stop",
			collapseIcon : "glyphicon glyphicon-unchecked",
			nodeIcon : "glyphicon glyphicon-user",
			showTags : true,
			data : defaultData
		});

	});
</script>
<title>这里是错误页面</title>
</head>
<body>
<h1> let us get a hello world!</h1>
<button id="errortest" > click to trigger window.</button>
<button id="errortestajax" > click to trigger ajax.</button>
<div class="jq22-container">
	<div class="container">
    	<h1>Bootstrap Tree View</h1>
      	<br>
    	<div class="row">
        	<div class="col-sm-4">
          		<h2>Tags as Badges</h2>
          		<div id="tree" class=""></div>
        	</div>
      	</div>
      	
      	<br/>
      	<br/>
	</div>	
</div>
<!--  <div class="row">
				<div class="col-sm-3">

					<div id="tree"></div>
				</div>
				
				<div class="col-sm-4">

					<form class="navbar-form navbar-left">
						<div class="form-group">
							<input type="text" class="form-control" placeholder="Search">
						</div>
						<button type="submit" class="btn btn-default">Submit</button>

						<div class="panel panel-default"></div>
						<div class="panel panel-default">
							<div class="panel-body">
								<div id="disPanel">
									<label id="disInfo">sadf asdf </label>
								</div>
								<div id="detailButton">
									<input class="btn btn-default" type="button" value="Input">
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
			-->
<div class="jive-contentBoxHeader">jive-contentBoxHeader</div>  
<div class="jive-contentBox">jive-contentBox</div>  
</body>
</html>