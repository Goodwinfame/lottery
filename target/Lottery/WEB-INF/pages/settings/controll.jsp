<%--
  Created by IntelliJ IDEA.
  User: Superwen
  Date: 2017/1/20
  Time: 下午3:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Abloomy年会抽奖控制</title>
    <script src="node_modules/jquery/dist/jquery.min.js"></script>
    <script src="node_modules/vue/dist/vue.js"></script>
    <style>
        ul,li{
            padding: 0;
            margin: 0;
            list-style: none;
        }
        .table{
            width: 100%;
        }
        .table ul{
            display: table;
            width: 100%;

        }
        .table ul li{
            display: table-cell;
            width: 15%;
            border: 1px solid #ccc;
        }
    </style>
</head>
<body>

    <div id="app" class="container">
        <div>
            <h2>流程控制</h2><span>连接状态:{{status}}</span>
            <div>
                <button v-on:click="doRoute('index')">首页</button>
                <%--<button v-on:click="doRoute('user')">用户</button>--%>
                <button v-on:click="doRoute('prize')">开始抽奖</button>
                <%--<button v-on:click="doRoute('result')">获奖结果</button>--%>
                <%--<button v-on:click="doRoute('thanks')">结束</button>--%>
            </div>
        </div>
        <div>
            <h2>抽奖控制</h2>
            <div>
                <h4>三等奖</h4>
                <div>
                    <div class="table">
                        <ul>
                            <li>ID</li>
                            <li>名称</li>
                            <li>数量</li>
                            <li>已抽</li>
                            <li>等级</li>
                            <li>操作</li>
                        </ul>
                        <comp-prize v-for="prize in filtedPrizes(3)" :prize="prize"></comp-prize>
                    </div>

                </div>

            </div>
            <div>
                <h4>二等奖</h4>
                <div>
                    <div class="table">
                        <ul>
                            <li>ID</li>
                            <li>名称</li>
                            <li>数量</li>
                            <li>已抽</li>
                            <li>等级</li>
                            <li>操作</li>
                        </ul>
                        <comp-prize v-for="prize in filtedPrizes(2)" :prize="prize"></comp-prize>
                    </div>

                </div>

            </div>
            <div>
                <h4>一等奖</h4>
                <div>
                    <div class="table">
                        <ul>
                            <li>ID</li>
                            <li>名称</li>
                            <li>数量</li>
                            <li>已抽</li>
                            <li>等级</li>
                            <li>操作</li>
                        </ul>
                        <comp-prize v-for="prize in filtedPrizes(1)" :prize="prize"></comp-prize>
                    </div>

                </div>

            </div>
            <div>
                <h4>特等奖</h4>
                <div>
                    <div class="table">
                        <ul>
                            <li>ID</li>
                            <li>名称</li>
                            <li>数量</li>
                            <li>已抽</li>
                            <li>等级</li>
                            <li>操作</li>
                        </ul>
                        <comp-prize v-for="prize in filtedPrizes(0)" :prize="prize"></comp-prize>
                    </div>

                </div>

            </div>
            <div>
                <h2>人员设置</h2>
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <td>ID</td>
                        <td>姓名</td>
                        <td>手机号</td>
                        <td>奖品</td>
                        <td>状态</td>
                        <td>活力值</td>
                    </tr>
                    </thead>
                    <tbody class="setting-table">
                    <tr v-for="member in members">
                        <td>{{member.id}}</td>
                        <td>{{member.name}}</td>
                        <td>{{member.phone}}</td>
                        <td>{{member.prize}}</td>
                        <td>{{member.online}}</td>
                        <td>{{member.activity}}</td>
                    </tr>
                    </tbody>
                </table>

            </div>

        </div>

    </div>
    <script type="text/x-template" id="prize-template">
        <ul>
            <li>{{prize.id}}</li>
            <li>{{prize.name}}</li>
            <li>{{prize.count}}</li>
            <li>{{prize.drawed}}</li>
            <li>{{prize.level}}</li>
            <li>
                <button v-on:click="doDraw(prize.id)" class="btn btn-sm btn-success btn-add">抽奖</button>
                <button v-on:click="reset(prize)" class="btn btn-sm btn-success btn-add">重置</button>
            </li>
        </ul>

    </script>

<script>
        var path = window.location.host;
        Vue.component('comp-prize', {
            template: '#prize-template',
            props: ['prize'],
            methods: {
                doDraw(data){
                    var data = {
                        fromName: "controll",
                        from: "8888",
                        text: data,
                        type: "draw"
                    }
                    websocket.send(JSON.stringify(data));
                },
                reset: function(prize){
                    $.ajax({
                                url:"settings/resetPrize",
                                dataType:"json",
                                contentType : "application/json",
                                method: "post",
                                data: JSON.stringify({id: prize.id})
                            })
                            .done(function(data){
                                prize.drawed = 0;
                                app.members.forEach(function(member) {

                                    if(member.prize != null && member.prize.id == prize.id) {
                                        member.prize = null;
                                    }
                                });

                            });
                },
            }
        });

        var app = new Vue({
            el: '#app',
            data: {
                members: [],
                prizes: [],
                status: false
            },
            created: function() {
                this.doGet('prize');
                this.doGet('member');
            },
            methods: {
                filtedPrizes: function (level) {
                    return this.prizes.filter(function (prize) {
                        return prize.level == level;
                    })
                },
                doGet: function(type){
                    $.ajax({url:"settings/" + type + (type == "member"?"/scale/0":""), contentType : "application/json", method: "get"})
                            .done(function(data){
                                app[type + "s"] = data;
                            });
                },


                doRoute: function(page){
                    var data = {
                        msg: "routerGo",
                        msgNo: "10000",
                        data: page
                    };
                    $.ajax({
                        url:"settings/routerGo",
                        dataType:"json",
                        contentType : "application/json",
                        method: "post",
                        data: JSON.stringify(data)
                    })
                    .done(function(data){


                    });
                }
            }

        });

        var websocket;
        (function startSocketConnection(data){
            if ('WebSocket' in window) {
                websocket = new WebSocket("ws://" + path + "/ws?id=8888");
            } else if ('MozWebSocket' in window) {
                websocket = new MozWebSocket("ws://" + path + "/ws?id=8888");
            } else {
                websocket = new SockJS("http://" + path + "/ws/sockjs?id=8888");
            }
            websocket.onopen = function(event) {
                console.log("WebSocket:已连接");
                console.log(event);
                app.status = true;

            };
            websocket.onmessage = function(event) {
                var data=JSON.parse(event.data);
                if (!data) return;

                if (data.msg != null && data.msg == "doDraw"){
                    for (var i = 0; i<app.members.length; i++){
                        if (app.members[i].id == data.data.id){
                            app.members[i].prize = data.data.prize;
                        }
                    }

                    for (var j = 0; j<app.prizes.length; j++){
                        if (app.prizes[j].id == data.data.prize.id){
                            app.prizes[j].drawed ++;
                        }
                    }
                }


            };
            websocket.onerror = function(event) {
                console.log("WebSocket:发生错误 ");
                console.log(event);
                app.status = false;
            };
            websocket.onclose = function(event) {
                console.log("WebSocket:已关闭");
                console.log(event);
                app.status = false;

            };
        }())
    </script>

</body>
</html>
