<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String path = request.getContextPath();
    String basePath = request.getServerName() + ":"
            + request.getServerPort() + path + "/";
    String basePath2 = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN"
"http://www.w3.org/TR/html4/strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title></title>
    <script src="//cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/handlebars.js/3.0.0/handlebars.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/list.js/1.1.1/list.min.js"></script>
    <style>
        @import url(https://fonts.googleapis.com/css?family=Lato:400,700);
        *, *:before, *:after {
            box-sizing: border-box;
        }
        li{
            list-style: none;
        }
        body {
            background: #C5DDEB;
            font: 14px/20px "Lato", Arial, sans-serif;
            padding: 40px 0;
            color: white;
        }

        .container {
            margin: 0 auto;
            width: 750px;
            background: #444753;
            border-radius: 5px;
        }

        .people-list {
            width: 260px;
            float: left;
        }
        .people-list .search {
            padding: 20px;
        }
        .people-list input {
            border-radius: 3px;
            border: none;
            padding: 14px;
            color: white;
            background: #6A6C75;
            width: 90%;
            font-size: 14px;
        }
        .people-list .fa-search {
            position: relative;
            left: -25px;
        }
        .people-list ul {
            padding: 20px;
            height: 770px;
        }
        .people-list ul li {
            padding-bottom: 20px;
        }
        .people-list img {
            float: left;
        }
        .people-list .about {
            float: left;
            margin-top: 8px;
        }
        .people-list .about {
            padding-left: 8px;
        }
        .people-list .status {
            color: #92959E;
        }

        .chat {
            width: 490px;
            float: left;
            background: #F2F5F8;
            border-top-right-radius: 5px;
            border-bottom-right-radius: 5px;
            color: #434651;
        }
        .chat .chat-header {
            padding: 20px;
            border-bottom: 2px solid white;
        }
        .chat .chat-header img {
            float: left;
        }
        .chat .chat-header .chat-about {
            float: left;
            padding-left: 10px;
            margin-top: 6px;
        }
        .chat .chat-header .chat-with {
            font-weight: bold;
            font-size: 16px;
        }
        .chat .chat-header .chat-num-messages {
            color: #92959E;
        }
        .chat .chat-header .fa-star {
            float: right;
            color: #D8DADF;
            font-size: 20px;
            margin-top: 12px;
        }
        .chat .chat-history {
            padding: 30px 30px 20px;
            border-bottom: 2px solid white;
            overflow-y: scroll;
            height: 575px;
        }
        .chat .chat-history .message-data {
            margin-bottom: 15px;
        }
        .chat .chat-history .message-data-time {
            color: #a8aab1;
            padding-left: 6px;
        }
        .chat .chat-history .message {
            color: white;
            padding: 18px 20px;
            line-height: 26px;
            font-size: 16px;
            border-radius: 7px;
            margin-bottom: 30px;
            width: 90%;
            position: relative;
        }
        .chat .chat-history .message:after {
            bottom: 100%;
            left: 7%;
            border: solid transparent;
            content: " ";
            height: 0;
            width: 0;
            position: absolute;
            pointer-events: none;
            border-bottom-color: #86BB71;
            border-width: 10px;
            margin-left: -10px;
        }
        .chat .chat-history .my-message {
            background: #86BB71;
        }
        .chat .chat-history .other-message {
            background: #94C2ED;
        }
        .chat .chat-history .other-message:after {
            border-bottom-color: #94C2ED;
            left: 93%;
        }
        .chat .chat-message {
            padding: 30px;
        }
        .chat .chat-message textarea {
            width: 100%;
            border: none;
            padding: 10px 20px;
            font: 14px/22px "Lato", Arial, sans-serif;
            margin-bottom: 10px;
            border-radius: 5px;
            resize: none;
        }
        .chat .chat-message .fa-file-o, .chat .chat-message .fa-file-image-o {
            font-size: 16px;
            color: gray;
            cursor: pointer;
        }
        .chat .chat-message button {
            float: right;
            color: #94C2ED;
            font-size: 16px;
            text-transform: uppercase;
            border: none;
            cursor: pointer;
            font-weight: bold;
            background: #F2F5F8;
        }
        .chat .chat-message button:hover {
            color: #75b1e8;
        }

        .online, .offline, .me {
            margin-right: 3px;
            font-size: 10px;
        }

        .online {
            color: #86BB71;
        }

        .offline {
            color: #E38968;
        }

        .me {
            color: #94C2ED;
        }

        .align-left {
            text-align: left;
        }

        .align-right {
            text-align: right;
        }

        .float-right {
            float: right;
        }

        .clearfix:after {
            visibility: hidden;
            display: block;
            font-size: 0;
            content: " ";
            clear: both;
            height: 0;
        }

    </style>

</head>
<body>

<div class="container clearfix">
    <div class="people-list" id="people-list">
        <div class="search">
            <input type="text" placeholder="search" />
            <i class="fa fa-search"></i>
        </div>
        <ul id="online-list" class="list">
            <c:forEach var="v" items="${users}" >
                <li class="clearfix" data-id="${v.key}" data-name="${v.value.getName()}">
                    <img src="https://s3-us-west-2.amazonaws.com/s.cdpn.io/195612/chat_avatar_0${v.key}.jpg" alt="avatar" />
                    <div class="about">
                        <div class="name">${v.value.getName()}</div>
                        <div class="status">
                            <i class="fa fa-circle online"></i> ${v.value.getStatus()?"在线":"离线"}
                        </div>
                    </div>
                </li>

            </c:forEach>


        </ul>
    </div>

    <div class="chat">
        <div id="chat-subject" class="chat-header clearfix">
            <img src="https://s3-us-west-2.amazonaws.com/s.cdpn.io/195612/chat_avatar_01_green.jpg" alt="avatar" />

            <div class="chat-about">
                <div class="chat-with">Chat with Vincent Porter</div>
                <div class="chat-num-messages">already 1 902 messages</div>
            </div>
            <i class="fa fa-star"></i>
        </div> <!-- end chat-header -->

        <div class="chat-history">
            <ul>


            </ul>

        </div> <!-- end chat-history -->

        <div class="chat-message clearfix">
            <textarea name="message-to-send" id="message-to-send" placeholder ="Type your message" rows="3"></textarea>

            <i class="fa fa-file-o"></i> &nbsp;&nbsp;&nbsp;
            <i class="fa fa-file-image-o"></i>

            <button>Send</button>

        </div> <!-- end chat-message -->

    </div> <!-- end chat -->

</div> <!-- end container -->

<script id="chatsubject-template" type="text/x-handlebars-template">
    <img src="https://s3-us-west-2.amazonaws.com/s.cdpn.io/195612/chat_avatar_0{{id}}_green.jpg" alt="avatar" />

    <div class="chat-about">
        <div class="chat-with">正在与{{name}}聊天</div>
        <div class="chat-num-messages">已经有{{chatNum}}聊天记录</div>
    </div>
    <i class="fa fa-star"></i>
</script>

<script id="message-template" type="text/x-handlebars-template">
    <li class="clearfix">
        <div class="message-data align-right">
            <span class="message-data-time" >{{time}}, Today</span> &nbsp; &nbsp;
            <span class="message-data-name" >Olia</span> <i class="fa fa-circle me"></i>
        </div>
        <div class="message other-message float-right">
            {{messageOutput}}
        </div>
    </li>
</script>

<script id="message-response-template" type="text/x-handlebars-template">
    <li>
        <div class="message-data">
            <span class="message-data-name"><i class="fa fa-circle online"></i> {{name}}</span>
            <span class="message-data-time">{{time}}, Today</span>
        </div>
        <div class="message my-message">
            {{response}}
        </div>
    </li>
</script>


<script>
    var path = '<%=basePath%>';
    var uid=${uid eq null?-1:uid};
    if(uid==-1){
        location.href="<%=basePath2%>";
    }
    var from=uid;
    var fromName='${name}';

    var websocket;
    if ('WebSocket' in window) {
        websocket = new WebSocket("ws://" + path + "/ws?uid="+uid + "name=" + fromName);
    } else if ('MozWebSocket' in window) {
        websocket = new MozWebSocket("ws://" + path + "/ws"+uid + "name=" + fromName);
    } else {
        websocket = new SockJS("http://" + path + "/ws/sockjs"+uid + "name=" + fromName);
    }
    websocket.onopen = function(event) {
        console.log("WebSocket:已连接");
        console.log(event);
    };
    websocket.onmessage = function(event) {
        var data=JSON.parse(event.data);
        chat.receivedMessage(data);
    };
    websocket.onerror = function(event) {
        console.log("WebSocket:发生错误 ");
        console.log(event);
    };
    websocket.onclose = function(event) {
        console.log("WebSocket:已关闭");
        console.log(event);
    };

    (function(){

        window.chat = {
            messageToSend: '',
            historyNum: 0,
            messageResponses: [
                'Why did the web developer leave the restaurant? Because of the table layout.',
                'How do you comfort a JavaScript bug? You console it.',
                'An SQL query enters a bar, approaches two tables and asks: "May I join you?"',
                'What is the most used language in programming? Profanity.',
                'What is the object-oriented way to become wealthy? Inheritance.',
                'An SEO expert walks into a bar, bars, pub, tavern, public house, Irish pub, drinks, beer, alcohol'
            ],
            init: function() {
                this.cacheDOM();
                this.bindEvents();
                this.render();
            },
            cacheDOM: function() {
                this.$chatHistory = $('.chat-history');
                this.$button = $('button');
                this.$textarea = $('#message-to-send');
                this.$chatHistoryList =  this.$chatHistory.find('ul');
                this.$chatSubject = $('#chat-subject');
                this.$onlineList = $('#online-list');
            },
            bindEvents: function() {
                this.$button.on('click', this.addMessage.bind(this));
                this.$onlineList.on('click', 'li', function(e){
                    var targetid = e.currentTarget.dataset.id;
                    var targetname = e.currentTarget.dataset.name;
                    var user = {
                        id: targetid,
                        name: targetname
                    };
                    this.changeSubject(user)
                }.bind(this));
                this.$textarea.on('keyup', this.addMessageEnter.bind(this));
            },
            render: function() {
                this.scrollToBottom();
                if (this.messageToSend.trim() !== '') {
                    var template = Handlebars.compile( $("#message-template").html());
                    var context = {
                        messageOutput: this.messageToSend,
                        time: this.getCurrentTime()
                    };

                    this.$chatHistoryList.append(template(context));
                    this.scrollToBottom();
                    this.$textarea.val('');

//                    // responses
//                    var templateResponse = Handlebars.compile( $("#message-response-template").html());
//                    var contextResponse = {
//                        response: this.getRandomItem(this.messageResponses),
//                        time: this.getCurrentTime()
//                    };
//
//                    setTimeout(function() {
//                        this.$chatHistoryList.append(templateResponse(contextResponse));
//                        this.scrollToBottom();
//                    }.bind(this), 1500);

                }

            },
            renderHead: function(){
                this.currentUser.chatNum = this.historyNum;
                var templateSubject = Handlebars.compile( $("#chatsubject-template").html());
                this.$chatSubject.html(templateSubject(this.currentUser));
            },
            changeSubject: function(user){
                this.currentUser = user;
                this.renderHead();
                this.sendTo = user.id;
            },
            receivedMessage: function(data){
                // responses
                var templateResponse = Handlebars.compile( $("#message-response-template").html());
                var contextResponse = {
                    response: data.text,
                    time: this.getCurrentTime(),
                    name: data.fromName
                };
                this.$chatHistoryList.append(templateResponse(contextResponse));
                this.scrollToBottom();
                this.historyNum++;
                this.renderHead();
            },

            addMessage: function() {
                this.messageToSend = this.$textarea.val();
                this.sendMessage();
                this.render();
            },
            sendMessage: function(){
                var data={};
                data["from"]=from;
                data["fromName"]=fromName;
                data["to"]=this.sendTo;
                data["text"]=this.messageToSend;
                websocket.send(JSON.stringify(data));
                this.historyNum++;
                this.renderHead();

            },
            addMessageEnter: function(event) {
                // enter was pressed
                if (event.keyCode === 13) {
                    this.addMessage();
                }
            },
            scrollToBottom: function() {
                this.$chatHistory.scrollTop(this.$chatHistory[0].scrollHeight);
            },
            getCurrentTime: function() {
                return new Date().toLocaleTimeString().
                replace(/([\d]+:[\d]{2})(:[\d]{2})(.*)/, "$1$3");
            },
            getRandomItem: function(arr) {
                return arr[Math.floor(Math.random()*arr.length)];
            }

        };

        chat.init();

        var searchFilter = {
            options: { valueNames: ['name'] },
            init: function() {
                var userList = new List('people-list', this.options);
                var noItems = $('<li id="no-items-found">No items found</li>');

                userList.on('updated', function(list) {
                    if (list.matchingItems.length === 0) {
                        $(list.list).append(noItems);
                    } else {
                        noItems.detach();
                    }
                });
            }
        };

        searchFilter.init();

    })();

</script>
</body>
</html>