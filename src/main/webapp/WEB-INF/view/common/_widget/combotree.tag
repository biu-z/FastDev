<script type="text/javascript">

		function beforeClick${id}_ipt(treeId, treeNode) {
			var zTree = $.fn.zTree.getZTreeObj("tree${id}_ipt");
			zTree.checkNode(treeNode, !treeNode.checked, null, true);
			return false;
		}
		
		function onCheck${id}_ipt(e, treeId, treeNode) {
			var zTree = $.fn.zTree.getZTreeObj("tree${id}_ipt"),
			nodes = zTree.getCheckedNodes(true),
			ids = "",
			v = "";
			for (var i = 0, l = nodes.length; i < l; i++) {
				@ var key = (func.like(type,"combotree_")) ? "num" : "id";
				@ key = (isEmpty(treeId) ? key : treeId);
				ids += nodes[i].${key} + ",";
				v += nodes[i].name + ",";
			}
			if (v.length > 0 ) v = v.substring(0, v.length - 1);
			if (ids.length > 0 ) ids = ids.substring(0, ids.length - 1);
			var treeInput = $("#${id}_ipt");
			var treeHidden = $("#${id}");
			treeInput.val(v);
			treeHidden.val(ids);
			
			var _name = $("#${id}").attr("name").replace("token_", "");
			$("#${id}").attr("name", _name);
			$("#form_token").val(1);
		}

		function showMenu${id}_ipt() {
			var $ipt = $("#${id}_ipt");
			@ if (isEmpty(width)) {
				$("#tree${id}_ipt").css({"width": $ipt.css("width")});
			@ }
			var top = $ipt.outerHeight() 
			@ if (fix!'false' == 'true') {
				top = top + $ipt.offset().top;
			@ }
			$("#treeContent${id}_ipt").css({top:top + "px"}).slideDown("fast");
			$("body").bind("mousedown", onBodyDown${id}_ipt);
		}
		
		function searchMenu${id}_ipt() {
			initComboTree${id}_ipt(0);
		}
		
		function hideMenu${id}_ipt() {
			$("#treeContent${id}_ipt").fadeOut("fast");
			$("body").unbind("mousedown", onBodyDown${id}_ipt);
		}
		
		function onBodyDown${id}_ipt(event) {
			if (!(event.target.id == "menuBtn" || event.target.id == "${id}_ipt" || event.target.id == "treeContent${id}_ipt" || $(event.target).parents("#treeContent${id}_ipt").length>0)) {
				hideMenu${id}_ipt();
			}
		}

		$(function(){
			@ if (autoInit!'true' == 'true') {
				var val = "${value!}";
				if(val != "") {
					initComboTreeName${id}_ipt(val);
				}
				initComboTree${id}_ipt(val);
			@ }
			
			var $ipt = $("#${id}_ipt");
			$ipt.bind("click", function(){
				showMenu${id}_ipt();
			});
			$ipt.bind("keydown", function(e){
				if (e.keyCode == 13) { 
					searchMenu${id}_ipt();
				}
			});
			
		});
		
		function initComboTree${id}_ipt(val) {
			var setting = {
					check: {
						enable: true,
						@ if(check! == "radio"){
							chkStyle: "radio",
							radioType : "all"
						@ } else{
							chkboxType :{ "Y" : "", "N" : "" }
						@ }
					},
					view: {
						dblClickExpand: false
					},
					data: {
						simpleData: {
							enable: true
						}
					},
					callback: {
						beforeClick: beforeClick${id}_ipt,
						onCheck: onCheck${id}_ipt
					}
				};
			var treeList = new Ajax("${ctxPath}/combotree/getTreeList", function(data){
				if (data.code === 0) {
					$.fn.zTree.init($("#tree${id}_ipt"), setting, data.data);
				} else {
					layer.alert(data.message, {icon: 2,title:"发生错误"});
				}
			});
			treeList.set("search", $("#${id}_ipt").val());
			treeList.set("type", "${type!}");
			treeList.set("source", "${source!}");
			treeList.set("where", "${where!}");
			treeList.set("intercept", "${intercept!}");
			treeList.set("ext", "${ext!}");
			treeList.set("val", val);
			treeList.set("treeId", "${treeId!}");
			treeList.start();
		}
		
		function initComboTreeName${id}_ipt(val) {
			var treeName = new Ajax("${ctxPath}/combotree/getTreeListName", function(data){
				if (data.code === 0) {
					$("#${id}_ipt").val(data.data);
				} else {
					layer.alert(data.message, {icon: 2,title:"发生错误"});
				}
			});
			treeName.set("type", "${type!}");
			treeName.set("source", "${source!}");
			treeName.set("where", "${where!}");
			treeName.set("val", val);
			treeName.set("treeId", "${treeId!}");
			treeName.start();
		}
</script>
@ var _token = token!'';
@ if (!isEmpty(value)){
@ 	_token = "";	
@ }
<input id="${id}_ipt" type="text" class="form-control" ${readonly!} ${required!} ${disabled!} placeholder="${placeholder!'输入后请按回车搜索'}" />
<input id="${id}" name="${_token}${name!}" class="form-control" data-type="combotree"  type="hidden" value="${value!}"/>
<div id="treeContent${id}_ipt" class="menuContent" style="display:none; position: absolute;z-index:233;">
	<ul id="tree${id}_ipt" class="ztree" style="border-radius: 0 !important;-webkit-box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);background: #fafafa;border: 1px solid rgba(0, 0, 0, 0.15);border: 1px solid #cccccc;overflow-y: scroll;overflow-x: auto;margin-top:0; width:${width!'180px'}; max-height: ${height!'250px'};"></ul>
</div>

<!-- 引入所需js -->
<link rel="stylesheet" href="${ctxPath}/static/zTree/css/zTreeStyle/zTreeStyle.css" />
<script src="${ctxPath}/static/zTree/js/jquery.ztree.core.js" type="text/javascript" ></script>
<script src="${ctxPath}/static/zTree/js/jquery.ztree.excheck.js" type="text/javascript"></script>