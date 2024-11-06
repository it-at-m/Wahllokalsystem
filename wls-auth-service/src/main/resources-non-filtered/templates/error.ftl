<html>
<head>
    <link rel="stylesheet" href="css/bootstrap.min.css"/>
    <link rel="stylesheet" href="css/style.css"/>
</head>
<body>

<#if error??>
    <div class="alert alert-danger">
        <p>Unbekannter Fehler.</p>
        <p>${error}</p>
    </div>
</#if>
<script src="js/wro.js" type="text/javascript"></script>
<script src="js/bootstrap.js" type="text/javascript"></script>
<script src="js/checkCapslock.js" type="text/javascript"></script>
</body>
</html>

