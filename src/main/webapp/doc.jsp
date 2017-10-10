<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Policy Machine API</title>
    <link rel="stylesheet" href="./css/theme.css">
    <link rel="stylesheet" href="./css/nav.css">
    <link href="https://fonts.googleapis.com/css?family=Open+Sans:400,700|Source+Code+Pro:300,600|Titillium+Web:400,600,700" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="swagger-ui.css" >
    <style>
        html
        {
            box-sizing: border-box;
            overflow: -moz-scrollbars-vertical;
            overflow-y: scroll;
        }
        *,
        *:before,
        *:after
        {
            box-sizing: inherit;
        }

        body {
            margin:0;
            background: lightgrey;
        }
    </style>
</head>

<body style="padding-bottom: 10px;">
<div class="header">
    <ul>
        <li style="float: right; margin-right: 5px; color: white; font-size: 40px">PolicyMachine</li>
        <li><a href="userguide.jsp" class="">User Guide</a></li>
        <li><a href="doc.jsp" class="pmactive">API Documentation</a></li>
        <li><a href="config.jsp" class="">Server Configuration</a></li>
    </ul>
</div>

<div class="card content" style="margin-right: 15%; margin-left: 15%; padding: 0 25px 50px 25px">
    <div id="swagger-ui"></div>
</div>
<script src="swagger-ui-bundle.js"> </script>
<script src="swagger-ui-standalone-preset.js"> </script>
<script>
    window.onload = function() {

        // Build a system
        const ui = SwaggerUIBundle({
            url: "/swagger.json",
            dom_id: '#swagger-ui',
            deepLinking: true,
            presets: [
                SwaggerUIBundle.presets.apis,
                SwaggerUIStandalonePreset
            ],
            plugins: [
                SwaggerUIBundle.plugins.DownloadUrl
            ],
            layout: "StandaloneLayout"
        });

        window.ui = ui
    }
</script>
</body>

</html>
