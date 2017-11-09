<%--
  Created by IntelliJ IDEA.
  User: Superwen
  Date: 2017/1/25
  Time: 下午3:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>结果展示</title>
    <script src="node_modules/jquery/dist/jquery.min.js"></script>
    <script src="node_modules/vue/dist/vue.min.js"></script>

</head>
<body>
<div id="app">
    <table class="table table-striped">
        <thead>
            <tr>
                <td>姓名</td>
                <td>手机号</td>
                <td>活力值</td>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>{{name}}</td>
                <td>{{phone}}</td>
                <td>{{activity}}</td>
            </tr>
        </tbody>
    </table>

    <button v-on:click="rolling()">摇一摇</button>
</div>

    <script>
        var path = window.location.host;

        var app = new Vue({
            el: '#app',
            data: {
                name: '',
                phone: '',
                activity: ''
            },
            methods: {
                rolling: function(){
                    var data = {
                        fromName: app.name,
                        from: app.id,
                        type: "rolling"
                    };
                    websocket.send(JSON.stringify(data));
                }
            }

        });
        $.ajax({
                    url:"user/self",
                    contentType : "application/json",
                    method: "get",
                    dataType: "json"
                })
                .done(function(data){
                    app.name = data.name;
                    app.phone = data.phone;
                    app.activity = data.activity;
                    app.id = data.id;
                    startSocketConnection(data);
                });

        var websocket;
        function startSocketConnection(data){
            if ('WebSocket' in window) {
                websocket = new WebSocket("ws://" + path + "/ws?id="+data.id + "&name=" + data.name);
            } else if ('MozWebSocket' in window) {
                websocket = new MozWebSocket("ws://" + path + "/ws?id="+data.id + "&name=" + data.name);
            } else {
                websocket = new SockJS("http://" + path + "/ws/sockjs?id="+data.id + "&name=" + data.name);
            }
            websocket.onopen = function(event) {
                console.log("WebSocket:已连接");
                console.log(event);
            };
            websocket.onmessage = function(event) {
                var data=JSON.parse(event.data);
                app.activity = data;
            };
            websocket.onerror = function(event) {
                console.log("WebSocket:发生错误 ");
                console.log(event);
            };
            websocket.onclose = function(event) {
                console.log("WebSocket:已关闭");
                console.log(event);
            };
        }



    </script>
</body>
</html>
