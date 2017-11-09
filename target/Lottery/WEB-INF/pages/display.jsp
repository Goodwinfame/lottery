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
    <script src="node_modules/vue/dist/vue.js"></script>
    <script src="node_modules/vue-router/dist/vue-router.js"></script>
    <%--<script src="dist/js/faces.js"></script>--%>
    <link rel="stylesheet" href="dist/fonts/myfont.css">
    <link rel="stylesheet" href="dist/css/display.css">

</head>
<body>
<div id="app">
    <%--<div id="snowZone"></div>--%>
    <div class="container">
        <transition name="slide-fade">
            <router-view></router-view>

        </transition>
    </div>
    <canvas id="world"></canvas>

    <div class="status" v-bind:class="{'green':status,'red':!status}"></div>
</div>

<script src="dist/js/snowfall.js"></script>



<script type="text/x-template" id="user-template">
    <li class="user shake" :class="[user.shakeStyle,shake?'freez':'']">
        <div class="user-name">{{user.name}}</div>
        <div class="user-photo" :style="{backgroundImage: 'url(' + user.photo + ')'}"></div>
    </li>
</script>
<script type="text/x-template" id="winner-template">
    <li class="user">
        <div class="user-name">{{user.name}}</div>
        <div class="user-photo" :style="{backgroundImage: 'url(' + user.photo + ')'}"></div>
        <div class="user-prize">{{user.prize.name}}</div>
    </li>
</script>
<script type="text/x-template" id="index-template">
    <div class="wrapper">
        <h1 class="wrapper-cell">
            <span class="clip-text">A b l o o m y</span>
            <span class="clip-text clip-text-emoji"></span>
            <span class="clip-text clip-text-small">武 汉 年 会</span>
        </h1>
    </div>

</script>
<script type="text/x-template" id="prizepage-template">
    <div class="prizepage">
        <ul class="users">
            <%--<transition-group name="list">--%>
                <comp-user v-for="item in users" v-bind:key="item" :user="item" :shake="drawing"></comp-user>
            <%--</transition-group>--%>
        </ul>
        <div id="congrats-text">
            <h1>抽取{{currentPrize.level | prizeLevel}}等奖{{count}}名&nbsp;<span v-for="item in winner">{{item.name}}&nbsp;</span></h1>
            <div class="selectarea">{{phone}}</div>
            <div class="winners">
                <p>幸运的Abloomiers</p>
                <ul class="users">
                    <transition-group name="list">
                        <comp-winner v-for="item in winner" v-bind:key="item" :user="item"></comp-winner>
                    </transition-group>
                </ul>
            </div>

        </div>

    </div>

</script>

<script>
    Vue.use(VueRouter);
    Vue.component('comp-user', {
        template: '#user-template',
        props: ['user','shake']
    });
    Vue.component('comp-winner', {
        template: '#winner-template',
        props: ['user']
    });

    var ThanksPage = {
        template: "#thanks-component-template"
    };

    var ResultPage = {
        template: "#result-component-template"
    };

    var UserPage = {
        template: '<table class="table table-striped"><thead><tr> <td>姓名</td><td>手机号</td><td>活力值</td></tr> </thead><tbody><tr v-for="member in members"><td>{{member.id}}</td><td>{{member.name}}</td><td>{{member.phone}}</td> <td>{{member.prize}}</td><td>{{member.online}}</td> <td>{{member.activity}}</td> </tr> </tbody> </table>',
        data() {
            return {
                members:[]
            }
        }
    };
    function genNewNum(len,indexObj, type){
        var flag = true;
        var returnNum;
        while (flag){
            var tempNum = Math.floor(Math.random() * len);
            if (tempNum != indexObj[type]){
                returnNum = tempNum;
                flag = false;
            }
        }
        indexObj[type] = returnNum;
        return returnNum
    }

    var PrizePage = {
        template: '#prizepage-template',
        data(){
            return {
                users: [],
                phone: "___________",
                drawing: false,
                winner: [],
                prizes: [],
                currentPrize: {},
                count: 0
            }
        },
        beforeRouteEnter (to, from, next) {

            next(function(vm){
                vm.initData(to, from, vm);

                $.ajax({url:"settings/prize", contentType : "application/json", method: "get"})
                        .done(function(data){
                            vm.prizes = data;
                            vm.intiPrize(to,vm)
                        });

            })
        },
        beforeRouteUpdate (to, from, next) {
            this.phone = "___________";
            this.initData(to, from, this);
            this.intiPrize(to,this);
            next();

        },
        filters: {
            prizeLevel: function (level) {
                switch (level) {
                    case 0:
                        level = "特";
                        break;
                    case 1:
                        level = "一";
                        break;
                    case 2:
                        level = "二";
                        break;
                    case 3:
                        level = "三";
                        break;
                    case 4:
                        level = "四";
                        break;
                    case 5:
                        level = "五";
                        break;
                    default:
                        break;

                }
                return level
            }
        },
        methods: {
            intiPrize(to,vm){
                vm.count = 0;
                for (var i=0;i<vm.prizes.length;i++){
                    if (to.params.level == vm.prizes[i].level){
                        vm.currentPrize = vm.prizes[i];
                        vm.count = vm.count + vm.prizes[i].count;
                    }
                }
            },
            initData(to,from, vm){

                $.ajax({url:"settings/member/scale/0", contentType : "application/json", method: "get"})
                        .done(function(data){
//                            var facesLen = systemFaces.length | 0;
//                            var randomIndex = {photo: null};
                            vm.users = [];
                            vm.winner = [];
                            data.forEach(function(item){
//                                if(item.photo == null){
//                                    item.photo = systemFaces[genNewNum(facesLen, randomIndex, 'photo')]
//                                }
                                if (item.prize && item.prize.level == to.params.level){
                                    vm.winner.push(item);
                                } else if (!item.prize){
                                    vm.users.push(item);
                                }
                            });
                        });
            },
            start(winner) {
//                if (this.currentPrize.count == this.currentPrize.drawed){
//                    return;
//                }
                letterChangeCount=0;
                var shakeStyles = ["shake-slow","shake-slow1","shake-slow2","shake-slow3","shake-little","shake-little1","shake-little2","shake-little3","shake-horizontal","shake-horizontal1","shake-horizontal2","shake-horizontal","shake-vertical3","shake-rotate","shake-rotate1","shake-rotate2","shake-rotate3"];
                var randomIndex = {shake: null};

                this.users.forEach(function(item){
                    if (item.id == winner.id){
                        item.prize = winner.prize;
                    }

                    item.shakeStyle = shakeStyles[genNewNum(shakeStyles.length, randomIndex, 'shake')];

                });

                selected = winner.phone.toString();
                covered = selected.replace(/[^\s]/g, '_');
                this.phone = covered;
                timer = setInterval(this.decode, 50);
                this.drawing = true;
            },
            decode() {
                letterChangeCount ++;
                newtext = covered.split('').map(this.changeLetter()).join('');
                if(newtext == covered || letterChangeCount >= 100){
                    this.phone = selected;
                    clearTimeout(timer);
                    this.drawing = false;
                    var winner = this.deleteMember(selected)[0];
                    if (winner){
                        this.winner.push(winner);

                    }
//                    this.currentPrize.drawed ++;
                    return false;
                }
                covered = newtext;
                this.phone = newtext;
            },
            deleteMember(phone){
                for (var i = 0; i < this.users.length; i++){
                    if(phone == this.users[i].phone){
                        return this.users.splice(i,1);
                    }
                }
                return [];

            },
            changeLetter() {
                replacements = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz%!@&*#_';
                replacementsLen = replacements.length | 0;
                return function(letter, index, err){
                    if(selected[index] === letter){
                        return letter;
                    } else {
                        return replacements[Math.floor(Math.random() * replacementsLen)];
                    }
                }
            }
        }
    };

    var IndexPage = {
        template: '#index-template'
    };

    var router = new VueRouter({
        routes: [
            { name: 'index', path: '', component: IndexPage},
            { name: 'prize', path: '/prize/:level', component: PrizePage},
            { name: 'user', path: '/user', component: UserPage},
            { name: 'result', path: '/result', component: ResultPage},
            { name: 'thanks', path: '/thanks', component: ThanksPage}
        ]
    });

    var app = new Vue({ router,data: {status: false} }).$mount('#app');






</script>
<script>



    var path= window.location.host;

    var websocket;
    (function startSocketConnection(data){
        if ('WebSocket' in window) {
            websocket = new WebSocket("ws://" + path + "/ws?id=9999");
        } else if ('MozWebSocket' in window) {
            websocket = new MozWebSocket("ws://" + path + "/ws?id=9999");
        } else {
            websocket = new SockJS("http://" + path + "/ws/sockjs?id=9999");
        }
        websocket.onopen = function(event) {
            console.log("WebSocket:已连接");
            console.log(event);
            app.status = true;
        };
        websocket.onmessage = function(event) {
            var respones=JSON.parse(event.data);
            if (!respones) return;
            if (respones.msg != null && respones.msg == "routerGo"){
                router.push({name: respones.data, params: respones.param});
                return;
            } else if(respones.msg != null && respones.msg == "doDraw"){

                if(router.currentRoute.name == "user"){
                    var flag = true;
                    for (var i = 0; i < app.$children[0].members.length; i++){
                        if (app.$children[0].member[i].id == respones.data.id){
                            $.extend(app.$children[0].member[i], respones.data);
                            flag = false;
                            return false;
                        }
                    }
                    if (flag) app.$children[0].members.push(respones.data);
                } else if (router.currentRoute.name == "prize") {
                    app.$children[0].start(respones.data);
                }

            }else if(respones.msg != null && respones.msg == "resetPrize"){
                var prize = respones.data;
                app.$children[0].winner.forEach(function(item, index){
                    if (item.prize.id == prize.id){
                        var user = app.$children[0].winner.splice(index,1)[0];
                        app.$children[0].users.push(user);
                        app.$children[0].phone = "___________";
                    }
                })
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
