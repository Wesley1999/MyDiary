$(function () {

    $(".no-ime").focusin(function(){
        $(this).attr("type","url");
    });
    $(".no-ime").focusout(function(){
        $(this).attr("type","text");
    });
    $(".no-ime").on("input", function(){
        var that = $(this);
        var val = that.val();
        var expression = /[\u4E00-\u9FA5]/;
        var rep = new RegExp(expression);
        if(rep.test(val)){
            alert("请使用拼音输入！");
            that.val("");
        }
    });

    $.ajax({
        url: '/api/whether_login.action',
        type: "POST",
        success: function (returnData) {
            if (returnData.data == true) {
                pageIsIndex = true
                location.href = "/";
            }
        }
    });

    $("#log_in_button").click(function () {
        $("#log_in_button").css("display", "none");
        $("#loging_in_button").css("display", "block");
        let SECRET_KEY = $("#SECRET_KEY").val();
        let paramsMap = new Map();
        paramsMap.set("SECRET_KEY", SECRET_KEY);
        $.ajax({
            url: '/api/login.action',
            data: parseParameterByMap(paramsMap),
            type: "POST",
            success: function (returnData) {
                if (returnData.status == 0) {
                    location.href = "/";
                } else {
                    $("#log_in_button").css("display", "block");
                    $("#loging_in_button").css("display", "none");
                    $("#modal_to_show").html("");
                    $("#info_content").html(returnData.msg);
                    $("#info_modal").modal("show");
                }
            },
            error: function () {
                $("#log_in_button").css("display", "block");
                $("#loging_in_button").css("display", "none");
                $("#modal_to_show").html("");
                $("#info_content").html("请求失败");
                $("#info_modal").modal("show");
            }
        })
    });

    $("#change_password_button").click(function () {
        $("#SECRET_KEY_change_password_old").val($("#SECRET_KEY").val());
        $("#SECRET_KEY_change_password").val("");
        $("#repeat_SECRET_KEY_initialization").val("");
        $("#change_password_modal").modal("show");
    });

});

function change_password_cancel_click() {
    $("#SECRET_KEY_change_password_old").val("");
    $("#SECRET_KEY_change_password").val("");
    $("#repeat_SECRET_KEY_change_password").val("");
    $("#change_password_modal").modal("hide");
}

function change_password_confirm_click() {

    $("#change_password_confirm_button").css("display", "none");
    $("#change_password_confirming_button").css("display", "block");

    let SECRET_KEY_change_password = $("#SECRET_KEY_change_password").val();
    let repeat_SECRET_KEY_change_password = $("#repeat_SECRET_KEY_change_password").val();
    let SECRET_KEY_change_password_old = $("#SECRET_KEY_change_password_old").val();


    if (SECRET_KEY_change_password !== repeat_SECRET_KEY_change_password) {
        $("#change_password_modal").modal("hide");
        $("#modal_to_show").html("change_password_modal");
        $("#info_content").html("新日记密码两次输入不一致");
        $("#info_modal").modal("show");
        $("#change_password_confirm_button").css("display", "block");
        $("#change_password_confirming_button").css("display", "none");
        return;
    }

    let paramsMap = new Map();
    paramsMap.set("SECRET_KEY", SECRET_KEY_change_password_old);
    $.ajax({
        url: '/api/login.action',
        data: parseParameterByMap(paramsMap),
        type: "POST",
        success: function (returnData) {
            let paramsMap = new Map();
            paramsMap.set("newSECRET_KEY", $("#SECRET_KEY_change_password").val());
            if (returnData.status == 0) {
                $.ajax({
                    url: '/api/change_secret_key.action',
                    data: parseParameterByMap(paramsMap),
                    type: "POST",
                    success: function (returnData) {
                        if (returnData.status == 0) {
                            $.ajax({
                                url: '/api/logout.action',
                                data: parseParameterByMap(paramsMap),
                                type: "POST",
                            });
                            $("#change_password_modal").modal("hide");
                            $("#modal_to_show").html("");
                            $("#info_content").html("日记密码修改成功");
                            $("#info_modal").modal("show");
                        } else {
                            $("#change_password_modal").modal("hide");
                            $("#modal_to_show").html("change_password_modal");
                            $("#info_content").html(returnData.msg);
                            $("#info_modal").modal("show");
                        }
                        $("#change_password_confirm_button").css("display", "block");
                        $("#change_password_confirming_button").css("display", "none");
                    },
                    error: function () {
                        alert("请求失败")
                        $("#change_password_confirm_button").css("display", "block");
                        $("#change_password_confirming_button").css("display", "none");
                    }
                })
            } else {
                $("#change_password_modal").modal("hide");
                $("#modal_to_show").html("change_password_modal");
                $("#info_content").html(returnData.msg);
                $("#info_modal").modal("show");
            }
        },
        error: function () {
            $("#change_password_modal").modal("hide");
            $("#modal_to_show").html("");
            $("#info_content").html("请求失败");
            $("#info_modal").modal("show");
            $("#change_password_confirm_button").css("display", "block");
            $("#change_password_confirming_button").css("display", "none");
        }
    })
}

function info_confirm_click() {
    $("#info_modal").modal("hide");
    $("#info_content").html("");
    if ($("#modal_to_show").html() !== "") {
        $("#"+$("#modal_to_show").html()).modal("show")
    }
}


