$(function () {
    $("#publishBtn").click(publish);
});

function publish() {
    //点击完发布则隐藏发布框
    $("#publishModal").modal("hide");

    var title = $("#recipient-name").val();
    var content = $("#message-text").val();

    //发送异步请求
    $.post(
        CONTEXT_PATH + "/discuss/add",
        {"title":title,"content":content},
        function (data){
            data = $.parseJSON(data);
            $("#hintBody").text(data.msg)
            $("#hintModal").modal("show");
            setTimeout(function () {
                $("#hintModal").modal("hide");
                window.location.reload();
            }, 2000);
        }
    );
}