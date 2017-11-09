<%--
  Created by IntelliJ IDEA.
  User: Superwen
  Date: 2017/3/31
  Time: 下午2:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <meta charset="UTF-8">
    <script type="text/javascript" src="//cdn.bootcss.com/jquery/1.11.1/jquery.min.js"></script>
    <script type="application/javascript">

        //标识，如果为true则每3秒访问一次进度
        var timeout = false;
        function progress(){
            if(!timeout) return;
            $.post("u_test.do",function(f){
                if(null == f){
                    $("#jindu").val("上传错误");
                }else{
                    $("#jindu").val(f+"%");
                }
            })
            setTimeout(progress,3000);
        }

        $(function(){
            $("#upload").click(function(){
                var uploadUrl = "upload.do";

                callback = function(res) {
                    timeout = false;
                    $("#d").append("上传结束" + res);
                    $("#jindu").val("100%");
                };
                uploading = function(res) {
                    $("#d").append(res+",");
                    timeout = true;
                    $("#jindu").val("1%");
                    setTimeout(progress,3000);
                };
                beforeSend = function() {
                    $("#d").append("开始");
                };

                var file = document.getElementById("myUpload-input").files[0];
                if(beforeSend instanceof Function){
                    if(beforeSend(file) === false){
                        return false;
                    }
                }

                var fd = new FormData();
                fd.append("file", file);
                var xhr = new XMLHttpRequest();
                xhr.onreadystatechange = function() {
                    if (xhr.readyState == 4 && xhr.status == 200) {
                        if(callback instanceof Function){
                            callback(xhr.responseText);
                        }
                    }
                }

                //侦查当前附件上传到TOMCAT的缓存情况，并不是真正传到了OSS中。如果是个大文件（2G+），这个过程也比较慢，有个初始化的进度条也可以
                xhr.upload.onprogress = function(evt) {
                    var loaded = evt.loaded;
                    var tot = evt.total;
                    var per = Math.floor(100 * loaded / tot); //已经上传的百分比
                    if(uploading instanceof Function){
                        uploading(per);
                    }
                };

                xhr.open("post", uploadUrl);
                xhr.send(fd);
            })
        });

    </script>
</head>
<body>
//如果能得到文件的url，后端直接就使用该url即可，也方便，file不用转来转去
<!--请输入url或文件路径：<input name="url" value="" id="url" width="200px"/><br>-->

<input type="file" id="myUpload-input" name="file"/><br>
<input type="button" value="点击上传" id="upload"/>

<div id="d">存入缓存的进度：</div>
上传的进度：<input id="jindu" value="0" />
</body>
</html>
