<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Policy Machine API</title>
    <link rel="stylesheet" href="./css/bootstrap.min.css">
    <style>
        button {
            margin-right: 10px;
        }
        .api-div {
            padding: 5px;
            border: solid .2em lightgrey;
        }
        ul {
            list-style: none;
        }
    </style>
</head>

<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <a class="navbar-brand" href="#">Policy Machine</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarColor01" aria-controls="navbarColor01" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarColor01">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item">
                <a class="nav-link" href="config.jsp">Server Configuration</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="userguide.jsp">User Guide</a>
            </li>
            <li class="nav-item active">
                <a class="nav-link" href="doc.jsp">Documentation</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="#">About</a>
            </li>
        </ul>
    </div>
</nav>

<div style="width: 60%">
    <h1>Policy Machine API</h1>
    <h4>Things to Know Before Starting</h4>
    <ul>
        <li>
            1. Every request (except the sessions endpoint) has a <i>session(string)</i> and <i>process(long)</i> query parameter.
            The <i>session</i> parameter is a session ID that is returned by creating a session using the <i>sessions</i> endpoint. (e.g. ...?session=SESSION_ID&process=PROCESS_ID)
        </li>
        <li>
            2. API Responses have three attributes: <strong>code</strong>, <strong>message</strong>, and <strong>entity</strong>:
            <ul>
                <li>
                    <strong>code: </strong>
                    <ul>
                        <li>9000 - Success</li>
                        <li>8000 - Error connecting to MySQL</li>
                        <li>7000 - Error connecting to Neo4j</li>
                        <li>6000 - Error with the request in terms of the Policy Machine (Node not found, Invalid node type, etc.)</li>

                    </ul>
                </li>
                <li>
                    <strong>message: </strong>If the request is successful the message will be 'Success'. If there is an error, the message will give a detailed explanation of the error that occurred.
                </li>
                <li>
                    <strong>entity: </strong>The entity that is returned as a result of the API request. (e.g.  A list of nodes)
                </li>
            </ul>
        </li>
        <li>
            3. <strong>Namespaces</strong> allow for a grouping of nodes so that nodes can share names as long a they are not in the same namespace.  Namespace is treated like a regular property of a node and if the namespace property is not set, then we consider this node to be in the 'default' namespace.
        </li>
    </ul>
</div>
<div id="nodes-card" class="card">
    <div class="card-header" onclick="show('nodes-api')" style="cursor: pointer">Nodes</div>
    <div id="nodes-api" class="card-body" style="display: none">
        <div id="/nodes">
            <h3>/nodes</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-success" onclick="showEndpoint('/nodes-GET')">GET</button>
                    <button class="btn btn-primary" onclick="showEndpoint('/nodes-POST')">POST</button>
                </div>
                <div id="/nodes-GET" style="display: none; padding: 15px">
                    <p>
                        Get nodes based on the provided search parameters
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">namespace</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">query</td>
                            <td style="max-width: 5px">no</td>
                            <td>The namespace value of the nodes</td>
                            <td>my_namespace</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">name</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">query</td>
                            <td style="max-width: 5px">no</td>
                            <td>The name of the nodes</td>
                            <td>MyNode</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">type</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">query</td>
                            <td style="max-width: 5px">no</td>
                            <td>The type of the nodes</td>
                            <td>OA</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">key</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">query</td>
                            <td style="max-width: 5px">no</td>
                            <td>A property key. <strong>Note:</strong> value must also be present to be considered in search</td>
                            <td>propKey</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">value</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">query</td>
                            <td style="max-width: 5px">no</td>
                            <td>A property value. <strong>Note:</strong> key must also be present to be considered in search</td>
                            <td>propValue</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                GET .. /pm/api/nodes?namespace=my_namespace&type=OA
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/nodes-GET-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {'code':9000,'message':'Success','entity':[
                                        {'id':1,'name':'node1','type':'OA','description':'','properties':[{'key':'namespace','value':'my_namespace','valid':true}]},
                                        {'id':2,'name':'node2','type':'OA','description':'','properties':[{'key':'namespace','value':'my_namespace','valid':true}]},
                                        {'id':3,'name':'node3','type':'OA','description':'','properties':[{'key':'namespace','value':'my_namespace','valid':true}]}]};
                                document.getElementById('/nodes-GET-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6006 - The key or the value parameters were null, causing an invalid Property</li>
                        <li>6008 - A node for the provided session ID was not found</li>
                        <li>6018 - The provided Node type was not one of (C, OA, UA, U, O, PC, D, OS)</li>
                        <li>6026 - The given session ID was not associated with a User</li>
                    </ul>
                </div>
                <div id="/nodes-POST" style="display: none; padding: 15px">
                    <p>
                        Create a new node
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">id</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">no</td>
                            <td>The ID of the new node.  If 0, a the node will be assigned an ID.</td>
                            <td>123</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">name</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The name of the new node</td>
                            <td>Node123</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">type</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The type of the new node</td>
                            <td>OA</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">description</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">no</td>
                            <td>The description of the node</td>
                            <td>This is a node</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">properties</td>
                            <td style="max-width: 5px">array</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">no</td>
                            <td>An array of properties for the new node</td>
                            <td>[{'prop1':'value1'},{'prop2':'value2'}]</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                POST .. /pm/api/nodes
                            </p>
                            <pre id="/nodes-POST-request" style="background-color: lightgrey; height: 162px"></pre>
                            <script>
                                var json = {'name':'Node123','type':'OA','description':'this is a description','properties':[{'key':'namespace','value':'my_namespace'}]};
                                document.getElementById('/nodes-POST-request').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/nodes-POST-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":{"id":57,"name":"Node123","type":"OA","description":"this is a description","properties":[{"key":"namespace","value":"my_namespace","valid":true}]}};
                                document.getElementById('/nodes-POST-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6011 - A name is required and cannot be null - There was no name provided</li>
                        <li>6021 - A node with the given name already exists in the given namespace - The provided node name already exists in the given namespace</li>
                        <li>6020 - A Node Type cannot be null - There was a null type provided</li>
                        <li>6006 - A property was malformed (Correct format is key=value) - A Property was provided but the format was invalid</li>
                        <li>7/8000- There was an error connecting to the database</li>
                        <li>6018 - The provided Node type was not one of (C, OA, UA, U, O, PC, D, OS)</li>
                        <li>6017 - A node with the given name does not exist in the given namespace - A node in the default namespace already has the given name</li>
                        <li>6014 - Error with Pm configuration - The PM is not connected to a database</li>
                        <li>6025 - A node already exists with the given ID</li>
                        <li>6008 - Node not found - If the connector node does not exist</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                        <li>6013 - No subject was provided - Both session and process IDs were not provided</li>
                        <li>6027 - The user is missing the correct permissions to perform this action - The user does not have the ability to create a node</li>
                        <li>6007 - The provided Subject Type was invalid - The session and/or process were not provided</li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="/nodes/{nodeId}">
            <h3>/nodes/{nodeId}</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-success" onclick="showEndpoint('/nodes/{nodeId}-GET')">GET</button>
                    <button class="btn btn-warning" onclick="showEndpoint('/nodes/{nodeId}-PUT')">PUT</button>
                    <button class="btn btn-danger" onclick="showEndpoint('/nodes/{nodeId}-DELETE')">DELETE</button>
                </div>
                <div id="/nodes/{nodeId}-GET" style="display: none; padding: 15px">
                    <p>
                        Get the node with the given ID
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">nodeId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">path</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the node</td>
                            <td>1234</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                GET .. /pm/api/nodes/1234
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/nodes/{nodeId}-GET-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":{"id":1234,"name":"Node123","type":"OA","description":"this is a description","properties":[{"key":"namespace","value":"my_namespace","valid":true}]}};
                                document.getElementById('/nodes/{nodeId}-GET-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6008 - Node not found</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                        <li>6013 - No subject was provided</li>
                        <li>6027 - The user is missing the correct permissions to perform this action</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                    </ul>
                </div>
                <div id="/nodes/{nodeId}-PUT" style="display: none; padding: 15px">
                    <p>
                        Update the node with the given ID
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">nodeId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">path</td>
                            <td style="max-width: 5px">yes</td>
                            <td>Te ID of the node</td>
                            <td>1234</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">name</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">no</td>
                            <td>The name of the new node</td>
                            <td>Node123</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">type</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">no</td>
                            <td>The type of the new node</td>
                            <td>OA</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">description</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">no</td>
                            <td>The description of the node</td>
                            <td>This is a node</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">properties</td>
                            <td style="max-width: 5px">array</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">no</td>
                            <td>An array of properties for the new node</td>
                            <td>[{'prop1':'value1'},{'prop2':'value2'}]</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                PUT .. /pm/api/nodes/1234
                            </p>
                            <pre id="/nodes/{nodeId}-PUT-request" style="background-color: lightgrey; height: 162px"></pre>
                            <script>
                                var json = {'description':'this is an UPDATED description'};
                                document.getElementById('/nodes/{nodeId}-PUT-request').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/nodes/{nodeId}-PUT-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":{"id":1234,"name":"Node123","type":"OA","description":"this is an UPDATED description","properties":[{"key":"namespace","value":"my_namespace","valid":true}]}};
                                document.getElementById('/nodes/{nodeId}-PUT-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6008 - Node not found</li>
                        <li>7/8000- There was an error connecting to the database</li>
                        <li>6014 - Error with Pm configuration</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                        <li>6013 - No subject was provided</li>
                        <li>6027 - The user is missing the correct permissions to perform this action</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                        <li>6006 - A property was malformed (Correct format is key=value)</li>
                        <li>6015 - A property with the given key, value pair was not found</li>
                    </ul>
                </div>
                <div id="/nodes/{nodeId}-DELETE" style="display: none; padding: 15px">
                    <p>
                        Delete the node with the given ID
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">nodeId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">path</td>
                            <td style="max-width: 5px">yes</td>
                            <td>Te ID of the node</td>
                            <td>1234</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <p>
                                DELETE .. /pm/api/nodes/1234
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/nodes/{nodeId}-DELETE-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":"Node successfully deleted"};
                                document.getElementById('/nodes/{nodeId}-DELETE-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6008 - Node not found</li>
                        <li>7/8000- There was an error connecting to the database</li>
                        <li>6014 - Error with Pm configuration</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                        <li>6013 - No subject was provided</li>
                        <li>6027 - The user is missing the correct permissions to perform this action</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="/nodes/{nodeId}/properties/{key}">
            <h3>/nodes/{nodeId}/properties/{key}</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-danger" onclick="showEndpoint('/nodes/{nodeId}/properties/{key}-DELETE')">DELETE</button>
                </div>
                <div id="/nodes/{nodeId}/properties/{key}-DELETE" style="display: none; padding: 15px">
                    <p>
                        Delete the property of the node with the given key
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="">nodeId</td>
                            <td style="">long</td>
                            <td style="">path</td>
                            <td style="">yes</td>
                            <td style="">The ID of the node to delete the property for</td>
                            <td style="">1234</td>
                        </tr>
                        <tr>
                            <td style="">key</td>
                            <td style="">string</td>
                            <td style="">path</td>
                            <td style="">yes</td>
                            <td style="">The key of the property to delete</td>
                            <td style="">propKey</td>
                        </tr>
                        </tbody>
                    </table><div class="row">
                    <div class="col-lg-6">
                        <p>
                            DELETE .. /nodes/1234/properties/propKey
                        </p>
                    </div>
                    <div class="col-lg-6">
                        <h5>Example Response</h5>
                        <pre id="/nodes/{nodeId}/properties/{key}-DELETE-response" style="background-color: lightgrey; height: 200px"></pre>
                        <script>
                            var json = {"code":9000,"message":"Success","entity":"The property was successfully deleted"};
                            document.getElementById('/nodes/{nodeId}/properties/{key}-DELETE-response').innerHTML = JSON.stringify(json, undefined, 2);
                        </script>
                    </div>
                </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>7/8000- There was an error connecting to the database</li>
                        <li>6008 - Node not found</li>
                        <li>6015 - A property with the given key, value pair was not found</li>
                        <li>6014 - Error with Pm configuration</li>
                        <li>6013 - No subject was provided</li>
                        <li>6027 - The user is missing the correct permissions to perform this action</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="/nodes/{nodeId}/children">
            <h3>/nodes/{nodeId}/children</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-success" onclick="showEndpoint('/nodes/{nodeId}/children-GET')">GET</button>
                    <button class="btn btn-danger" onclick="showEndpoint('/nodes/{nodeId}/children-DELETE')">DELETE</button>
                </div>
                <div id="/nodes/{nodeId}/children-GET" style="display: none; padding: 15px">
                    <p>
                        Get the nodes that are assigned to the node with the given ID
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="">nodeId</td>
                            <td style="">long</td>
                            <td style="">path</td>
                            <td style="">yes</td>
                            <td style="">The ID of the node to get the children for</td>
                            <td style="">1234</td>
                        </tr>
                        <tr>
                            <td style="">type</td>
                            <td style="">string</td>
                            <td style="">query</td>
                            <td style="">no</td>
                            <td style="">The type of the children to get</td>
                            <td style="">OA</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                GET .. /nodes/{nodeId}/children?type=OA
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/nodes/{nodeId}/children-GET-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":[
                                        {
                                            "id": 12345,
                                            "name": "nodeName",
                                            "type": "OA",
                                            "description": "node description"
                                        },
                                        {
                                            "id": 123456,
                                            "name": "nodeName123",
                                            "type": "OA",
                                            "description": "node description"
                                        }
                                    ]};
                                document.getElementById('/nodes/{nodeId}/children-GET-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6008 - Node not found</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                        <li>6009 - A user parameter was expected but none was received</li>
                    </ul>
                </div>
                <div id="/nodes/{nodeId}/children-DELETE" style="display: none; padding: 15px">
                    <p>
                        Delete the nodes that are assigned to the node with the given ID
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="">nodeId</td>
                            <td style="">long</td>
                            <td style="">path</td>
                            <td style="">yes</td>
                            <td style="">The ID of the node to delete the children for</td>
                            <td style="">1234</td>
                        </tr>
                        <tr>
                            <td style="">type</td>
                            <td style="">string</td>
                            <td style="">query</td>
                            <td style="">no</td>
                            <td style="">The type of the children to delete</td>
                            <td style="">OA</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                DELETE .. /nodes/{nodeId}/children?type=OA
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/nodes/{nodeId}/children-DELETE-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":"The children of the node were all deleted"};
                                document.getElementById('/nodes/{nodeId}/children-DELETE-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6018 - An invalid Node Type was recieved</li>
                        <li>6008 - Node not found</li>
                        <li>7/8000- There was an error connecting to the database</li>
                        <li>6014 - Error with Pm configuration</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                        <li>6013 - No subject was provided</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                        <li>6027 - The user is missing the correct permissions to perform this action</li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="/nodes/{nodeId}/parents">
            <h3>/nodes/{nodeId}/parents</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-success" onclick="showEndpoint('/nodes/{nodeId}/parents-GET')">GET</button>
                </div>
                <div id="/nodes/{nodeId}/parents-GET" style="display: none; padding: 15px">
                    <p>
                        Get nodes based on the provided search parameters
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">nodeId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">path</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the node to get the parents for</td>
                            <td>1234</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                GET .. /nodes/1234/parents
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/nodes/{nodeId}/parents-GET-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {'code':9000,'message':'Success','entity':[
                                        {'id':1,'name':'node1','type':'OA','description':'','properties':[{'key':'namespace','value':'my_namespace','valid':true}]},
                                        {'id':2,'name':'node2','type':'OA','description':'','properties':[{'key':'namespace','value':'my_namespace','valid':true}]},
                                        {'id':3,'name':'node3','type':'OA','description':'','properties':[{'key':'namespace','value':'my_namespace','valid':true}]}]};
                                document.getElementById('/nodes/{nodeId}/parents-GET-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6018 - An invalid Node Type was recieved</li>
                        <li>6008 - Node not found</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                        <li>6013 - No subject was provided</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                        <li>6027 - The user is missing the correct permissions to perform this action</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="assignments-card" class="card">
    <div class="card-header" onclick="show('assignments-api')" style="cursor: pointer">Assignments</div>
    <div id="assignments-api" class="card-body" style="display: none">
        <div id="/assignments">
            <h3>/assignments</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-success" onclick="showEndpoint('/assignments-GET')">GET</button>
                    <button class="btn btn-primary" onclick="showEndpoint('/assignments-POST')">POST</button>
                    <button class="btn btn-danger" onclick="showEndpoint('/assignments-DELETE')">DELETE</button>
                </div>
                <div id="/assignments-GET" style="display: none; padding: 15px">
                    <p>
                        Return true or false if the assignment exists between the childId and the parentId
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">childId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">query</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the child node</td>
                            <td>1234</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">parentId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">query</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the parent node</td>
                            <td>123</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                GET .. /pm/api/assignments?childId=1234&parentId=123
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/assignments-GET-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {'code':9000,'message':'Success','entity':true};
                                document.getElementById('/assignments-GET-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6008 - Node not found</li>
                    </ul>
                </div>
                <div id="/assignments-POST" style="display: none; padding: 15px">
                    <p>
                        Create a new assignment
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">childId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the child in the assignment</td>
                            <td>1234</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">parentId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the parent in the assignment</td>
                            <td>123</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                POST .. /pm/api/assignments
                            </p>
                            <pre id="/assignments-POST-request" style="background-color: lightgrey; height: 162px"></pre>
                            <script>
                                var json = {'childId':1234,'parentId':123};
                                document.getElementById('/assignments-POST-request').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/assignments-POST-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":"Assignment was successfully created"};
                                document.getElementById('/assignments-POST-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6008 - Node not found</li>
                        <li>6005 - An Assignment already exists between two nodes</li>
                        <li>6013 - No subject was provided</li>
                        <li>6027 - The user is missing the correct permissions to perform this action</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                        <li>7/8000- There was an error connecting to the database</li>
                        <li>6014 - Error with Pm configuration</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                    </ul>
                </div>
                <div id="/assignments-DELETE" style="display: none; padding: 15px">
                    <p>
                        Delete the assignment between the child and the parent
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">childId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">query</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the child in the assignment</td>
                            <td>1234</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">parentId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">query</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the parent in the assignment</td>
                            <td>123</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                DELETE .. /pm/api/assignments?childId=1234&parentId=123
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/assignments-DELETE-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":"Assignment was successfully deleted"};
                                document.getElementById('/assignments-DELETE-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6008 - Node not found</li>
                        <li>6001 - An Assignment does not exist</li>
                        <li>6014 - Error with Pm configuration</li>
                        <li>7/8000- There was an error connecting to the database</li>
                        <li>6013 - No subject was provided</li>
                        <li>6027 - The user is missing the correct permissions to perform this action</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="associations-card" class="card">
    <div class="card-header" onclick="show('associations-api')" style="cursor: pointer">Associations</div>
    <div id="associations-api" class="card-body" style="display: none">
        <div id="/associations">
            <h3>/associations</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-success" onclick="showEndpoint('/associations-GET')">GET</button>
                    <button class="btn btn-primary" onclick="showEndpoint('/associations-POST')">POST</button>
                </div>
                <div id="/associations-GET" style="display: none; padding: 15px">
                    <p>
                        Get a list of all the Associations
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">targetId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">query</td>
                            <td style="max-width: 5px">no</td>
                            <td>The ID of the Object Attribute to get the Associations for</td>
                            <td>123</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                GET .. /pm/api/associations?targetId=123
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/associations-GET-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":[{'uaId':'321','targetId':'123','ops':['read', 'write']},{'uaId':'4321','targetId':'123','ops':['read', 'write']}]};
                                document.getElementById('/associations-GET-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6008 - Node not found</li>
                        <li>6006 - A property was malformed (Correct format is key=value)</li>
                        <li>6013 - No subject was provided</li>
                        <li>6027 - The user is missing the correct permissions to perform this action</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                    </ul>
                </div>
                <div id="/associations-POST" style="display: none; padding: 15px">
                    <p>
                        Create a new Association
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">uaId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the User Attribute in the association</td>
                            <td>123</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">targetId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the Target of the association.  The target is an Object Attribute</td>
                            <td>321</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">ops</td>
                            <td style="max-width: 5px">array</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The operations of the association</td>
                            <td>["read", "write"]</td>
                        </tr>
                        <!--<tr>
                            <td style="min-width: 40px">inherit</td>
                            <td style="max-width: 5px">boolean</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">no</td>
                            <td>Whether or not the association is applied to all nodes beneath the target.</td>
                            <td>This is a node</td>
                        </tr>-->
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                POST .. /pm/api/associations
                            </p>
                            <pre id="/associations-POST-request" style="background-color: lightgrey; height: 162px"></pre>
                            <script>
                                var json = {'uaId':'123','targetId':'321','ops':['read', 'write']};
                                document.getElementById('/associations-POST-request').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/associations-POST-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":"Association was successfully created"};
                                document.getElementById('/associations-POST-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6008 - Node not found</li>
                        <li>6026 - An Association already exists between these two nodes</li>
                        <li>6014 - Error with Pm configuration</li>
                        <li>7/8000- There was an error connecting to the database</li>
                        <li>6013 - No subject was provided</li>
                        <li>6027 - The user is missing the correct permissions to perform this action</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="/associations/{targetId}">
            <h3>/associations/{targetId}</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-warning" onclick="showEndpoint('/associations/{targetId}-PUT')">PUT</button>
                </div>
                <div id="/associations/{targetId}-PUT" style="display: none; padding: 15px">
                    <p>
                        Update the operations of an Association between the targetId and the uaId.  The new associations will overwrite the existing ones.
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">targetId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">path</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the Target of the Association</td>
                            <td>321</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">uaId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the User Attribute in the Association</td>
                            <td>123</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">ops</td>
                            <td style="max-width: 5px">array</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The operations of the Association</td>
                            <td>["read", "write"]</td>
                        </tr>
                        <!--<tr>
                            <td style="min-width: 40px">inherit</td>
                            <td style="max-width: 5px">boolean</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">no</td>
                            <td>Whether or not the association is applied to all nodes beneath the target.</td>
                            <td>This is a node</td>
                        </tr>-->
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                PUT .. /pm/api/associations/321
                            </p>
                            <pre id="/associations/{targetId}-PUT-request" style="background-color: lightgrey; height: 162px"></pre>
                            <script>
                                var json = {'uaId':'123','targetId':'321','ops':['read', 'write', 'execute']};
                                document.getElementById('/associations/{targetId}-PUT-request').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/associations-PUT-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":"Successfully updated the association"};
                                document.getElementById('/associations-PUT-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6008 - Node not found</li>
                        <li>6010 - An Association does not exist between two nodes</li>
                        <li>6014 - Error with Pm configuration</li>
                        <li>7/8000- There was an error connecting to the database</li>
                        <li>6013 - No subject was provided</li>
                        <li>6027 - The user is missing the correct permissions to perform this action</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="/associations/{targetId}/subjects/{subjectId}">
            <h3>/associations/{targetId}/subjects/{subjectId}</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-danger" onclick="showEndpoint('/associations/{targetId}/subjects/{subjectId}-DELETE')">DELETE</button>
                </div>
                <div id="/associations/{targetId}/subjects/{subjectId}-DELETE" style="display: none; padding: 15px">
                    <p>
                        Delete the association between the Subject and the Target
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">targetId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">path</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the Target of the Association</td>
                            <td>321</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">subjectId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">path</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the User Attribute in the Association</td>
                            <td>123</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                DELETE .. /pm/api/associations/321/subjects/123
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/associations/{targetId}/subjects/{subjectId}-DELETE-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":"Successfully deleted Association"};
                                document.getElementById('/associations/{targetId}/subjects/{subjectId}-DELETE-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6008 - Node not found</li>
                        <li>6009 - A user parameter was expected but none was received</li>
                        <li>6010 - An Association does not exist between two nodes</li>
                        <li>6014 - Error with Pm configuration</li>
                        <li>7/8000- There was an error connecting to the database</li>
                        <li>6013 - No subject was provided</li>
                        <li>6027 - The user is missing the correct permissions to perform this action</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="/associations/subjects/{subjectId}">
            <h3>/associations/subjects/{subjectId}</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-success" onclick="showEndpoint('/associations/subjects/{subjectId}-GET')">GET</button>
                </div>
                <div id="/associations/subjects/{subjectId}-GET" style="display: none; padding: 15px">
                    <p>
                        Get the Associations that a User Attribute is the subject of
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">subjectId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">path</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the subject to get Associations for</td>
                            <td>123</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                GET .. /pm/api/associations/subjects/123
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/associations/subjects/{subjectId}-GET-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":[{'uaId':'123','targetId':'321','ops':['read', 'write']}]};
                                document.getElementById('/associations/subjects/{subjectId}-GET-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6008 - Node not found</li>
                        <li>6013 - No subject was provided</li>
                        <li>6027 - The user is missing the correct permissions to perform this action</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="prohibitions-card" class="card">
    <div class="card-header" onclick="show('prohibitions-api')" style="cursor: pointer">Prohibitions</div>
    <div id="prohibitions-api" class="card-body" style="display: none">
        <div id="prohibitions-model">
            <h5>Data Model</h5>
            Prohibitions deny a subject a set of operations on a list of resources.  The Prohibition Model consists of a name, a Subject, an array of Resources, an array of Operations, and a boolean value indicating if the prohibition is an intersection.
            <ul>
                <li><strong>Name:</strong> The name of the Prohibition.  This is just an identifier for the Prohibition.</li>
                <li><strong>Subject:</strong> The Subject is the entity that is being prohibited.  This can be a User, User Attribute, or Process.</li>
                <li><strong>Resources:</strong> A resource consists of a resourceId and a boolean value indicating if we should take the complement of the resource.</li>
                <li><strong>Operations:</strong> A list of the operations that are prohibited for a Subject on the Resources</li>
                <li><strong>Intersection:</strong> A boolean value.  If true, apply the Prohibition to objects that are in each of the resources.  Otherwise, apply the Prohibition to objects in any of the resources</li>
            </ul>
            <pre id="/prohibitions-model" style="background-color: lightgrey; height: 340px; width: 50%"></pre>
            <script>
                var json =
                    {
                        "name": "string",
                        "subject": {
                            "subjectId": "long",
                            "subjectType": "[UA, U, P]"
                        },
                        "resources": [
                            {
                                "resourceId": "long",
                                "complement": "boolean"
                            }
                        ],
                        "operations": [
                            "string"
                        ],
                        "intersection": "boolean"
                    };
                document.getElementById('/prohibitions-model').innerHTML = JSON.stringify(json, undefined, 2);
            </script>
        </div>
        <div id="/prohibitions">
            <h3>/prohibitions</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-success" onclick="showEndpoint('/prohibitions-GET')">GET</button>
                    <button class="btn btn-primary" onclick="showEndpoint('/prohibitions-POST')">POST</button>
                </div>
                <div id="/prohibitions-GET" style="display: none; padding: 15px">
                    <p>
                        Get a list of all Prohibitions
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">subjectId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">query</td>
                            <td style="max-width: 5px">no</td>
                            <td>The ID of the subject to get Prohibitions for</td>
                            <td>1234</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">resourceId</td>
                            <td style="max-width: 5px">long</td>
                            <td style="max-width: 5px">query</td>
                            <td style="max-width: 5px">no</td>
                            <td>The ID of the resource to get Prohibitions for</td>
                            <td>123</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                GET .. /pm/api/prohibitions?subjectId=1234&targetId=123
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/prohibitions-GET-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {'code':9000,'message':'Success','entity':
                                        {
                                            "name": "prohibition123",
                                            "subject": {
                                                "subjectId": "1234",
                                                "subjectType": "U"
                                            },
                                            "resources": [
                                                {
                                                    "resourceId": "123",
                                                    "complement": "false"
                                                },
                                                {
                                                    "resourceId": "567",
                                                    "complement": "true"
                                                }
                                            ],
                                            "operations": [
                                                "read",
                                                "write"
                                            ],
                                            "intersection": "true"
                                        }
                                };
                                document.getElementById('/prohibitions-GET-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <p>
                        N/A
                    </p>
                </div>
                <div id="/prohibitions-POST" style="display: none; padding: 15px">
                    <p>
                        Create a new Prohibition
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">name</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The name of the new Prohibition</td>
                            <td>prohibition123</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">subject</td>
                            <td style="max-width: 5px">Object</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The Subject of the new Prohibition</td>
                            <td>
                                <pre id="/prohibitions-POST-subject" style="background-color: lightgrey; height: 100px"></pre>
                                <script>
                                    var json = {
                                        "subject":{
                                            "subjectId":23,
                                            "subjectType":"UA"
                                        }
                                    };
                                    document.getElementById('/prohibitions-POST-subject').innerHTML = JSON.stringify(json, undefined, 2);
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">resources</td>
                            <td style="max-width: 5px">array</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>An Array of resources to apply the Prohibition to</td>
                            <td>
                                <pre id="/prohibitions-POST-resources" style="background-color: lightgrey; height: 100px"></pre>
                                <script>
                                    var json = {
                                        "resources":[
                                            {
                                                "resourceId":15,
                                                "complement":true
                                            },
                                            {
                                                "resourceId":352555,
                                                "complement":false
                                            }
                                        ]
                                    };
                                    document.getElementById('/prohibitions-POST-resources').innerHTML = JSON.stringify(json, undefined, 2);
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">operations</td>
                            <td style="max-width: 5px">array</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>An array of operations</td>
                            <td>["read","write"]</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">intersection</td>
                            <td style="max-width: 5px">boolean</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>Boolean indicating whether or not to take the intersection of the resources</td>
                            <td>true</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                POST .. /pm/api/prohibitions
                            </p>
                            <pre id="/prohibitions-POST-request" style="background-color: lightgrey; height: 162px"></pre>
                            <script>
                                var json = {
                                    "name":"prohibition123",
                                    "subject":{
                                        "subjectId":23,
                                        "subjectType":"UA"
                                    },
                                    "resources":[
                                        {
                                            "resourceId":15,
                                            "complement":true
                                        },
                                        {
                                            "resourceId":352555,
                                            "complement":false
                                        }
                                    ],
                                    "operations":[
                                        "read",
                                        "write"
                                    ],
                                    "intersection":true
                                };
                                document.getElementById('/prohibitions-POST-request').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/prohibitions-POST-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":{
                                        "name":"prohibition123",
                                        "subject":{
                                            "subjectId":23,
                                            "subjectType":"UA"
                                        },
                                        "resources":[
                                            {
                                                "resourceId":15,
                                                "complement":true
                                            },
                                            {
                                                "resourceId":352555,
                                                "complement":false
                                            }
                                        ],
                                        "operations":[
                                            "read",
                                            "write"
                                        ],
                                        "intersection":true
                                    }};
                                document.getElementById('/prohibitions-POST-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6007 - The provided Subject Type was invalid</li>
                        <li>6004 - The provided name already exists for a Prohibition</li>
                        <li>6022 - The given prohibition does not exist</li>
                        <li>6008 - Node not found</li>
                        <li>7/8000- There was an error connecting to the database</li>
                        <li>6003 - The resource already exists in the Prohibition</li>
                        <li>6014 - Error with Pm configuration</li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="/prohibitions/{prohibitionName}">
            <h3>/prohibitions/{prohibitionName}</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-success" onclick="showEndpoint('/prohibitions/{prohibitionName}-GET')">GET</button>
                    <button class="btn btn-primary" onclick="showEndpoint('/prohibitions/{prohibitionName}-PUT')">PUT</button>
                    <button class="btn btn-danger" onclick="showEndpoint('/prohibitions/{prohibitionName}-DELETE')">DELETE</button>
                </div>
                <div id="/prohibitions/{prohibitionName}-GET" style="display: none; padding: 15px">
                    <p>
                        Get the Prohibition with the given name
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">prohibitionName</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">path</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The name of the Prohibition to return</td>
                            <td>prohibition123</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                GET .. /pm/api/prohibitions/prohibition123
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/prohibitions/{prohibitionName}-GET-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {'code':9000,'message':'Success','entity':
                                        {
                                            "name": "prohibition123",
                                            "subject": {
                                                "subjectId": "1234",
                                                "subjectType": "U"
                                            },
                                            "resources": [
                                                {
                                                    "resourceId": "123",
                                                    "complement": "false"
                                                },
                                                {
                                                    "resourceId": "567",
                                                    "complement": "true"
                                                }
                                            ],
                                            "operations": [
                                                "read",
                                                "write"
                                            ],
                                            "intersection": "true"
                                        }
                                };
                                document.getElementById('/prohibitions/{prohibitionName}-GET-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6022 - The given prohibition does not exist</li>
                    </ul>
                </div>
                <div id="/prohibitions/{prohibitionName}-PUT" style="display: none; padding: 15px">
                    <p>
                        Update the Prohibition with the given name
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">prohibitionName</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">path</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The name of the Prohibition to update</td>
                            <td>prohibition123</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">name</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">no</td>
                            <td>The new name to give the Prohibition</td>
                            <td>prohibitionNEW</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">subject</td>
                            <td style="max-width: 5px">Object</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">no</td>
                            <td>The new Subject of the Prohibition</td>
                            <td>
                                <pre id="/prohibitions/{prohibitionName}-PUT-subject" style="background-color: lightgrey; height: 100px"></pre>
                                <script>
                                    var json = {
                                        "subject":{
                                            "subjectId":77,
                                            "subjectType":"UA"
                                        }
                                    };
                                    document.getElementById('/prohibitions/{prohibitionName}-PUT-subject').innerHTML = JSON.stringify(json, undefined, 2);
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">resources</td>
                            <td style="max-width: 5px">array</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>An Array of resources to apply the Prohibition to</td>
                            <td>
                                <pre id="/prohibitions/{prohibitionName}-PUT-resources" style="background-color: lightgrey; height: 100px"></pre>
                                <script>
                                    var json = {
                                        "resources":[
                                            {
                                                "resourceId":15,
                                                "complement":true
                                            },
                                            {
                                                "resourceId":352555,
                                                "complement":false
                                            },
                                            {
                                                "resourceId":321,
                                                "complement":false
                                            }
                                        ]
                                    };
                                    document.getElementById('/prohibitions/{prohibitionName}-PUT-resources').innerHTML = JSON.stringify(json, undefined, 2);
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">operations</td>
                            <td style="max-width: 5px">array</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>An array of operations</td>
                            <td>["read"]</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">intersection</td>
                            <td style="max-width: 5px">boolean</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>Boolean indicating whether or not to take the intersection of the resources</td>
                            <td>false</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                PUT .. /pm/api/prohibitions/prohibition123
                            </p>
                            <pre id="/prohibitions/{prohibitionName}-PUT-request" style="background-color: lightgrey; height: 162px"></pre>
                            <script>
                                var json = {
                                    "name":"prohibitionNEW",
                                    "subject":{
                                        "subjectId":77,
                                        "subjectType":"UA"
                                    },
                                    "resources":[
                                        {
                                            "resourceId":15,
                                            "complement":true
                                        },
                                        {
                                            "resourceId":352555,
                                            "complement":false
                                        },
                                        {
                                            "resourceId":321,
                                            "complement":false
                                        }
                                    ],
                                    "operations":[
                                        "read"
                                    ],
                                    "intersection":false
                                };
                                document.getElementById('/prohibitions/{prohibitionName}-PUT-request').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/prohibitions/{prohibitionName}-PUT-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":{
                                        "name":"prohibitionNEW",
                                        "subject":{
                                            "subjectId":77,
                                            "subjectType":"UA"
                                        },
                                        "resources":[
                                            {
                                                "resourceId":15,
                                                "complement":true
                                            },
                                            {
                                                "resourceId":352555,
                                                "complement":false
                                            },
                                            {
                                                "resourceId":321,
                                                "complement":false
                                            }
                                        ],
                                        "operations":[
                                            "read"
                                        ],
                                        "intersection":false
                                    }};
                                document.getElementById('/prohibitions/{prohibitionName}-POST-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>7/8000- There was an error connecting to the database</li>
                        <li>6022 - The given prohibition does not exist</li>
                        <li>6012 - The provided resource does not exist in the Prohibition</li>
                        <li>6008 - Node not found</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                        <li>6003 - The resource already exists in the Prohibition</li>
                        <li>6014 - Error with Pm configuration</li>
                    </ul>
                </div>
                <div id="/prohibitions/{prohibitionName}-DELETE" style="display: none; padding: 15px">
                    <p>
                        Delete the Prohibition with the given name
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">prohibitionName</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">path</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The name of the Prohibition to delete</td>
                            <td>prohibition123</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                DELETE .. /pm/api/prohibitions/prohibition123
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/prohibitions/{prohibitionName}-DELETE-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {"code":9000,"message":"Success","entity":"Prohibition was deleted successfully"};
                                document.getElementById('/prohibitions/{prohibitionName}-DELETE-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>7/8000- There was an error connecting to the database</li>
                        <li>6022 - The given prohibition does not exist</li>
                        <li>6014 - Error with Pm configuration</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="analytics-card" class="card">
    <div class="card-header" onclick="show('analytics-api')" style="cursor: pointer">Analytics</div>
    <div id="analytics-api" class="card-body" style="display: none">
        <div id="/analytics/{var1:target}/users/permissions">
            <h3>analytics/{var1:target}/users/permissions</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-success" onclick="showEndpoint('/{var1:target}/users/permissions-GET')">GET</button>
                </div>
                <div id="/{var1:target}/users/permissions-GET" style="display: none; padding: 15px">
                    <p>
                        Get all of the users that have access to the target node, and the permissions each user has. If the permissions query parameter is present, filter any users that dont have the provided permissions
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">name</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">matrix</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The name of the target node</td>
                            <td>/target;name=object1/</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">type</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">matrix</td>
                            <td style="max-width: 5px">no</td>
                            <td>The type of the target node</td>
                            <td>/target;name=object1;type=O/</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">properties</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">matrix</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The properties of the target node</td>
                            <td>/target;name=object1;type=O;properties=prop1=value1,prop2=value2/</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">permissions</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">query</td>
                            <td style="max-width: 5px">no</td>
                            <td>Filter any users that don't have the permissions provided</td>
                            <td>read, write</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                GET .. /pm/api/analytics/target;name=object1;type=O;properties=prop1=value1,prop2=value2/users/permissions?permissions=read,write
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/{var1:target}/users/permissions-GET-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {'code':9000,'message':'Success','entity':
                                        {
                                            "target": {
                                                "id": 1234,
                                                "name": "user1",
                                                "type": "U"
                                            },
                                            "operations":[
                                                "read",
                                                "write"
                                            ]
                                        }
                                };
                                document.getElementById('/{var1:target}/users/permissions-GET-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6018 - An invalid Node Type was recieved</li>
                        <li>6006 - A property was malformed (Correct format is key=value)</li>
                        <li>6029 - The returned number of nodes was un expected.  Most likely, expexted one but was multiple or none</li>
                        <li>6008 - Node not found</li>
                        <li>6014 - Error with Pm configuration</li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="/analytics/{var1:target}/users/{username}/permissions">
            <h3>/analytics/{var1:target}/users/{username}/permissions</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-success" onclick="showEndpoint('/analytics/{var1:target}/users/{username}/permissions-GET')">GET</button>
                </div>
                <div id="/analytics/{var1:target}/users/{username}/permissions-GET" style="display: none; padding: 15px">
                    <p>
                        Get the permissions a user has on a target node.
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">name</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">matrix</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The name of the target node</td>
                            <td>/target;name=object1/</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">type</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">matrix</td>
                            <td style="max-width: 5px">no</td>
                            <td>The type of the target node</td>
                            <td>/target;name=object1;type=O/</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">properties</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">matrix</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The properties of the target node</td>
                            <td>/target;name=object1;type=O;properties=prop1=value1,prop2=value2/</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">username</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">path</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The name of the user</td>
                            <td>user1</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <p>
                                GET .. /pm/api/analytics/target;name=object1;type=O;properties=prop1=value1,prop2=value2/users/user1/permissions
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/analytics/{var1:target}/users/{username}/permissions-GET-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {'code':9000,'message':'Success','entity':
                                        [
                                            "read",
                                            "write"
                                        ]
                                };
                                document.getElementById('/analytics/{var1:target}/users/{username}/permissions-GET-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6018 - An invalid Node Type was recieved</li>
                        <li>6006 - A property was malformed (Correct format is key=value)</li>
                        <li>6029 - The returned number of nodes was un expected.  Most likely, expexted one but was multiple or none</li>
                        <li>6008 - Node not found</li>
                        <li>6014 - Error with Pm configuration</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                        <li>6013 - No subject was provided</li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="/analytics/{var1:target}/users/{username}">
            <h3>/analytics/{var1:target}/users/{username}</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-success" onclick="showEndpoint('/analytics/{var1:target}/users/{username}-GET')">GET</button>
                </div>
                <div id="/analytics/{var1:target}/users/{username}-GET" style="display: none; padding: 15px">
                    <p>
                        Check if a user has the permissions provided on a target node. If no permissions are given, then check if the user has any permissions on the target.
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">name</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">matrix</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The name of the target node</td>
                            <td>/target;name=object1/</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">type</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">matrix</td>
                            <td style="max-width: 5px">no</td>
                            <td>The type of the target node</td>
                            <td>/target;name=object1;type=O/</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">properties</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">matrix</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The properties of the target node</td>
                            <td>/target;name=object1;type=O;properties=prop1=value1,prop2=value2/</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">username</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">path</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The name of the user</td>
                            <td>user1</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">permissions</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">query</td>
                            <td style="max-width: 5px">no</td>
                            <td>The permissions to check if the user has on the target</td>
                            <td>read, write</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <p>
                                GET .. /pm/api/analytics/target;name=object1;type=O;properties=prop1=value1,prop2=value2/users/user1?permissions=read,write
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/analytics/{var1:target}/users/{username}-GET-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {'code':9000,'message':'Success','entity': true};
                                document.getElementById('/analytics/{var1:target}/users/{username}-GET-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6018 - An invalid Node Type was recieved</li>
                        <li>6006 - A property was malformed (Correct format is key=value)</li>
                        <li>6029 - The returned number of nodes was un expected.  Most likely, expexted one but was multiple or none</li>
                        <li>6008 - Node not found</li>
                        <li>6014 - Error with Pm configuration</li>
                        <li>6007 - The provided Subject Type was invalid</li>
                        <li>6013 - No subject was provided</li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="/analytics/{username}/targets/permissions">
            <h3>/analytics/{username}/targets/permissions</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-success" onclick="showEndpoint('/analytics/{username}/targets/permissions-GET')">GET</button>
                </div>
                <div id="/analytics/{username}/targets/permissions-GET" style="display: none; padding: 15px">
                    <p>
                        Get all of the nodes that a given user has access to and the permissions they have on each node. Optionally, if permissions are provided aa a query parameter, this method will return the list of nodes that the user has the specified permissions on.
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">username</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">path</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The name of the user</td>
                            <td>user1</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">permissions</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">query</td>
                            <td style="max-width: 5px">no</td>
                            <td>The permissions to check if the user has on the targets</td>
                            <td>read, write</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <p>
                                GET .. /pm/api/analytics/user1/targets/permissions?permissions=read, write
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/analytics/{username}/targets/permissions-GET-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {'code':9000,'message':'Success','entity':
                                        {
                                            "target": {
                                                "id": 1234,
                                                "name": "object1",
                                                "type": "o"
                                            },
                                            "operations":[
                                                "read",
                                                "write",
                                                "execute"
                                            ]
                                        }
                                };
                                document.getElementById('/analytics/{username}/targets/permissions-GET-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6018 - An invalid Node Type was recieved</li>
                        <li>6006 - A property was malformed (Correct format is key=value)</li>
                        <li>6029 - The returned number of nodes was un expected.  Most likely, expexted one but was multiple or none</li>
                        <li>6008 - Node not found</li>
                        <li>6009 - A user parameter was expected but none was received</li>
                        <li>6014 - Error with Pm configuration</li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="/analytics/sessions">
            <h3>/analytics/sessions</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-success" onclick="showEndpoint('/analytics/sessions-GET')">GET</button>
                </div>
                <div id="/analytics/sessions-GET" style="display: none; padding: 15px">
                    <p>
                        Get all of the nodes that the current session has access to, and the analytics on each node
                    </p>
                    <h5>Parameters</h5>
                    *The only parameter is the Session ID described above.

                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                GET .. /pm/api/analytics/sessions?session=SESSION_ID
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/analytics/sessions-GET-response" style="background-color: lightgrey; height: 200px;"></pre>
                            <script>
                                var json = {'code':9000,'message':'Success','entity':
                                        [
                                            {
                                                "target": {
                                                    "id": 124,
                                                    "name": "nodeName",
                                                    "type": "OA",
                                                    "description": ""
                                                },
                                                "operations":[
                                                    "read",
                                                    "write"
                                                ]
                                            },
                                            {
                                                "target": {
                                                    "id": 125,
                                                    "name": "nodeName2",
                                                    "type": "OA",
                                                    "description": ""
                                                },
                                                "operations":[
                                                    "read"
                                                ]
                                            }
                                        ]
                                };
                                document.getElementById('/analytics/sessions-GET-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6008 - Node not found</li>
                        <li>6009 - A user parameter was expected but none was received</li>
                        <li>6028 - The given session ID was not associated with a User</li>
                        <li>6014 - Error with Pm configuration</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="sessions-card" class="card">
    <div class="card-header" onclick="show('sessions-api')" style="cursor: pointer">Sessions</div>
    <div id="sessions-api" class="card-body" style="display: none">
        Sessions are required to use the Policy Machine APIs.  Create a User using the nodes endpoint and include a password in the properties.
        <div id="/sessions">
            <h3>/sessions</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-primary" onclick="showEndpoint('/sessions-POST')">POST</button>
                </div>
                <div id="/sessions-POST" style="display: none; padding: 15px">
                    <p>
                        Create a new session after authenticating the given username and password
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">username</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The username of the user creating a session</td>
                            <td>bob</td>
                        </tr>
                        <tr>
                            <td style="min-width: 40px">password</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">body</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The password of the user creating a session</td>
                            <td>password</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <h5>Example Request</h5>
                            <p>
                                POST .. /pm/api/sessions
                            </p>
                            <pre id="/sessions-POST-request" style="background-color: lightgrey; height: 162px"></pre>
                            <script>
                                var json = {
                                    "username": "bob",
                                    "password": "password"
                                };
                                document.getElementById('/sessions-POST-request').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/sessions-POST-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {'code':9000,'message':'Success','entity':"3D9BB9C8D7A747698C6BB46EDCD3C969"};
                                document.getElementById('/sessions-POST-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6011 - A name is required and cannot be null</li>
                        <li>6021 - A node with the given name already exists in the given namespace</li>
                        <li>6017 - A node with the given name does not exist in the given namespace</li>
                        <li>6008 - Node not found</li>
                        <li>7/8000- There was an error connecting to the database</li>
                        <li>6018 - An invalid Node Type was recieved</li>
                        <li>6006 - A property was malformed (Correct format is key=value)</li>
                        <li>6014 - Error with Pm configuration</li>
                        <li>6020 - A Node Type cannot be null</li>
                        <li>6025 - A node already exists with the given ID</li>
                        <li>6005 - An Assignment already exists between two nodes</li>
                        <li>6024 - The user does not have permission to perform this action</li>
                        <li>6015 - A property with the given key, value pair was not found</li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="/sessions/{sessionId}">
            <h3>/sessions/{sessionId}</h3>
            <div>
                <div class="row" style="padding: 0 20px; margin-bottom: 5px">
                    <button class="btn btn-danger" onclick="showEndpoint('/sessions/{sessionId}-DELETE')">DELETE</button>
                </div>
                <div id="/sessions/{sessionId}-DELETE" style="display: none; padding: 15px">
                    <p>
                        Delete the session with the given ID
                    </p>
                    <h5>Parameters</h5>
                    <table class="table table-hover table-sm">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Location</th>
                            <th>Required</th>
                            <th>Description</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td style="min-width: 40px">session</td>
                            <td style="max-width: 5px">string</td>
                            <td style="max-width: 5px">path</td>
                            <td style="max-width: 5px">yes</td>
                            <td>The ID of the session to delete</td>
                            <td>3D9BB9C8D7A747698C6BB46EDCD3C969</td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-lg-6">
                            <p>
                                DELETE .. /pm/api/sessions/3D9BB9C8D7A747698C6BB46EDCD3C969
                            </p>
                        </div>
                        <div class="col-lg-6">
                            <h5>Example Response</h5>
                            <pre id="/sessions/{sessionId}-DELETE-response" style="background-color: lightgrey; height: 200px"></pre>
                            <script>
                                var json = {'code':9000,'message':'Success','entity':"Session was deleted"};
                                document.getElementById('/sessions/{sessionId}-DELETE-response').innerHTML = JSON.stringify(json, undefined, 2);
                            </script>
                        </div>
                    </div>
                    <h5>Response Error Codes</h5>
                    <ul>
                        <li>6008 - Node not found</li>
                        <li>6006 - A property was malformed (Correct format is key=value)</li>
                        <li>6018 - An invalid Node Type was recieved</li>
                        <li>7/8000- There was an error connecting to the database</li>
                        <li>6014 - Error with Pm configuration</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    curId = '';
    function show(id){
        element = document.getElementById(id);
        if (element.style.display === "none") {
            element.style.display = "block";
        } else {
            element.style.display = "none";
        }
        curId = id;
    }
    curEndpointId = '';
    function showEndpoint(id){
        if(curEndpointId !== id) {
            var element = document.getElementById(curEndpointId);
            if (element) {
                element.style.display = 'none';
            }
        }
        element = document.getElementById(id);
        if (element.style.display === "none") {
            element.style.display = "block";
        } else {
            element.style.display = "none";
        }
        curEndpointId = id;
    }

    $.each($('.displayWrapper').children(), function(idx, child) {
        $(child).text(json);
    });
</script>
</body>
</html>
