$(function () {

    // 保存用户名
    var username;

    // 保存相片路径
    var pic;

    var socket;

    var auth;

    // 字体定时器
    let fontTimer = null;

    var cont = 0;

    var font = $("#text").html();

    // 字体慢展示
    function run() {
        if (cont > font.length) {
            clearInterval(fontTimer);
        }
        $("#msg").html(font.substring(0, cont++));
    }
    fontTimer = setInterval(run, 114);


    // 选择照片
    $(".pic_body img").click(function () {
        $(this).css("box-shadow", "0 0 10px 0 red").siblings().css("box-shadow", "");
        pic = $(this).attr("src");
    })

    /**
     * 初始化滚动条
     */
    $(".content-body").mCustomScrollbar({
        autoHideScrollbar: true,
    });

    /**
     * 回车发送信息
     */
    $("#msg_info").keydown(function (event) {
        if (event.keyCode === 13) {
            let msg = $("#msg_info").val().trim();
            if (msg) {
                sendOneMsg(msg);
            }
            // 判断输入的信息是否为空！
            else {
                // alert("请先输入要发送的内容");
            }
            // 去除最后一个空格
            stopDefaultKey(event);
        }
    })


    /**
     * 去除默认空格
     * @param {*} e 
     */
    function stopDefaultKey(e) {
        if (e && e.preventDefault) {
            e.preventDefault();
        } else {
            window.event.returnValue = false;
        }
        return false;
    }

    // 监听信息的发送
    $("#send_msg").click(function () {






        let msg = $("#msg_info").val().trim();
        if (msg) {
            sendOneMsg(msg);
        }
        // 判断输入的信息是否为空！
        else {
            // alert("请先输入要发送的内容");
        }
    });



    // 登录
    $("#login").click(function () {
        // 获取用户名
        username = $("#username").val().trim();

        // 判断用户是否为空
        if (username == "") {
            $(".name-msg").html("请输入用户名");
            $(".name-msg").css("display", "inline-block");
            return;
        }

        // 判断图片是否为空
        if (pic == undefined) {
            $(".pic-msg").css("display", "inline-block");
            return;
        }

        initSocket(function () {
            $('#myModal').modal("hide");
            $("#index").fadeOut(200, function () {
                $(".head_username").text(username);
                $(".head_img").attr("src", pic);
                $("body,html").css("background-color", "#353943");
                $("body,html").css("color", "black");
                $("#body").css("display", "block");
                $("#content").addClass("content");
            });
        })

    });


    /**
     *  socket初始化
     * @param {回调函数} callback 
     */
    function initSocket(callback) {
        if (typeof (WebSocket) === "undefined") {
            alert("您的浏览器不支持socket")
        } else {
            // 实例化socket
            socket = new WebSocket("ws://localhost:8080/websocket");
            // 监听socket的消息
            socket.onmessage = function (ev) {
                let msgInfo = JSON.parse(ev.data);

                switch (msgInfo.stateEnum) {
                    // 认证失败
                    case "REGISTER_ERROR":
                        $(".name-msg").html("当前用户名已存在，请重新输入！");
                        $(".name-msg").css("display", "inline-block");
                        // 销毁滚动条
                        $(".content-body").mCustomScrollbar("destroy");
                        // 清空欢迎内容
                        $(".content-body").empty();
                        return;
                        // 更新在线人数
                    case "COUNT":
                        $(".on-line").text(msgInfo.count + " 人");
                        break;
                        // 用户登录提示信息
                    case "RETURN_USER_LOGIN_INFO":
                        let loginHtml = `
                            <p style="text-align: center; font-size: 12px;">欢迎<span style="color: red; margin: 0 5px; font-weight: bold;">${msgInfo.userInfo.username}</span>加入廖同学的聊天室
                             <span style="font-size: 10px;margin-left: 10px;color: #353943;">
                                ${Utils.simpleDateFormat(new Date)}
                             </span>
                            </p>
                        `;
                        // 追加当前用户在线的信息
                        appendHtml(loginHtml);
                        break;
                        // 用户退出提示信息
                    case "RETURN_USER_LOGOUT_INFO":
                        let logOutHtml = `
                        <p style="text-align: center; font-size: 12px;"><span style="color: #28a745; margin: 0 5px; font-weight: bold;">${msgInfo.userInfo.username}</span>已退出廖同学的聊天室
                         <span style="font-size: 10px;margin-left: 10px;color: #353943;">
                            ${msgInfo.date}
                         </span>
                        </p>
                    `;
                        // 追加当前用户在线的信息
                        appendHtml(logOutHtml);
                        break;
                        // 接受信息
                    case "MSG_ALL":
                        acceptMsgHtml(msgInfo);
                        break;
                    default:
                        console.log("其他");
                        break;
                }
                callback();
            }

            // socket连接
            socket.onopen = function () {
                // 新增用户资源
                let regUser = {
                    stateEnum: "REGISTER",
                    userInfo: {
                        "username": username,
                        "pic": pic
                    }
                }
                let html = `
                    <p style="text-align: center; font-size: 12px;"><b>欢迎您加入廖同学的聊天室</b>
                     <span style="font-size: 10px;margin-left: 10px;color: #353943;">
                        ${Utils.simpleDateFormat(new Date)}
                     </span>
                    </p>
                `;
                // 追加当前用户在线的信息
                appendHtml(html);
                // 向服务器注册信息
                regUser = JSON.stringify(regUser);
                socket.send(regUser);
            }

            // socket断开
            socket.onclose = function (ev) {
                if (auth) {
                    $('#warning_Model').modal({
                        show: true
                    });
                }
                // 关闭模态框刷新页面
                $('#warning_Model').on("hidden.bs.modal", function (e) {
                    location.reload();
                })
            }
        }
    }



    /**
     * 群发消息
     */
    function sendOneMsg(msg) {

        if (!socket) {
            return;
        }

        if (socket.readyState == socket.OPEN) {
            let msgInfo = {
                stateEnum: "MSG_ALL",
                msg: msg,
                date: new Date(),
                userInfo: {
                    "username": username,
                    "pic": pic
                }
            }
            sendMsgHtml(msgInfo);
            msgInfo = JSON.stringify(msgInfo);
            socket.send(msgInfo);
        } else {
            alert("连接未成功");
        }
    }


    /**
     * 封装发送的信息
     */
    function sendMsgHtml(msgInfo) {
        let html = `
        <div class="item-my">
            <div class="content_left">
                <div class="content_left_head">
                    <span><b>${username}</b> &nbsp;</span>
                    <span style="font-size: 10px;">${Utils.simpleDateFormat(msgInfo.date)}</span>
                </div>
                <div class="content_left_content">
                    <p>${msgInfo.msg}</p>
                </div>
            </div>
            <div class="content_right">
                <img src="${msgInfo.userInfo.pic}"
                    alt="">
            </div>
        </div>`;

        appendHtml(html);

        // 清空发送的内容
        $("#msg_info").val("");
    }



    /**
     * 封装信息
     */
    function acceptMsgHtml(msgInfo) {
        if (msgInfo.stateEnum != "MSG_ALL") {
            return;
        }
        let html = `
        <div class="item">
        <div class="content_left">
            <img src="${msgInfo.userInfo.pic}"
                alt="">
        </div>
        <div class="content_right">
            <span><b>${msgInfo.userInfo.username}</b> &nbsp;</span>
            <span style="font-size: 10px;">${msgInfo.date}</span>
            <div>
                <p>
                    ${msgInfo.msg}
                </p>
            </div>
        </div>
    </div>
        `;
        appendHtml(html);
    }


    /**
     * 添加元素
     * @param {元素} html 
     */
    function appendHtml(html) {
        // 添加节点
        $(".content-body").append(html);

        // 销毁滚动条
        $(".content-body").mCustomScrollbar("destroy");

        // 设置滚动条到最底部
        $(".content-body").mCustomScrollbar({
            setTop: "-9999999999px"
        });
    }

})