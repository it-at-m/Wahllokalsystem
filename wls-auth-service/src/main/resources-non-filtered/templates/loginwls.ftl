<html>
<head>
    <link rel="stylesheet" href="css/bootstrap.min.css"/>
    <link rel="stylesheet" href="css/style.css"/>
    <style>
        .material-button {
            display: block;
            margin-left: auto;
            width: 86px;
            line-height: 32px !important;
        }

        .material-form-input.password {
            margin: 40px 0;
            padding-bottom: 8px;
        }

        .material-placeholder {
            top: -2 !important;
        }

        .material-input {
            font-family: monospace;
        }

        .lp_minimize {
            visibility: hidden;
        }
    </style>
</head>
<body>
<#if error??>
    <div class="alert alert-danger">
        <p>Fehler beim Login.</p>
        <p>${error}</p>
    </div>
</#if>
<#if _csrf??>
    <div class="vertical-center">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-sm-6 col-md-4 col-md-offset-4" id="login-form">
                    <h3>Wahllokalsystem abc</h3>
                    <h4>${willkommensnachricht}</h4>
                    <form role="form" action="login" method="post">
                        <div class="material-form-input">
                            <label class="material-label">
                                <input class="material-input" type="text" id="username" name="username" required>
                                <span class="material-placeholder">Benutzername</span>
                            </label>
                        </div>

                        <div class="material-form-input password">
                            <label class="material-label">
                                <input class="material-input" autocomplete="off" type="text" id="password"
                                       name="password" required>
                                <span class="material-placeholder">Passwort</span>
                            </label>
                        </div>
                        <input type="hidden" id="csrf_token" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <div onClick="javascript:this.parentNode.submit();" type="submit" id="submit" value="Login"
                             class="material-button">Login
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
<#else>
    <div class="alert alert-danger">
        <p>There was a problem logging in.</p>
    </div>
</#if>

<script src="js/wro.js" type="text/javascript"></script>
<script src="js/bootstrap.js" type="text/javascript"></script>
<script src="js/checkCapslock.js" type="text/javascript"></script>

<script>

    /**
     * Login wenn "Enter" gedrückt wird.
     */
    document.addEventListener("keyup", (event) => {
        if (event.keyCode === 13) {
            document.querySelector("form").submit();
        }
    });

</script>

</body>
</html>
