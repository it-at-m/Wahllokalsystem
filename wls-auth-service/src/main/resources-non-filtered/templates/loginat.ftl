<html>
<head>
    <link rel="stylesheet" href="css/bootstrap.min.css"/>
    <link rel="stylesheet" href="css/style.css"/>
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
                            <input type="password" class="form-control" id="password" name="password"
                                   placeholder="Passwort" required>
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
</body>
</html>
