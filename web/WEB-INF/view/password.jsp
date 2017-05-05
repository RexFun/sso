<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/common/inc_ctx.jsp"%>
<%@ include file="/common/inc_css.jsp"%>
<%@ include file="/common/inc_js.jsp"%>
<script type="text/javascript">
$(function(){
	$('input:password:first').focus(); //把焦点放在第一个文本框
	$('input').keypress(function (e) { //这里给function一个事件参数命名为e，叫event也行，随意的，e就是IE窗口发生的事件。
	    var key = e.which; //e.which是按键的值
	    if (key == 13) {
	    	$("#save").click();
	    }
	});
	$("#save").click(function(){
		if(!$chok.validator.check()) return; 
		var url = $("#pwdForm").attr('action');
		$.post(
			url, 
			{'account':$("#account").val(),'old_password':$("#old_password").val(),'new_password':$("#new_password").val()},
		  	function(result) {
				if(result.success){
					alert('密码修改成功');
					location.href="${ctx}/"+result.data.loginUrl;
				}else{
					$('#msgModalText').html(result.msg);
					$('#msgModal').modal('show');
				}
			}
		);
	});
});
</script>
</head>
<body class="hold-transition login-page">
	<div class="login-box">
		<div class="login-logo">
			<a href="#"><b>Admin</b><br/>统一密码修改平台</a>
		</div>
		<!-- /.login-logo -->
		<div class="login-box-body">
			<form class="dataForm" id="pwdForm" role="form" action="password2.action?service=${service}" method="post">
				<div class="form-group has-feedback">
					<input type="text" class="form-control" id="account" name="account" value="${account}" readonly="readonly"/>
				</div>
				<div class="form-group has-feedback">
					<input type="password" class="form-control" placeholder="Old Password" id="old_password" name="old_password" validate validate-rule-required/>
					<span class="glyphicon glyphicon-lock form-control-feedback"></span>
				</div>
				<div class="form-group has-feedback">
					<input type="password" class="form-control" placeholder="New Password" id="new_password" name="new_password" validate validate-rule-required/>
					<span class="glyphicon glyphicon-lock form-control-feedback"></span>
				</div>
			</form>
			<div class="row">
				<div class="col-xs-8">
				</div>
				<!-- /.col -->
				<div class="col-xs-4">
					<button type="submit" class="btn btn-primary btn-block btn-flat" id="save"><i class="glyphicon glyphicon-floppy-save"></i></button>
				</div>
				<!-- /.col -->
			</div>
		</div>
		<!-- /.login-box-body -->
		<div class="modal fade" id="msgModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title" id="msgModalTitle">提示</h4>
					</div>
					<div class="modal-body alert-danger" id="msgModalText"></div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					</div>
				</div><!-- /.modal-content -->
			</div><!-- /.modal-dialog -->
		</div><!-- /.modal -->
	</div>
</body>
</html>