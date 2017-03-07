	<script src="${ctxPath}/static/laydate/laydate.js"></script>	
	<script type="text/javascript"> 
		$(function(){
			var _elem = "${id}";
			laydate({
			    elem: '#'+_elem,
			    format: '${format!'YYYY-MM-DD hh:mm:ss'}', // 分隔符可以任意定义，该例子表示只显示年月日
			    //festival: true, //显示节日
			    choose: function(datas){ //选择日期完毕的回调
			      /*   alert('得到：'+datas); */
			    }
			});
			
			 $("#${id}").bind("focus",function(){
					var _name = $("#${id}").attr("name").replace("token_", "");
					$("#${id}").attr("name", _name);
					$("#form_token").val(1);
			 });
		});	
	</script>	
	@ var _token = token!'';
	@ if (!isEmpty(value)){
	@ 	_token = "";	
	@ }
	<input type="text" id="${id}" name="${_token}${name}" class="form-control" ${required!} ${disabled!}  value="${value!}" placeholder="${placeholder!}"  />