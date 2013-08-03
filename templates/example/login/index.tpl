<html>
    <head>
        <title>Hello!</title>

        <link href="/css/bootstrap-v3.0.0.css" rel="stylesheet">

        <style>
            body {
                padding-top: 40px;
                padding-bottom: 40px;
                background-color: #eee;
            }
            a {
                color: firebrick;
            }
        </style>
    </head>
    <body>
        <div style="width:640px;margin:auto;" class="alert alert-info">
            <#if isLoggedIn>
                Welcome on our awesome website dear <b>${username}</b>! You are officially logged in! :) Logout <a href="/logout">here!</a>
            <#else>
                Welcome on our awesome website! Please login <a href="/login">here</a>!
            </#if>
        </div>
    </body>
</html>