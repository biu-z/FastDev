		<div class="form-group" style="display:${display!'block'}">
			<div class="col-sm-10" id="${id}_BTN">
				<label class="control-label no-padding-right" id="${id}_upload" style="cursor:pointer;"><i class="fa fa-cloud-upload"></i>&nbsp;选择图片</label>&nbsp;&nbsp;
				<label class="control-label no-padding-right" id="${id}_delete" style="cursor:pointer;"><i class="fa fa-times"></i>&nbsp;删除图片</label>
				<input type="hidden" id="${id}" data-type="imgupload" name="token_${name}"  value=""  class="form-control"/>
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-8">
				<image id="${id}_IMG" src="${ctxPath}/static/blade/img/img.jpg" data-auto="${auto}" style="padding:2px; border:1px solid #ccc;cursor:pointer;width:${width!'170px'};height:${height!'110px'};"></image>
			</div>
		</div>
		<div class="form-group" style="display:none;">
			<textarea  id="${id}_editor" name="${id}_editor" ></textarea>
		</div>
		
		<script charset="utf-8" src="${ctxPath}/static/kindeditor/kindeditor.js"></script>
		<script type="text/javascript">
			$(function(){
				$("#${id}_IMG").bind("click",function() {
					var src = $(this).attr("src");
					if(src != "${ctxPath}/static/blade/img/img.jpg"){
						window.open(src);
					}
				});
				
				$("#${id}_delete").bind("click",function() {
					var _name = $("#${id}").attr("name").replace("token_", "");
					$("#${id}").attr("name", _name);
					$("#${id}").val("0");
					$("#${id}_IMG").attr("src","${ctxPath}/static/blade/img/img.jpg");
					$("#form_token").val(1);
				});
				

				KindEditor.ready(function(K) {
					var editor = K.create('textarea[name="${id}_editor"]', {
						uploadJson : '${ctxPath}/kindeditor/upload_json',
						fileManagerJson : '${ctxPath}/kindeditor/file_manager_json',
						allowFileManager : false
					});
					//----插入图片-----
		            K("#${id}_upload").click(function () {
		                editor.loadPlugin('image', function () {
		                    editor.plugin.imageDialog({
		                        clickFn: function (url, title) {
		                        	var value = title;
		                        	if ("${returnType!'id'}" == "url") {
		                        		value = url;
		                        	}
		        					var _name = $("#${id}").attr("name").replace("token_", "");
		        					$("#${id}").attr("name", _name);
		                        	$("#${id}").val(value);//返回附件表中id
		                            $("#${id}_IMG").attr("src",url);
		                            $("#form_token").val(1);
		                            editor.hideDialog();
		                        }
		                    });
		                });
		            });
		            //-----插入图片-----
				});
			});
			
			
			
			
			
			function initImgUpload(id){
				$.post("${ctxPath}/kindeditor/initimg", {id : id}, function(data){
					if(data.code === 0){
						$("#${id}_IMG").attr("src", data.data.URL);
					}
					else{
						/* layer.alert("加载图片失败", {
							icon : 7
						}); */
					}
					
				}, "json");
			}
			
			
			
		</script>