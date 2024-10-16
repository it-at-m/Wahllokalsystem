<html>
<head>
    <link rel="stylesheet" href="css/bootstrap.min.css"/>
    <link rel="stylesheet" href="css/style.css"/>
</head>
<body>
<script type="text/javascript">
    function checkIfButtonDisabled(f) {
        if (undefined === document.getElementById('at') || undefined === document.getElementById('mt') ||
            null === document.getElementById('at') || null === document.getElementById('mt') ||
            document.getElementById('at').checked || document.getElementById('mt').checked) {
            document.getElementById('submitMessage').innerHTML = "";
            document.getElementById("submit").disabled = false;
            return false;
        } else {
            document.getElementById('submitMessage').innerHTML = "Bitte wählen Sie eine Option aus!";
            document.getElementById("submitMessage").style.display = "block";
            document.getElementById("submitMessage").style.color = "red";
            document.getElementById("submit").disabled = true;
            return true;
        }
    }
</script>
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
                    <h3>Login</h3>
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
                        <p>Wählen Sie eines der folgenden Tools aus:</p>
                        <fieldset>
                            <input type="radio" id="at" name="Toolname" value="AdminTool"
                                   onchange="checkIfButtonDisabled(this)"> <label for="at"> Admin-Tool</label><br>
                            <input type="radio" id="mt" name="Toolname" value="MonitoringTool"
                                   onchange="checkIfButtonDisabled(this)"> <label for="mt"> Monitoring-Tool</label><br>
                        </fieldset>
                        <p id="submitMessage">Bitte wählen Sie eine Option aus!</p>
                        <input type="hidden" id="csrf_token" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <input type="submit" id="submit" value="Login" class="btn btn-default"
                               disabled="checkIfButtonDisabled(this)">
                        <script>
                            checkIfButtonDisabled(this);
                        </script>
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