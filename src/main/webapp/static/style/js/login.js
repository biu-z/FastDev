// 判断时候在Iframe框架内,在则刷新父页面
if (self != top) {
    parent.location.reload(true);
    if (!!(window.attachEvent && !window.opera)) {
        document.execCommand("stop");
    } else {
        window.stop();
    }
}

$(function () {
	
    // 得到焦点
    $("#password").focus(function () {
        $("#left_hand").animate({
            left: "150",
            top: " -38"
        }, {
            step: function () {
                if (parseInt($("#left_hand").css("left")) > 140) {
                    $("#left_hand").attr("class", "left_hand");
                }
            }
        }, 2000);
        $("#right_hand").animate({
            right: "-64",
            top: "-38px"
        }, {
            step: function () {
                if (parseInt($("#right_hand").css("right")) > -70) {
                    $("#right_hand").attr("class", "right_hand");
                }
            }
        }, 2000);
    });
    // 失去焦点
    $("#password").blur(function () {
        $("#left_hand").attr("class", "initial_left_hand");
        $("#left_hand").attr("style", "left:100px;top:-12px;");
        $("#right_hand").attr("class", "initial_right_hand");
        $("#right_hand").attr("style", "right:-112px;top:-12px");
    });

	// 登录
	$("form").submit(function(){
		var $form = $(this);
		var $imgcode=$("#"+$form.attr("data-imgcode"));
		
		var ajax = new Ajax($form.attr("action"), function(data){
			if (data.code === 0) {
				layer.msg(data.message, {shift: 1});
				setTimeout(function(){window.location.href = BladeApp.ctxPath + "/";}, 1200);
				return false;
			} else {
				layer.msg(data.message, {shift: 6});
				$imgcode.click();
				return false;
			}
		});
		ajax.data = $form.serialize();
		ajax.start();
		
		return false;
	});
});

//回车登录
function enterlogin(){
    if (event.keyCode == 13){
        event.returnValue=false;
        event.cancel = true;
        $('#loginform').submit();
    }
}
