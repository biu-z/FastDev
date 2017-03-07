	<label class="pull-left inline" style="margin-top:5px;">
		<input id="${id}_chb" type="checkbox" ${checked!} ${disabled!} class="ace ace-switch ace-switch-5" />
		<span class="lbl middle"></span>
	</label>	
	
	<input type="hidden" id="${id}" name="${name}" value="0">
	
	<script type="text/javascript">
		$(function(){
			$("#${id}_chb").bind("click",function(){
				$("#form_token").val(1);
				
				if($("#${id}_chb").is(":checked")){
					$("#${id}").val("1");
				}
				else{
					$("#${id}").val("0");
				}
			});
		});
	</script>