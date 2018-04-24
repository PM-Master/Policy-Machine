<html>
<head>
    <title>Home</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="./css/bootstrap.min.css">
    <link rel="stylesheet" href="./css/theme.css">
    <style>

    </style>

    <script type="text/javascript">
        function showForm(name) {
            document.getElementById(name + 'Form').style.display = 'block';

            switch (name) {
                case 'neo4j':
                    hideForm('mysql');
                    hideForm('reset');
                    hideForm('load');
                    hideForm('save');
                    hideForm('interval');
                    break;
                case 'mysql':
                    hideForm('neo4j');
                    hideForm('reset');
                    hideForm('load');
                    hideForm('save');
                    hideForm('interval');
                    break;
                case 'reset':
                    hideForm('neo4j');
                    hideForm('mysql');
                    hideForm('interval');
                    hideForm('save');
                    hideForm('load');
                    break;
                case 'interval':
                    hideForm('neo4j');
                    hideForm('mysql');
                    hideForm('reset');
                    hideForm('load');
                    hideForm('save');
                    break;
                case 'save':
                    hideForm('neo4j');
                    hideForm('mysql');
                    hideForm('reset');
                    hideForm('interval');
                    hideForm('load');
                    break;
                case 'load':
                    hideForm('neo4j');
                    hideForm('mysql');
                    hideForm('reset');
                    hideForm('interval');
                    hideForm('save');
                    break;
            }
        }

        function hideForm(name) {
            document.getElementById(name + 'Form').style.display = 'none';
        }

        function message() {

        }
    </script>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <a class="navbar-brand" href="#">Policy Machine</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarColor01" aria-controls="navbarColor01" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarColor01">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item active">
                <a class="nav-link" href="config.jsp">Server Configuration</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="userguide.jsp">User Guide</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="doc.jsp">Documentation</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="#">About</a>
            </li>
        </ul>
    </div>
</nav>
<div id="messageDiv" style="display: <%= request.getParameter("display") != null ? request.getParameter("display") : "none" %>">
    <div class="alert alert-dismissible alert-<%= request.getParameter("result") %>">
        <button type="button" class="close" data-dismiss="alert" onclick="document.getElementById('messageDiv').style.display='none'">&times;</button>
        <span id="message"><%= request.getParameter("message") %></span>
    </div>
</div>

<div style="width: 70%; margin: 2% 15%; background-color: white; padding: 10px">
    <div class="row" style="margin: 0">
        <div id="neo4j" class="col col-lg-6 tile" style="padding: 10px 10px 10px 10px;">
            <div class="card text-white bg-primary" style="padding: 0;">
                <!--<div class="card-header" onclick="showIcon('neo4j')">Connect</div>-->
                <div id="neo4jIcon" class="card-body icon-panel" style="text-align: center" onclick="showForm('neo4j')">
                    <h4 class="card-title">Connect to Neo4j</h4>
                    <img src="images/graph.png" alt="graph" width="150" height="150">
                </div>
            </div>
        </div>
        <div id="mysql" class="col col-lg-6 tile" style="padding: 10px 10px 10px 10px;">
            <div class="card text-white bg-primary" style="padding: 0;">
                <!--<div class="card-header" onclick="showIcon('neo4j')">Connect</div>-->
                <div id="mysqlIcon" class="card-body icon-panel" style="text-align: center" onclick="showForm('mysql')">
                    <h4 class="card-title">Connect to MySQL</h4>
                    <img src="images/database.png" alt="mysql" width="150" height="150">
                </div>
            </div>
        </div>
    </div>
    <div id="neo4jForm" class="col-lg-12 card-body" style="display: none; background-color: white; height: inherit; color: #008cba">
        <button type="button" class="close" onclick="hideForm('neo4j')" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
        <form action="SetConnection" method="post">
            <input type="hidden" name="database" value="neo4j">
            <fieldset>
                <legend>Connect to Neo4j</legend>
                <div class="row">
                    <div class="form-group col-lg-6">
                        <label>Host</label>
                        <input class="form-control" name="host" placeholder="Host" type="text" value="">
                    </div>
                    <div class="form-group col-lg-6">
                        <label>Port</label>
                        <input class="form-control" name="port" placeholder="Port" type="text" value="">
                    </div>
                </div>
                <div class="row">
                    <div class="form-group col-lg-6">
                        <label>Username</label>
                        <input class="form-control" name="username" placeholder="Username" type="text" value="">
                    </div>
                    <div class="form-group col-lg-6" style="margin: 0">
                        <label>Password</label>
                        <input class="form-control" name="password" placeholder="Password" type="text" value="">
                    </div>
                </div>
                <div class="row">
                    <div class="form-group col-lg-3">
                        <button type="submit" class="btn btn-primary">Submit</button>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
    <div id="mysqlForm" class="col-lg-12 card-body" style="display: none; background-color: white; height: inherit; color: #008cba">
        <button type="button" class="close" onclick="hideForm('mysql')" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
        <form action="SetConnection" method="post">
            <input type="hidden" name="database" value="mysql">
            <fieldset>
                <legend>Connect to MySQL</legend>
                <div class="row">
                    <div class="form-group col-lg-6">
                        <label>Host</label>
                        <input class="form-control" name="host" placeholder="Host" type="text">
                    </div>
                    <div class="form-group col-lg-6">
                        <label>Port</label>
                        <input class="form-control" name="port" placeholder="Port" type="text">
                    </div>
                </div>
                <div class="row">
                    <div class="form-group col-lg-6">
                        <label>Username</label>
                        <input class="form-control" name="username" placeholder="Username" type="text">
                    </div>
                    <div class="form-group col-lg-6">
                        <label>Password</label>
                        <input class="form-control" name="password" placeholder="Password" type="text">
                    </div>
                </div>
                <div class="row">
                    <div class="form-group col-lg-12" style="margin: 0">
                        <label>Database</label>
                        <input class="form-control" name="schema" placeholder="Database" type="text">
                    </div>
                </div>
                <div class="row">
                    <div class="form-group col-lg-3">
                        <button type="submit" class="btn btn-primary">Submit</button>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
    <div class="row" style="margin: 0">
        <div class=" col-lg-3 tile" style="padding: 10px 10px 10px 10px;">
            <div class="card text-white bg-primary" style="padding: 0">
                <div class="card-body" style="text-align: center" onclick="showForm('reset')">
                    <h5 class="card-title">Reset Data</h5>
                    <img src="images/reset.png" alt="reset" width="150" height="150">
                </div>
            </div>
        </div>
        <div class=" col-lg-3 tile" style="padding: 10px 10px 10px 10px;">
            <div class="card text-white bg-primary" style="padding: 0">
                <div class="card-body" style="text-align: center" onclick="showForm('interval')">
                    <h5 class="card-title">Set Data Dump Interval</h5>
                    <img src="images/interval.png" alt="interval" width="150" height="150">
                </div>
            </div>
        </div>
        <div class=" col-lg-3 tile" style="padding: 10px 10px 10px;">
            <div class="card text-white bg-primary" style="padding: 0">
                <div class="card-body" style="text-align: center" onclick="showForm('save')">
                    <h5 class="card-title">Save Configuration</h5>
                    <img src="images/save.png" alt="save" width="150" height="150">
                </div>
            </div>
        </div>
        <div class=" col-lg-3 tile" style="padding: 10px 10px 10px 10px;">
            <div class="card text-white bg-primary" style="padding: 0">
                <div class="card-body" style="text-align: center" onclick="showForm('load')">
                    <h5 class="card-title">Load Configuration</h5>
                    <img src="images/load.png" alt="load" width="150" height="150">
                </div>
            </div>
        </div>
    </div>
    <div id="resetForm" class="col-lg-12 card-body" style="display: none; background-color: white; height: inherit; color: #008cba">
        <button type="button" class="close" onclick="hideForm('reset')" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
        <form action="Reset" method="post">
            <fieldset>
                <legend>Reset Policy Machine Data</legend>
                <p class="text-danger">This will delete all current data in the Policy Machine.</p>
                <div class="row">
                    <div class="form-group col-lg-3">
                        <button type="submit" class="btn btn-primary">Reset</button>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
    <div id="intervalForm" class="col-lg-12 card-body" style="display: none; background-color: white; height: inherit; color: #008cba">
        <button type="button" class="close" onclick="hideForm('interval')" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
        <form action="SetInterval" method="post">
            <fieldset>
                <legend>Set Data Dump Interval</legend>
                <div class="row">
                    <div class="form-group col-lg-6">
                        <input class="form-control" name="interval" placeholder="Interval" type="text">
                    </div>
                </div>
                <div class="row">
                    <div class="form-group col-lg-3">
                        <button type="submit" class="btn btn-primary">Submit</button>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
    <div id="saveForm" class="col-lg-12 card-body" style="display: none; background-color: white; height: inherit; color: #008cba">
        <button type="button" class="close" onclick="hideForm('save')" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
        <form action="save" method="post">
            <fieldset>
                <legend>Save Configuration</legend>
                <div class="row">
                    <div class="form-group col-lg-6">
                        <input class="form-control" name="configName" placeholder="Configuration Name" type="text">
                    </div>
                </div>
                <div class="row">
                    <div class="form-group col-lg-3">
                        <button type="submit" class="btn btn-primary">Submit</button>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
    <div id="loadForm" class="col-lg-12 card-body" style="display: none; background-color: white; height: inherit; color: #008cba">
        <button type="button" class="close" onclick="hideForm('load')" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
        <form  enctype="multipart/form-data" action="load" method="post">
            <fieldset>
                <legend>Load Configuration</legend>
                <ul style="list-style: decimal">
                    <li class="text-muted">
                        Make sure the super.pm configuration is already loaded.
                    </li>
                    <li class="text-muted">
                        This may take a few minutes depending on the size of the configuration.
                    </li>
                </ul>
                <div class="row">
                    <div class="form-group col-lg-6">
                        <input type="file" name="configFile" accept=".pm">
                    </div>
                </div>
                <div class="row">
                    <div class="form-group col-lg-3">
                        <button type="submit" class="btn btn-primary">Submit</button>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
</div>
</body>
</html>