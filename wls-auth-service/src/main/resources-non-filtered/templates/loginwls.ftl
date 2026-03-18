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
            position: relative;
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

        .material-form-input.password .material-input {
            padding-right: 40px;
        }

        .password-press-to-show {
            position: absolute;
            right: 0;
            top: 15px;
            height: 30px;
            display: flex;
            align-items: center;
            border: 0;
            background: transparent;
            color: #888;
            padding: 0 6px;
            cursor: pointer;
            user-select: none;
        }

        .password-press-to-show:hover {
            color: #444;
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
                    <h3>Wahllokalsystem</h3>
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
                                <input class="material-input" autocomplete="off" type="password" id="password"
                                       name="password" required>
                                <span class="material-placeholder">Passwort</span>
                            </label>
                            <button type="button" id="password-press-to-show" class="password-press-to-show"
                                    aria-label="Passwort anzeigen (gedrückt halten)">
                                <svg xmlns="http://www.w3.org/2000/svg" width="21" height="21" viewBox="0 0 24 24"
                                     fill="none" stroke="currentColor" stroke-width="2"
                                     stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                                    <circle cx="12" cy="12" r="3"/>
                                </svg>
                            </button>
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
<script src="js/passwordPressToShow.js" type="text/javascript"></script>

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
