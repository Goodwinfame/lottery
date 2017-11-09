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
    <title>Abloomy年会抽奖设置</title>
    <script src="node_modules/jquery/dist/jquery.min.js"></script>
    <script src="node_modules/vue/dist/vue.min.js"></script>

</head>
<body>
    <div id="app" class="container">
        <h1>抽奖设置</h1>
        <div>
            <h2>奖品设置</h2>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <td>ID</td>
                        <td>名称</td>
                        <td>数量</td>
                        <td>等级</td>
                        <td>操作</td>
                    </tr>
                </thead>
                <tbody class="setting-table">
                    <tr v-for="prize in prizes">
                        <td>{{prize.id}}</td>
                        <td><input type="text" v-model="prize.name"></td>
                        <td><input type="text" v-model="prize.count"></td>
                        <td><input type="text" v-model="prize.level"></td>
                        <td>
                            <button v-on:click="doSave('prize', prize)" class="btn btn-sm btn-success btn-add">保存</button>
                            <button v-on:click="doDelete('prize', prize)" class="btn btn-sm btn-danger btn-delete">删除</button>
                        </td>
                    </tr>
                </tbody>
            </table>
            <button v-on:click="doAdd('prize')" class="btn btn-md btn-primary add-btn">添加奖品</button>

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
                        <td>操作</td>
                    </tr>
                </thead>
                <tbody class="setting-table">
                    <tr v-for="member in members">
                        <td>{{member.id}}</td>
                        <td><input type="text" v-model="member.name"></td>
                        <td><input type="text" v-model="member.phone"></td>
                        <td>{{member.prizeName}}</td>
                        <td>{{member.online}}</td>
                        <td>{{member.activity}}</td>
                        <td>
                            <button v-on:click="doSave('member', member)" class="btn btn-sm btn-success btn-add">保存</button>
                            <button v-on:click="doDelete('member', member)" class="btn btn-sm btn-danger btn-delete">删除</button>
                        </td>
                    </tr>
                </tbody>
            </table>
            <button v-on:click="doAdd('member')" class="btn btn-md btn-primary add-btn">添加人员</button>

        </div>
    </div>


<script>
        var path = window.location.host;

        var app = new Vue({
            el: '#app',
            data: {
                members: [],
                prizes: []
            },
            created: function() {
                this.doGet('prize');
                this.doGet('member');
            },
            methods: {
                doGet: function(type){
                    $.ajax({url:"settings/" + type + (type == "member"?"/scale/0":""), contentType : "application/json", method: "get"})
                            .done(function(data){
                                app[type + "s"] = data;
                            });
                },
                doAdd: function(type){
                    app[type + "s"].push({method: 'add'});
                },
                doSave: function(type, data){
                    $.ajax({
                        url:"settings/" + type,
                        dataType:"json",
                        contentType : "application/json",
                        method: data.method?"post":"put",
                        data:JSON.stringify(data)
                    }).done(function(data){
                        app.doGet(type);
                    })
                },
                doDelete: function(type, data){
                    $.ajax({
                        url:"settings/" + type + "/id/" + data.id,
                        dataType:"json",
                        contentType : "application/json",
                        method: "delete",
                        data:JSON.stringify(data)
                    }).done(function(data){
                        app.doGet(type);
                    })
                }
            }

        });


    </script>

</body>
</html>
