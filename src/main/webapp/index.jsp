<!DOCTYPE html>
<html>
<head>
<title>Add Map</title>

<link rel="stylesheet" href="bootstrap-3.2.0-dist/css/bootstrap.css" />
</head>
<body>
	<div class="appForm panel panel-primary">
		<div class="panel-heading">
			<h2 class="label-primary">Hello, Md!</h2>
		</div>
		  
		<div class="appForm panel-body">
			  
			<form action="rs/mapservice/uploadFile" method="POST"
				enctype="multipart/form-data" class="navbar-form navbar-left"
				novalidate>

				<div class="form-group">
					    <label for="name">Select Map</label>      <input type="file"
						class="form-control" name="image" />
				</div>
				   

				<button type="submit" class="btn btn-primary">
					<span class="glyphicon glyphicon-upload"></span>
				</button>
			</form>
		</div>

		<div class="panel-footer">
			<span>iakavramenko@gmail.com <span> 
			<a class="btn btn-primary" href="rs/mapservice/downloadFile"><span
						class="glyphicon glyphicon-download"></span></a>
		</div>
	</div>

</body>
</html>