<%@ page pageEncoding="UTF-8" %>
<html>
<head>
    <jsp:include page="/WEB-INF/pages/plugins/include_basepath.jsp"/>
    <script type="text/javascript" src="jquery/jquery.min.js"></script>
    <script type="text/javascript">
        var authcode = "bdab43de3ddc197fbbb91f1ad4bb4d4f" ;
        $(function(){
            $(tokenButton).on("click",function() { // 进行异步的请求发送
                $.ajax({    // 对于需要发送的请求内容进行相应的JSON配置
                    url : "http://www.oauth.com/oauth2/accessToken.action" ,       // Ajax异步请求的处理路径
                    data : {
                        code : authcode ,    // 设置要传递的参数信息
                        client_id: "client000111" ,
                        client_secret: "2EF93F5CB1D2E8DE4C2BCD419F3D5CF8" ,
                        grant_type: "authorization_code" ,
                        redirect_uri: "http://www.crm.com/sin"
                    },
                    dataType : "json" ,       // 回应的数据类型
                    method : "post" ,
                    success : function(data) {  // 后台响应完毕正常处理
                        console.log(data) ; // 输出返回的结果数据
                        $(showDiv).append("<p>"+data+"</p>") ;
                    } ,
                    error : function(e) {
                        console.log("代码出错啦！") ;
                        console.log(e) ;
                    }
                })
            }) ;
        }) ;
    </script>
</head>
<body>
<div id="inputDiv">
    <button id="tokenButton">获取AccessToken</button>
</div>
<div id="showDiv"></div>
</body>
</html>
