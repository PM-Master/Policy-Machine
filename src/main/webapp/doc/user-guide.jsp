<html>
<body>
<head>
	<title>PM User Guide</title>
	<link rel="stylesheet" href="./css/theme.css">
	<link rel="stylesheet" href="./css/nav.css">
	<style type="text/css">
		h1, h3, h5 {
			font-family: "Times New Roman", Times, serif;
			color: #2196F3;
		}

		body {
			font-family: "Times New Roman", Times, serif;
			background: lightgrey;
		}

		dt {
			font-weight: bold;
			padding-left: 30px;
			font-family: "Times New Roman", Times, serif;
			color: #2196F3;
		}

		dd {
			padding-left: 30px;
		}

		.doc-div {
			margin-left: 14%;
			margin-right: 14%;
			margin-bottom: 3%;
			background: white;
		}

		.card {
			box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);
			transition: 0.3s;
			padding: 5px;
			border-radius: 5px 5px 5px 5px;
		}

	</style>
</head>
<div class="doc-div card">
	<h1 align="center">Policy Machine Overview</h1>
	<p>Click <a href="./doc/PMUserGuide.pdf" target="_blank">here</a> for the Policy Machine User Guide</p>
</div>

<div class="doc-div card">
	<h1 align="center">Policy Machine Entities</h1>
	<h3>Nodes</h3>
	<dl style="list-style-type: none">
		<dt>Policy Class</dt>
		<br>
		<dd>A Policy Class node is the base node for any policy.  For example, the Role Based Access Control policy will have a Policy Class node called RBAC in which the policy is contained.</dd>
		<br>
		<dt>Object Attribute</dt>
		<br>
		<dd>An Object Attribure node is a container that can hold other Object Attrbibutes or objects, and are often the target of the policies that are defined by the Policy Class they are assigned to.
			For example, in a Multiple Layer Security (MLS) Policy Class, there may be an Object Attribute labeled "Top Secret".  The nodes (Object Attributes or Objects) that are then assigned to this Object
			Attribute might be subjected to the policies defined by the MLS policy class on "Top Secret".</dd>
		<br>
		<dt>Object</dt>
		<br>
		<dd>An Object is a representation of data, whether the data is on a file system or in a schema.</dd>
		<br>
		<dt>User Attribute</dt>
		<br>
		<dd>A User Attribute node is a collection of one or many users.</dd>
		<br>
		<dt>User</dt>
		<br>
		<dd>A User node is a representation of a User of the Policy Machine.</dd>
		<br>
		<dt>Operation Set</dt>
		<br>
		<dd>An Operation Set node is a collection of Operations.  This set is then used to connect a User Attribute to an Object Attribute, creating an association relationship in which the users that belong to the
			User Attribute are granted the rights in the Operation set on the Object Attribute.  For example, if there is an Object Attribute called "Medical Records" and a User Attribute called "Doctors" and we want to give
			doctors the permission to read and write, we would create the association: "Doctor" ---> Operation Set{read, write} ---> "Medical Records".</dd>
		<br>
	</dl>

	<h3>Deny Constraints</h3>
	<p>While Policies can be defined on tables and columns, there is still a need to restrict access at the record field level. For example, consider a table called "Employee Record" with the columns: Name, Phone Number, Salary and a User Bob.
		The Name and Phone Number fields may be public information available to everyone, however, the Salary field is private data and Bob can only read his own salary, no one elses.  We can use a deny constraint to deny
		Bob the ability to read the column "Salary" instersected with the complement of his own Record.  This would lead to Bob only being able to read the Salary field of his own record.</p>
	<h3>Assignments</h3>
	<p>Assignments are fundamental to the Policy Machine because they are how Policies are created and enforced.</p>
	<h3>Operations</h3>
	<p>Brief explanation of different kinds (class) of Operations.  Describe resource vs admin</p>
	<h3>Policy Scripts</h3>
	Policy Scripts are another means of defining policies in the Policy Machine.  For example, we can write a script that when a User is created, an Object Attribute called User Home is also created.  We can then grant the new User the permissions
	read and write on that Object Attribute. This is just one example of using Policy Scripts. An in-depth documentation with example scripts is available <a href="./doc/policyScriptsDoc.pdf" target="_blank">here</a>.
</div>
<div class="doc-div card">
	<h1 align="center">Examples</h1>
	<p>Examples and use cases of Policy Machine calls</p>
</div>
<div class="doc-div card">
	<h1 align="center">Policy Machine API</h1>
</div>
</body>
</html>

