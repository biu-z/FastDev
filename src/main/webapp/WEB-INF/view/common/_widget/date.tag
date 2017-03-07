	<link rel="stylesheet" href="${ctxPath}/static/assets/css/datepicker.css" />
	<script src="${ctxPath}/static/assets/js/date-time/bootstrap-datepicker.js"></script>		
	@ var _token = token!'';
	@ if (!isEmpty(value)){
	@ 	_token = "";	
	@ }
	<input type="text" id="${id}" name="${_token}${name}" ${required!} ${disabled!}  value="${value!}" placeholder="请输入${name!}" data-date-format="${format!'yyyy-mm-dd'}" class="form-control" />
	<script type="text/javascript">
		$(function(){
			 $("#${id}").datepicker({
				    showOtherMonths: true,
				    selectOtherMonths: false,
			 });
 
			 $("#${id}").bind("focus",function(){
					var _name = $("#${id}").attr("name").replace("token_", "");
					$("#${id}").attr("name", _name);
					$("#form_token").val(1);
			 });
		});
	</script>	