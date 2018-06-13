<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="pageID" content="arc_search" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="bootstrap/css/bootstrap.min.css" type="text/css"
	rel="stylesheet">
<script src="bootstrap/jquery/jquery.min.js"></script>
<style>
.marginTop20 {
	margin-top: 20px;
}

.height470 {
	height: 470px;
	overflow-y: scroll;
}

.message {
	margin-bottom: 12px;
	position: relative;
}

.message:hover {
	background-color: #eee;
}

.message:hover>button {
	visibility: visible;
}

.inlineBlock {
	display: inline-block;
}

.fixRight {
	position: absolute;
	right: 20px;
	top: 0px;
	visibility: hidden;
}

.relative {
	position: relative;
}

.searchMsg {
	position: absolute;
	bottom: -12px;
	right: 50px;;
}

.timeSpan {
	float: right;
}
</style>
<script type="text/javascript">
	var globalMessageList;
	var globalConversationId = null;
	var globalFrom = null;
	var globalTo = null;
	function deleteMessage(id, to, from) {

		for (var i = 0; i < globalMessageList.length; i++) {
			if (id == globalMessageList[i].messageId)
				delete globalMessageList[i];
		}
		$.ajax({
			url : 'archive/servlet?requestMethod=deleteMessage',
			type : "post",
			data : {
				"messageId" : id
			},
			dataType : "json",
			success : function(data) {

			},
			error : function(e) {
				console.log(e);
			}
		});

		$('#messageList').html('');

		for (var i = 0; i < globalMessageList.length; i++) {
			if (globalMessageList[i] != undefined) {
				if (globalMessageList[i].direction == "to")
					direction = to;
				else
					direction = from;
				$('#messageList')
						.append(
								'<div class="message">'
										+ '<div class="text-success">'
										+ direction
										+ ' '
										+ globalMessageList[i].time
										+ '</div>'
										+ '<div class="inlineBlock">'
										+ globalMessageList[i].body
										+ '</div>'
										+ '<button type="button" class="btn btn-link fixRight" onclick=deleteMessage('
										+ globalMessageList[i].messageId
										+ ')>删除</button>' + '</div>');
			}
		}
	}

	function getMessage(id, to, from) {
		globalConversationId = id;
		globalFrom = from;
		globalTo = to;
		var direction;
		$
				.ajax({
					url : 'archive/servlet?requestMethod=getMessage',
					type : "post",
					data : {
						"conversationId" : id
					},
					dataType : "json",
					success : function(data) {
						if (data[0].resultCode == "1") {
							
							$('#messageList').html('');
							var list = data[0].messageList;
							globalMessageList = list;
							for (var i = 0; i < list.length; i++) {
								if (list[i].direction == "to")
									direction = to;
								else
									direction = from;
								$('#messageList')
										.append(
												'<div class="message">'
														+ '<div class="text-success">'
														+ direction
														+ ' '
														+ list[i].time
														+ '</div>'
														+ '<div class="inlineBlock">'
														+ list[i].body
														+ '</div>'
														+ '<button type="button" class="btn btn-link fixRight" onclick=deleteMessage('
														+ list[i].messageId
														+ ',"' + to
														+ '","' + from
														+ '")>删除</button>'
														+ '</div>');
							}
						} else {
							alert(data[0].info);
						}
					},
					error : function(e) {
						console.log(e);
					}
				});

	}

	$(function() {

		$('#searchConversation')
				.click(
						function() {

							var Sender = $("#Sender").val();
							var Receiver = $("#Receiver").val();
							var startDate = $("#startDate").val();
							var endDate = $("#endDate").val();

							
									$.ajax({
										url : 'archive/servlet?requestMethod=searchArchive',
										type : "post",
										data : {
											"Sender" : Sender,
											"Receiver" : Receiver,
											"startDate" : startDate,
											"endDate" : endDate
										},
										dataType : "json",
										success : function(data) {
											if (data[0].resultCode == "1") {
												$('#conversationList').html('');
												var list = data[0].conversationMapList;
												for (var i = 0; i < list.length; i++) {

													var time = list[i].start
															.split(' ');
													var to = list[i].to
															.split('@');
													var from = list[i].from
															.split('@');
													var resource = list[i].resource;
													if (resource == null)
														resource = ' ';
													$('#conversationList')
															.append(
																	'<a href="javascript:void(0);" onclick=getMessage('
																			+ list[i].id
																			+ ',"'
																			+ to[0]
																			+ '","'
																			+ from[0]
																			+ '") class="list-group-item list-group-item-action flex-column align-items-start ">'
																			+ '<div>'
																			+ '<span>'
																			+ list[i].id
																			+ '</span><span class="timeSpan">'
																			+ time[0]
																			+ '</span>'
																			+ '<div class="d-flex w-100 justify-content-between" >'
																			+ '<h6 class="mb-1 ">'
																			+ to[0]
																			+ '———'
																			+ from[0]
																			+ '</h6>'
																			+ '<small>'
																			+ resource
																			+ '</small>'
																			+ '</div>'
																			+ '</div>'
																			+ '</a>');
												}
											} else {
												alert(data[0].info);
											}
										},
										error : function(e) {
											console.log(e);
										}
									});
						});
		
		function isNullString(str){
			if(str == null || str == "" || str === undefined)
				return true;
			return false;
			
		}
		
		$('#searchKeyword').click(function(){
			var keyword = $('#keyword').val();
			var direction = null;
			if(!isNullString(keyword)){
				$.ajax({
					url : 'archive/servlet?requestMethod=searchKeyword',
					type : "post",
					data : {
						"conversationId" : globalConversationId,
						"keyword" : keyword
					},
					dataType : "json",
					success : function(data) {
						if (data[0].resultCode == "1") {
							
							$('#messageList').html('');
							var list = data[0].messageList;
							globalMessageList = list;
							for (var i = 0; i < list.length; i++) {
								if (list[i].direction == "to")
									direction = globalTo;
								else
									direction = globalFrom;
								$('#messageList')
										.append(
												'<div class="message">'
														+ '<div class="text-success">'
														+ direction
														+ ' '
														+ list[i].time
														+ '</div>'
														+ '<div class="inlineBlock">'
														+ list[i].body
														+ '</div>'
														+ '<button type="button" class="btn btn-link fixRight" onclick=deleteMessage('
														+ list[i].messageId
														+ ',"' + globalTo
														+ '","' + globalFrom
														+ '")>删除</button>'
														+ '</div>');
							}
						} else {
							alert(data[0].info);
						}
						
					},
					error : function(e) {
						console.log(e);
					}
				});
			}
		});
		
	});
</script>
<title>搜索消息档案</title>
</head>
<body>
	<div class="container">
		<div class="row relative">
			<div class="col-10 offset-1">
				<div class="row marginTop20">
					<div class="col">
						<div class="card">
							<div class="card-header">
								<div class="container">
									<div class="row">
										<div class="col-4">
											<div class="input-group mb-3">
												<input type="text" class="form-control" id="Sender"
													placeholder="发送者">
											</div>
										</div>
										<div class="col-4">
											<div class="input-group mb-3">
												<div class="input-group-prepend">
													<span class="input-group-text">开始</span>
												</div>
												<input type="date" id="startDate" class="form-control">
											</div>
										</div>

									</div>
									<div class="row">
										<div class="col">
											<div class="input-group mb-3">
												<input type="text" class="form-control" id="Receiver"
													placeholder="接收者">
											</div>
										</div>
										<div class="col">
											<div class="input-group mb-3">
												<div class="input-group-prepend">
													<span class="input-group-text">结束</span>
												</div>
												<input type="date" id="endDate" class="form-control">
											</div>
										</div>
										<div class="col">
											<button type="button" id="searchConversation"
												class="btn btn-primary btn-block">搜索</button>
										</div>
									</div>
								</div>
							</div>
							<div class="card-body">
								<div class="container">
									<div class="row">
										<div class="col-5 height470">
											<div class="list-group" id="conversationList"></div>
										</div>
										<div class="col height470" style="position: static;"
											id="messageList">

											
										</div>
										<div class="searchMsg">
												<div class="input-group input-group-sm mb-3">
													<input type="text" class="form-control" id="keyword" placeholder="关键字">
													<div class="input-group-append">
														<button class="btn btn-outline-secondary" id="searchKeyword" type="button">搜索</button>
													</div>
												</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row marginTop20">
					<div class="col">
						<nav aria-label="Page navigation">
						<ul class="pagination float-right">
							<li class="page-item"><a class="page-link" href="#"
								aria-label="Previous"> <span aria-hidden="true">&laquo;</span>
									<span class="sr-only">Previous</span>
							</a></li>
							<li class="page-item"><a class="page-link" href="#">1</a></li>
							<li class="page-item"><a class="page-link" href="#">2</a></li>
							<li class="page-item"><a class="page-link" href="#">3</a></li>
							<li class="page-item"><a class="page-link" href="#">4</a></li>
							<li class="page-item"><a class="page-link" href="#">5</a></li>
							<li class="page-item"><a class="page-link" href="#"
								aria-label="Next"> <span aria-hidden="true">&raquo;</span> <span
									class="sr-only">Next</span>
							</a></li>
						</ul>
						</nav>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>