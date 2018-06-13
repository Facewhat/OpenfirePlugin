<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="pageID" content="arc_audit" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="bootstrap/css/bootstrap.min.css" type="text/css"
	rel="stylesheet">
<script src="bootstrap/jquery/jquery.min.js"></script>
<script type="text/javascript">
$(function(){
		
	$("#test").click(function(){
		alert("this is test function.");
		$.ajax({
			url : 'archive/servlet?requestMethod=archiveAudit',
			type : "post",
			data : {
				"test" : "test"
			},
			dataType : "json",
			success : function(data) {
				alert("success test .");
			},
			error : function(e) {
				console.log(e);
			}
		});
	});
});
</script>
<title>消息审计</title>
</head>
<body>
	审计
	<button id="test">审计</button>>
</body>
</html>