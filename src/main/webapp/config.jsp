<html>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="./css/theme.css">
<link rel="stylesheet" href="./css/nav.css">
<style>
    input[type=text]{
        margin-bottom: 3%;
    }
    body{
        background-color: lightgrey;
        padding-bottom: 20px;
    }
</style>
<body>
<div class="header">
    <ul>
        <li style="float: right; margin-right: 5px; color: white; font-size: 40px">PolicyMachine</li>
        <li><a href="userguide.jsp" class="">User Guide</a></li>
        <li><a href="doc.jsp" class="">API Documentation</a></li>
        <li><a href="config.jsp" class="pmactive">Server Configuration</a></li>
    </ul>
</div>
<div class="footer" onclick="this.style.display='none'">
    <div class="error">${errorMessage}</div>
    <div class="success">${successMessage}</div>
</div>
<div class="card content" style="padding: 15px; text-align: center; margin-right: 25%; margin-left: 25%">
    <h1>Policy Machine Configuration</h1>
    <div id="database-div">
        <h2>Select a Database</h2>
        <button class="green-btn" style="display: inline-block;width:45%" onclick="setDb('neo4j')">Neo4j</button>
        <button class="blue-btn" style="display: inline-block;width:45%" onclick="setDb('mysql')">MySQL</button>
        <div id="neo4j" class="card" style="display: none; padding: 15px;">
            <form action="SetConnection" method="post" style="text-align: left">
                <input type="hidden" name="database" id="database" value="neo4j">

                <label for="host" style="font-weight: bold">Host</label>
                <input class="green-txt" type="text" name="host" id="host" value="localhost" placeholder="host">

                <label for="port" style="font-weight: bold">Port</label>
                <input class="green-txt" type="text" name="port" id="port" value="7474" placeholder="port">

                <label for="username" style="font-weight: bold">Username</label>
                <input class="green-txt" type="text" name="username" id="username" value="neo4j" placeholder="username">

                <label for="password" style="font-weight: bold">Password</label>
                <input class="green-txt" type="text" name="password" id="password" value="root" placeholder="password">

                <input class="green-btn" type="submit" value="Connect"/>
            </form>
        </div>
        <div id="mysql" class="card" style="display: none; padding: 15px;">
            <form action="SetConnection" method="post" style="text-align: left">
                <input type="hidden" name="database" id="database" value="mysql">

                <label for="host" style="font-weight: bold">Host</label>
                <input class="blue-txt" type="text" name="host" id="host" value="localhost" placeholder="host">

                <label for="port" style="font-weight: bold">Port</label>
                <input class="blue-txt" type="text" name="port" id="port" value="3306" placeholder="port">

                <label for="username" style="font-weight: bold">Username</label>
                <input class="blue-txt" type="text" name="username" id="username" value="root" placeholder="username">

                <label for="password" style="font-weight: bold">Password</label>
                <input class="blue-txt" type="text" name="password" id="password" value="root" placeholder="password">

                <label for="schema" style="font-weight: bold">Database</label>
                <input class="blue-txt" type="text" name="schema" id="schema" value="pmwsdb" placeholder="schema">

                <input class="blue-btn" type="submit" value="Connect"/>
            </form>
        </div>
        <script type="text/javascript">
            function setDb(db){
                if(db === 'neo4j'){
                    if(document.getElementById('neo4j').style.display === 'block'){
                        document.getElementById('neo4j').style.display = 'none';
                    }else {
                        document.getElementById('neo4j').style.display = 'block';
                        document.getElementById('mysql').style.display = 'none';
                    }
                }else{
                    if(document.getElementById('mysql').style.display === 'block'){
                        document.getElementById('mysql').style.display = 'none';
                    }else {
                        document.getElementById('neo4j').style.display = 'none';
                        document.getElementById('mysql').style.display = 'block';
                    }
                }
            }
        </script>
    </div>
    <div>
        <h2>Set Configuration Dump Interval</h2>
        <p style="font-size: 12px; text-align: left">
            * The Policy Machine periodically saves the current policy configuration to avoid losing data.
            This value will tell the Policy Machine how often to save the current configuration. The default is 30 seconds.
            <strong>Value must be in seconds and greater than 0.</strong>
        </p>

        <form action="SetInterval" method="post" style="text-align: left">
            <label for="interval" style="font-weight: bold">Interval</label>
            <input class="blue-txt" type="text" name="interval" id="interval" value="">

            <div style="text-align: center">
                <input class="blue-btn" type="submit" value="Set Interval" style="width: 45%;">
            </div>
        </form>
    </div>
</div>
</body>
</html>