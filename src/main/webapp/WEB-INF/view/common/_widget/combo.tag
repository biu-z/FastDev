				<div class="input-group">
                       <input type="text" class="form-control" id="combo_${id}" ${required!} placeholder="${placeholder!}">	
							@ var token = "token_";
							@ if (value != ""){
							@ 	token = "";	
							@ }
                       <input type="hidden" id="_${id}" name="${token}${name}" value="${value!0}">
                       <div class="input-group-btn">
                   		<button type="button" id="btn_${id}" class="btn btn-sx btn-white">
							<span>x</span> 
						</button>
                           <ul class="dropdown-menu dropdown-menu-right" role="menu">
                           </ul>
                       </div>
                       <!-- /btn-group -->
                   </div>
                <script src="${ctxPath}/static/assets/js/bootstrap-suggest.js" type="text/javascript" ></script>
				<script type="text/javascript">
					$(function(){
						 $("#btn_${id}").bind("click",function(){
							    $("#_${id}").val("");
							    $("#combo_${id}").val("");
						 });
						 $("#combo_${id}").bind("focus",function(){
								var _name = $("#_${id}").attr("name").replace("token_", "");
								$("#_${id}").attr("name", _name);
								$("#form_token").val(1);
						 });
						 $.post("${ctxPath}/cache/getCombo", {code:"${code!}", type:"${type!}", source:"${source!}"}, function (data) {
								if(data.code === 0){
								    $("#combo_${id}").bsSuggest({
								    	idField: "id",       
								    	keyField: "text", 
								    	effectiveFields: ["text"],
								        data: {
								            'value':data.data
								        }
								    }).on('onSetSelectValue', function (e, keyword) {
								        $("#_${id}").val(keyword.id);
								    });
								    var comboModle = ${model!"null"};
								    if(comboModle == null) {return;};
								    var _comboid = comboModle["${id}"];
								    for(var i=0; i<data.data.length; i++){
								    	if(data.data[i].id == _comboid){
										    $("#combo_${id}").val(data.data[i].text);
								    		return;
								    	}
								    }
								}
					     }, "json");
					});
				</script>