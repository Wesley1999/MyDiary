window.onbeforeunload=function(){
    if (editing) {
        return 0;
    }
};

// 页面加载 筛选 按钮功能实现
$(function () {
    // 全局变量
    tryingGettingNextPage = true;
    nextDiaryIndex = 0;
    paramsMap = new Map();
    totalDiaries = 2147483647;
    paramsMap.set("startIndex", nextDiaryIndex);
    editing = false;

    getDiaries(paramsMap);

    $(document).scroll(function () {
        if ($(document).height() - $(document).scrollTop() - window.innerHeight < 1000) {
            if (!tryingGettingNextPage) {
                tryingGettingNextPage = true;
                paramsMap.set("startIndex", paramsMap.get("startIndex") + 10)
                getDiaries(paramsMap);
            }
        }
    });

    $("#btn-logout").click(function () {
        $.ajax({
            url: '/api/logout.action',
            data: parseParameterByMap(paramsMap),
            type: "POST",
            success: function (returnData) {
                if (returnData.status == 0) {
                    $("#body").load("login.html")
                }
            }
        })
    })
});

// 按钮
// $().prop('scrollHeight') 文章内容的实际高度
// $().prop('clientHeight') 文章内容的显示高度
// $().prop('scrollWidth')  文章内容的实际宽度
// $().prop('clientWidth')  文章内容的显示宽度

function more_button_click(target) {
    let jq_diary_item = $(target);
    let scrollHeight = jq_diary_item.prop('scrollHeight');
    jq_diary_item.css({"overflow":"visible","white-space":"normal"});
    jq_diary_item.animate({height:scrollHeight}, scrollHeight - 200 > 200 ? scrollHeight / 2 - 100 : 200);
}

function edit_button_click(target) {
    editing = true;
    let jq_diary_item = $(target).parent().parent().parent();
    let uuid = decompressData(jq_diary_item.children().eq(0).html());
    let timeStamp = decompressData(jq_diary_item.children().eq(1).html());
    // 处理mark标签
    originalContent = decompressData(jq_diary_item.children().eq(2).html()).replaceAll("<mark>", "").replaceAll("</mark>", "");
    originalDiaryTimeString = new Date(parseInt(timeStamp)).format("yyyy-MM-dd hh:mm:ss");

    $("#edit_input_text_preview").empty();
    $("#edit_input_text_preview").css("display", "none");
    $("#edit_preview_back_button").css("display", "none");
    $("#edit_preview_button").css("display", "block");
    $("#edit_shortcut_button").css("display", "block");
    $("#edit_input_text").css("display", "block");
    $("#edit_diary_time").val(originalDiaryTimeString);
    $("#edit_input_text").val(originalContent);
    $("#edit_words").html(wordsCountFunction($("#edit_input_text").val())+"字");
    $("#edit_uuid_info").html(uuid);
    $("#edit_diary_modal").modal("show");
}

function edit_save_button_click() {
    let paramsMap = new Map();
    paramsMap.set("uuid", $("#edit_uuid_info").html());
    paramsMap.set("diaryTime", new Date($("#edit_diary_time").val()).valueOf());
    paramsMap.set("content", $("#edit_input_text").val());
    $("#edit_save_button").css("display", "none");
    $("#edit_saving_button").css("display", "block");
    $.ajax({
        url: '/api/update_diary.action',
        data: parseParameterByMap(paramsMap),
        type: "POST",
        success: function (returnData) {
            if (returnData.status == 0) {
                $('#add_diary_modal').modal('hide');
                // alert("成功");
                // 页面会刷新，不必在刷新之前修改按钮
                // $("#edit_save_button").css("display", "block");
                // $("#edit_saving_button").css("display", "none");
                editing = false;
                location.reload();
            }
        },
        error: function () {
            $("#edit_save_button").css("display", "block");
            $("#edit_saving_button").css("display", "none");
        }
    })
}

function edit_preview_button_click() {
    $("#edit_input_text").css("display", "none");
    $("#edit_preview_button").css("display", "none");
    $("#edit_shortcut_button").css("display", "none");
    // 解析前，一个换行替换成两个换行
    let htmlContent = marked($("#edit_input_text").val().replaceAll("\n", "\n\n"));
    // 忽略脚本和样式代码
    htmlContent = htmlContent.replace(/<script>.*<\/script>/g,'');
    htmlContent = htmlContent.replace(/<style>.*<\/style>/g,'');
    htmlContent = htmlContent.replace(/<body>.*<\/body>/g,'');
    htmlContent = htmlContent.replace(/<html>.*<\/html>/g,'');
    $("#edit_input_text_preview").append(htmlContent);
    $("#edit_input_text_preview").css("display", "block");
    $("#edit_preview_back_button").css("display", "block");
    $("#edit_input_text_preview").css("height", parseInt($("#edit_input_text").css("height"))-7+"px");
}

function edit_preview_back_button_click() {
    $("#edit_input_text_preview").empty();
    $("#edit_input_text_preview").css("display", "none");
    $("#edit_preview_back_button").css("display", "none");
    $("#edit_input_text").css("display", "block");
    $("#edit_preview_button").css("display", "block");
    $("#edit_shortcut_button").css("display", "block")
}

function edit_cancel_button_click() {
    $('#edit_diary_modal').modal('hide');
    if ($("#edit_input_text").val() !== originalContent || $("#edit_diary_time").val() !== originalDiaryTimeString) {
        $("#edit_cancel_alert").modal("show")
    } else {
        editing = false;
    }
}

function edit_cancel_alert_cancel_click() {
    $("#edit_cancel_alert").modal("hide");
    $('#edit_diary_modal').modal('show');
}

function edit_cancel_alert_confirm_click() {
    $("#edit_diary_time").val("");
    $("#edit_input_text").val("");
    $("#edit_cancel_alert").modal("hide");
    editing = false;
}


function add_button_click() {
    editing = true;
    $("#add_input_text_preview").empty();
    $("#add_input_text_preview").css("display", "none");
    $("#add_preview_back_button").css("display", "none");
    $("#add_input_text").css("display", "block");
    $("#add_preview_button").css("display", "block");
    $("#add_shortcut_button").css("display", "block");
    $("#add_diary_time").val(new Date().format("yyyy-MM-dd hh:mm:ss"));
    $('#add_diary_modal').modal('show');
}

function add_save_button_click() {
    let paramsMap = new Map();

    paramsMap.set("diaryTime", new Date($("#add_diary_time").val()).valueOf());
    paramsMap.set("content", $("#add_input_text").val());
    $("#add_save_button").css("display", "none");
    $("#add_saving_button").css("display", "block");
    $.ajax({
        url: '/api/add_diary.action',
        data: parseParameterByMap(paramsMap),
        type: "POST",
        success: function (returnData) {
            if (returnData.status == 0) {
                $('#add_diary_modal').modal('hide');
                // alert("成功");
                // 页面会刷新，不必在刷新之前修改按钮
                // $("#add_save_button").css("display", "block");
                // $("#add_saving_button").css("display", "none");
                editing = false;
                location.reload();
            }
        },
        error: function () {
            $("#add_save_button").css("display", "block");
            $("#add_saving_button").css("display", "none");
        }
    })
}

function add_preview_button_click() {
    $("#add_input_text").css("display", "none");
    $("#add_preview_button").css("display", "none");
    $("#add_shortcut_button").css("display", "none");
    // 解析前，一个换行替换成两个换行
    let htmlContent = marked($("#add_input_text").val().replaceAll("\n", "\n\n"));
    // 忽略脚本和样式代码
    htmlContent = htmlContent.replace(/<script>.*<\/script>/g,'');
    htmlContent = htmlContent.replace(/<style>.*<\/style>/g,'');
    htmlContent = htmlContent.replace(/<body>.*<\/body>/g,'');
    htmlContent = htmlContent.replace(/<html>.*<\/html>/g,'');
    $("#add_input_text_preview").append(htmlContent);
    $("#add_input_text_preview").css("display", "block");
    $("#add_preview_back_button").css("display", "block");
    $("#add_input_text_preview").css("height", parseInt($("#add_input_text").css("height"))-7+"px");
}

function add_preview_back_button_click() {
    $("#add_input_text_preview").empty();
    $("#add_input_text_preview").css("display", "none");
    $("#add_preview_back_button").css("display", "none");
    $("#add_input_text").css("display", "block");
    $("#add_preview_button").css("display", "block");
    $("#add_shortcut_button").css("display", "block")
}

function add_cancel_button_click() {
    $('#add_diary_modal').modal('hide');
    if ($("#add_input_text").val() !== "") {
        $("#add_cancel_alert").modal("show")
    } else {
        editing = false;
    }
}

function add_cancel_alert_cancel_click() {
    $("#add_cancel_alert").modal("hide");
    $('#add_diary_modal').modal('show');
}

function add_cancel_alert_confirm_click() {
    $("#add_diary_time").val("");
    $("#add_input_text").val("");
    $("#add_cancel_alert").modal("hide");
    editing = false;
}


function delete_button_click(target) {
    let jq_diary_item = $(target).parent().parent().parent();
    let uuid = decompressData(jq_diary_item.children().eq(0).html());
    let timeStamp = decompressData(jq_diary_item.children().eq(1).html());
    let year = new Date(parseInt(timeStamp)).getFullYear();
    $("#delete_diary_uuid_info").html(uuid);
    $("#delete_diary_year_info").html(year);
    $("#delete_alert").modal("show");
}

function delete_alert_cancel_click() {
    $("#delete_alert").modal("hide");
}

function delete_alert_confirm_click() {
    let paramsMap = new Map();
    let uuid = $("#delete_diary_uuid_info").html();
    let year = $("#delete_diary_year_info").html();
    paramsMap.set("uuid", uuid);
    paramsMap.set("year", year);
    let jq_diary_item = $("#diary_item_"+uuid);
    $("#delete_alert_confirm_button").css("display", "none");
    $("#delete_alert_confirming_button").css("display", "block");
    $.ajax({
        url: '/api/delete_diary.action',
        data: parseParameterByMap(paramsMap),
        type: "POST",
        success: function (returnData) {
            if (returnData.status == 0) {
                nextDiaryIndex--;
                $("#diary_number_info").text("("+ --totalDiaries +"篇日记)");
                // alert("删除成功");
                $("#delete_alert").modal("hide");
                setTimeout(function(){
                    $("#delete_alert_confirm_button").css("display", "block");
                    $("#delete_alert_confirming_button").css("display", "none");
                    jq_diary_item.animate({height:0, padding:0}, 150);
                    setTimeout(function(){
                        jq_diary_item.remove();
                    }, 150);
                }, 200);
            }
        },
        error: function () {
            $("#delete_alert_confirm_button").css("display", "block");
            $("#delete_alert_confirming_button").css("display", "none");
        }
    })
}


function filter_confirm_button_click() {
    $("#filter_confirm_button").css("display", "none");
    $("#filter_confirming_button").css("display", "block");
    let startDate = new Date($("#filter_start_date").val());
    let endDate = new Date($("#filter_end_date").val());

    if (isNaN(startDate.valueOf())) {
        paramsMap.delete("startDate");
    } else {
        paramsMap.set("startDate", startDate.valueOf())
    }
    if (isNaN(endDate.valueOf())) {
        paramsMap.delete("endDate");
    } else {
        paramsMap.set("endDate", endDate.valueOf())
    }
    paramsMap.set("keyWord", $("#keyWord").val());
    paramsMap.set("startIndex", 0);
    $("#diaryItems").empty();
    getDiaries(paramsMap, true)

}

function parseParameterByMap(paramsMap) {
    let data = "";
    if (typeof(paramsMap) == "undefined") {
        return data;
    }
    for (var [key, value] of paramsMap) {
        data = parseParameter(data, key, value);
    }
    return data;
}

function getDiaries(paramsMap, forcedGet=false) {
    $("#loading_info").css("display", "block");
    $("#load_complete_info").css("display", "none");
    if (forcedGet !== true && (paramsMap.get("startIndex") >= totalDiaries || totalDiaries === 0)) {
        $("#loading_info").css("display", "none");
        $("#load_complete_info").css("display", "block");
        return;
    }
    // alert(parseParameterByMap(paramsMap))
    $.ajax({
        url: '/api/get_diaries.action',
        type: "POST",
        data: parseParameterByMap(paramsMap),
        success: function (returnData) {
            if (returnData.status == 0) {
                totalDiaries = returnData.data.totalItems;
                let diaryList = returnData.data.diaryList;
                for (let i = 0; i < diaryList.length; i++) {
                    // 获取返回值中的信息
                    let originalContent = diaryList[i].content;
                    let uuid = diaryList[i].uuid;
                    let diaryTime = diaryList[i].diaryTime;
                    let diaryTimeDate = new Date(parseInt(diaryTime));
                    let diaryTimeString = dateToString(diaryTimeDate);
                    // 计算字数
                    let wordsCount = wordsCountFunction(originalContent);
                    // 使搜索关键字高亮，且忽略markdown保留字
                    let keyWord = paramsMap.get("keyWord");
                    if (typeof (keyWord) !== "undefined" && keyWord !== "") {
                        originalContent = originalContent.replaceAll(keyWord, "<mark>"+keyWord+"</mark>");
                    }
                    // 解析前，一个换行替换成两个换行
                    let htmlContent = marked(originalContent.replaceAll("\n", "\n\n"));
                    // 忽略脚本和样式代码
                    htmlContent = htmlContent.replace(/<script>.*<\/script>/g,'');
                    htmlContent = htmlContent.replace(/<style>.*<\/style>/g,'');
                    htmlContent = htmlContent.replace(/<body>.*<\/body>/g,'');
                    htmlContent = htmlContent.replace(/<html>.*<\/html>/g,'');
                    // 注意隐藏标签的顺序
                    $("#diaryItems").append('' +
                        '    <div class="diary-item-content card shadow-sm rounded" id="diary_item_'+uuid+'" style="padding: 25px; margin-bottom: 20px">\n' +
                        '        <div style="display: none">' + compressData(uuid) + '</div>' +
                        '        <div style="display: none">' + compressData(diaryTime) + '</div>' +
                        '        <div style="display: none">' + compressData(originalContent) + '</div>' +
                        '        <div class="diary-item-content" style="height: 150px;" onclick="more_button_click(this)">\n' +
                        '        <p style="padding: 0; color: grey">' + diaryTimeString + '<span style="float: right">'+wordsCount+'字</span>' + '</p>\n' +
                        htmlContent +
                        '        </div>\n' +
                        '        <div style="text-align: right;">\n' +
                        '            <br>\n' +
                        '            <span>\n' +
                        '                <i class="far fa-edit" onclick="edit_button_click(this)" style="font-size: 20px; padding-right: 30px"></i>\n' +
                        '                <i class="far fa-trash-alt" onclick="delete_button_click(this)" style="font-size: 20px; padding-right: 30px"></i>\n' +
                        '            </span>' +
                        '            </span>\n' +
                        '        </div>\n' +
                        '    </div>\n' +
                        '')
                }

                // 获取页面完成
                tryingGettingNextPage = false;
                $("#diary_number_info").text("("+ totalDiaries +"篇日记)")
                if (returnData.data.endIndex <= totalDiaries) {
                    $("#loading_info").css("display", "none")
                    $("#load_complete_info").css("display", "block")
                    $("#filter_confirm_button").css("display", "block");
                    $("#filter_confirming_button").css("display", "none");
                }
            } else if (returnData.status == 110) {
                // alert(returnData.msg);
                $("#body").load("login.html");
                $("#filter_confirm_button").css("display", "block");
                $("#filter_confirming_button").css("display", "none");
            }
        },
        error: function () {
            // 有可能发生超时
            tryingGettingNextPage = false;
            $("#filter_confirm_button").css("display", "block");
            $("#filter_confirming_button").css("display", "none");
        }
    })
}
