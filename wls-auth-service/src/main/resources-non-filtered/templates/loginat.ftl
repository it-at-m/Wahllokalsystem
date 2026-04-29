<html>
<head>
    <link rel="stylesheet" href="css/bootstrap.min.css"/>
    <link rel="stylesheet" href="css/style.css"/>
    <style>
        .password-input-wrapper {
            position: relative;
        }

        .password-input-wrapper .form-control {
            padding-right: 40px;
        }

        .password-press-to-show {
            position: absolute;
            top: 0;
            right: 0;
            height: 100%;
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
                    <h3>Admin-Tool</h3>
                    <form role="form" action="login" method="post" class="formpart">
                        <div class="form-group">
                            <label for="username">Benutzername</label>
                            <input type="text" class="form-control" id="username" name="username"
                                   placeholder="Benutzername" required>
                        </div>
                        <div class="form-group">
                            <label for="password">Passwort</label>
                            <div class="password-input-wrapper">
                                <input type="password" class="form-control" id="password" name="password"
                                       placeholder="Passwort" required>
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
                        </div>
                        <input type="hidden" id="csrf_token" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <input type="submit" id="submit" value="Login" class="btn btn-default">
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
</body>
</html>
