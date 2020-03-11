$(function () {
    $.ajax({
        url: '/api/whether_login.action',
        type: "POST",
        success: function (returnData) {
            if (returnData.data == true) {
                location.href = "/";
            }
        }
    });

    $("#log_in_button").click(function () {
        $("#log_in_button").css("display", "none");
        $("#loging_in_button").css("display", "block");

        let WEBDAV_USERNAME = $("#WEBDAV_USERNAME").val();
        let WEBDAV_PASSWORD = $("#WEBDAV_PASSWORD").val();
        let SECRET_KEY = $("#SECRET_KEY").val();

        let paramsMap = new Map();
        paramsMap.set("WEBDAV_USERNAME", WEBDAV_USERNAME);
        paramsMap.set("WEBDAV_PASSWORD", WEBDAV_PASSWORD);
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

    $("#initialize_button").click(function () {
        $("#WEBDAV_USERNAME_initialization").val($("#WEBDAV_USERNAME").val());
        $("#WEBDAV_PASSWORD_initialization").val($("#WEBDAV_PASSWORD").val());
        $("#SECRET_KEY_initialization").val($("#SECRET_KEY").val());
        $("#repeat_SECRET_KEY_initialization").val("");
        $("#initialize_modal").modal("show");
    });

    $("#change_password_button").click(function () {
        $("#WEBDAV_USERNAME_change_password").val($("#WEBDAV_USERNAME").val());
        $("#WEBDAV_PASSWORD_change_password").val($("#WEBDAV_PASSWORD").val());
        $("#SECRET_KEY_change_password_old").val($("#SECRET_KEY").val());
        $("#SECRET_KEY_change_password").val("");
        $("#repeat_SECRET_KEY_initialization").val("");
        $("#change_password_modal").modal("show");
    });

});

function initialization_cancel_click() {
    $("#WEBDAV_USERNAME_initialization").val("");
    $("#WEBDAV_PASSWORD_initialization").val("");
    $("#SECRET_KEY_initialization").val("");
    $("#repeat_SECRET_KEY_initialization").val("");
    $("#initialize_modal").modal("hide");
}

function initialization_confirm_click() {
    $("#initialize_confirm_button").css("display", "none");
    $("#initialize_confirming_button").css("display", "block");

    let WEBDAV_USERNAME_initialization = $("#WEBDAV_USERNAME_initialization").val();
    let WEBDAV_PASSWORD_initialization = $("#WEBDAV_PASSWORD_initialization").val();
    let SECRET_KEY_initialization = $("#SECRET_KEY_initialization").val();
    let repeat_SECRET_KEY_initialization = $("#repeat_SECRET_KEY_initialization").val();

    if (SECRET_KEY_initialization !== repeat_SECRET_KEY_initialization) {
        $("#initialize_modal").modal("hide");
        $("#modal_to_show").html("initialize_modal");
        $("#info_content").html("日记密码两次输入不一致");
        $("#info_modal").modal("show");
        $("#initialize_confirm_button").css("display", "block");
        $("#initialize_confirming_button").css("display", "none");
        return;
    }
    if (SECRET_KEY_initialization == "" || WEBDAV_USERNAME_initialization == ""
        || WEBDAV_PASSWORD_initialization == "" || repeat_SECRET_KEY_initialization == "") {
        $("#initialize_modal").modal("hide");
        $("#modal_to_show").html("initialize_modal");
        $("#info_content").html("所有表单项必填");
        $("#info_modal").modal("show");
        $("#initialize_confirm_button").css("display", "block");
        $("#initialize_confirming_button").css("display", "none");
        return;
    }
    let paramsMap = new Map();
    paramsMap.set("WEBDAV_USERNAME", $("#WEBDAV_USERNAME_initialization").val());
    paramsMap.set("WEBDAV_PASSWORD", $("#WEBDAV_PASSWORD_initialization").val());
    paramsMap.set("SECRET_KEY", SECRET_KEY_initialization);
    $.ajax({
        url: '/api/initialization.action',
        data: parseParameterByMap(paramsMap),
        type: "POST",
        success: function (returnData) {
            if (returnData.status == 0) {
                $("#initialize_modal").modal("hide");
                $("#modal_to_show").html("");
                $("#info_content").html("初始化成功");
                $("#info_modal").modal("show");
            } else {
                $("#initialize_modal").modal("hide");
                $("#modal_to_show").html("initialize_modal");
                $("#info_content").html(returnData.msg);
                $("#info_modal").modal("show");
            }
            $("#initialize_confirm_button").css("display", "block");
            $("#initialize_confirming_button").css("display", "none");
        },
        error: function () {
            alert("请求失败");
            $("#initialize_confirm_button").css("display", "block");
            $("#initialize_confirming_button").css("display", "none");
        }
    })
}

function change_password_cancel_click() {
    $("#WEBDAV_USERNAME_change_password").val("");
    $("#WEBDAV_PASSWORD_change_password").val("");
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
    let WEBDAV_USERNAME_change_password = $("#WEBDAV_USERNAME_change_password").val();
    let WEBDAV_PASSWORD_change_password = $("#WEBDAV_PASSWORD_change_password").val();
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
    if (SECRET_KEY_change_password == "" || repeat_SECRET_KEY_change_password == "" || WEBDAV_USERNAME_change_password == ""
        || WEBDAV_PASSWORD_change_password == "" || SECRET_KEY_change_password_old == "") {
        $("#change_password_modal").modal("hide");
        $("#modal_to_show").html("change_password_modal");
        $("#info_content").html("所有表单项必填");
        $("#info_modal").modal("show");
        $("#change_password_confirm_button").css("display", "block");
        $("#change_password_confirming_button").css("display", "none");
        return;
    }

    let paramsMap = new Map();
    paramsMap.set("WEBDAV_USERNAME", WEBDAV_USERNAME_change_password);
    paramsMap.set("WEBDAV_PASSWORD", WEBDAV_PASSWORD_change_password);
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


