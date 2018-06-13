<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@ page language="java" import="java.util.*"
	contentType="text/html;  charset=UTF-8" pageEncoding="UTF-8"%>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="pageID" content="fw-orgMain" />
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

</style>
<script src="bootstrap-treeview/jquery/jquery.min.js"></script>
<script src="bootstrap-treeview/js/transition.min.js"></script>
<script src="bootstrap-treeview/js/bootstrap.js"></script>
<script src="bootstrap-treeview/js/bootstrap-treeview.js"></script>
<script type="text/javascript">
	$(function() {

		var level;
		var disName;
		var $searchableTree;
		
		// ajxa 异步请求 加载企业通讯录树形图
		$(window).load(function() {

			$.ajax({
				url : "/plugins/fworgnization/org/servlet",
				type : "get",
				data : {
					"method" : "getOrgTree"
				},
				dataType : "json",
				success : function(data) {

					$searchableTree = $('#tree').treeview({
						color : "#428bca",
						collapseIcon : "glyphicon glyphicon-collapse-up",
						expandIcon : "glyphicon glyphicon-collapse-down",
						nodeIcon : "glyphicon glyphicon-user",
						selectedIcon : "glyphicon glyphicon-record",
						showTags : true,
						onNodeSelected : function(event, node) {
							disName = node.text;
						},
						data : data
					});

				}
			});
		});
		
		
		// 修改 操作 
		$("#btn-detail").click(function(){
			if(name === undefined){
				alert("未选中节点");
				return ;
			}
			//alert(disName);
			//alert("org/servlet?disName="+disName+"&method="+"toInfoJsp");
			window.location.href="org/servlet?disName="+disName+"&method="+"toInfoJsp";
		});	
		
		// 树 搜索 等功能
		var search = function(e) {
	          var pattern = $('#input-search').val();
	          var options = {
	            ignoreCase: $('#chk-ignore-case').is(':checked'),
	            exactMatch: $('#chk-exact-match').is(':checked'),
	            revealResults: $('#chk-reveal-results').is(':checked')
	          };
	          var results = $searchableTree.treeview('search', [ pattern, options ]);

	          var output = '<p>' + results.length + ' matches found</p>';
	          $.each(results, function (index, result) {
	            output += '<p>- ' + result.text + '</p>';
	          });
	          $('#search-output').html(output);
	        }

	        $('#btn-search').on('click', search);
	        $('#input-search').on('keyup', search);

	        $('#btn-clear-search').on('click', function (e) {
	        	$searchableTree.treeview('clearSearch');
	          $('#input-search').val('');
	          $('#search-output').html('');
	        });
		
		
	});
</script>

<title>企业通讯录</title>
</head>
<body>

<!-- 
<h3>org servlet jsp!! <a href="/plugins/fworgnization/org/servlet">OrgServlet</a></h3>  
-->

	<div class="jq22-container" style="width:80%;	">
		<div class="container">

		<br/>
	<div class="row">
        <hr>
        <div class="col-sm-3">
          <h2>Search User</h2>
          <!-- <form> -->
            <div class="form-group">
              <label for="input-search" class="sr-only">Search Tree:</label>
              <input type="input" class="form-control" id="input-search" placeholder="input to search..." value="">
            </div>
            <div class="checkbox">
              <label>
                <input type="checkbox" class="checkbox" id="chk-ignore-case" value="false">
                Ignore Case
              </label>
            </div>
            <div class="checkbox">
              <label>
                <input type="checkbox" class="checkbox" id="chk-exact-match" value="false">
                Exact Match
              </label>
            </div>
            <div class="checkbox">
              <label>
                <input type="checkbox" class="checkbox" id="chk-reveal-results" value="false">
                Reveal Results
              </label>
            </div>
            <button type="button" class="btn btn-success" id="btn-search">Search</button>
            <button type="button" class="btn btn-default" id="btn-clear-search">Clear</button>
            <button type="button" class="btn btn-default" id="btn-detail">Alter</button>
          <!-- </form> -->
        </div>
		
        <div class="col-sm-3">
          <h2>Orgnization</h2>
          <div id="tree" ></div>
        </div>
        <div class="col-sm-3">
          <h2>Search Results</h2>
          <div id="search-output"></div>
        </div>
      </div>
			
			<br /> <br />
		</div>
	</div>

</body>
</html>