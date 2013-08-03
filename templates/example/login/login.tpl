<html>
    <head>
        <title>Login!</title>
        
        <link href="//netdna.bootstrapcdn.com/bootstrap/3.0.0-rc1/css/bootstrap.min.css" rel="stylesheet">

        <style>
            body {
                padding-top: 40px;
                padding-bottom: 40px;
                background-color: #eee;
            }

            .form-signin {
                max-width: 330px;
                padding: 15px;
                margin: 0 auto;
            }
            .form-signin .form-signin-heading,
            .form-signin .checkbox {
                margin-bottom: 10px;
            }
            .form-signin .checkbox {
                font-weight: normal;
            }
            .form-signin input[type="text"],
            .form-signin input[type="password"] {
                position: relative;
                font-size: 16px;
                height: auto;
                padding: 10px;
                -webkit-box-sizing: border-box;
                -moz-box-sizing: border-box;
                box-sizing: border-box;
            }
            .form-signin input[type="text"]:focus,
            .form-signin input[type="password"]:focus {
                z-index: 2;
            }
            .form-signin input[type="text"] {
                margin-bottom: -1px;
                border-bottom-left-radius: 0;
                border-bottom-right-radius: 0;
            }
            .form-signin input[type="password"] {
                margin-bottom: 10px;
                border-top-left-radius: 0;
                border-top-right-radius: 0;
            }

            .bs-callout {
                margin: 20px 0;
                padding: 15px 30px 15px 15px;
                border-left: 5px solid #eee;
            }
            .bs-callout h4 {
                margin-top: 0;
            }
            .bs-callout p:last-child {
                margin-bottom: 0;
            }
            .bs-callout code,
            .bs-callout .highlight {
                background-color: #fff;
            }

            .bs-callout-danger {
                background-color: #fcf2f2;
                border-color: #dFb5b4;
            }
            .bs-callout-warning {
                background-color: #fefbed;
                border-color: #f1e7bc;
            }
            .bs-callout-info {
                background-color: #f0f7fd;
                border-color: #d0e3f0;
            }

        </style>
    </head>
    <body>
        <div class="container">

            <div style="width: 640px;margin: auto;">
                <form method="POST" action="/login" class="form-signin">
                    <h2 class="form-signin-heading">Please sign in</h2>
                    <input name="username" type="text" class="input-block-level" placeholder="Username" autofocus required>
                    <input name="password" type="password" class="input-block-level" placeholder="Password" required>
                    <button class="btn btn-large btn-primary btn-block" type="submit">Sign in</button>
                </form>

                <div class="bs-callout bs-callout-info">
                    <h4>Default passwords</h4>
                    <p>Use admin/admin as username and password.</p>
                </div>
            </div>

        </div>
    </body>
</html>
