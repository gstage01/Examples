<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Favourite Places</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="style.css" />
    <nav>
      <ul>
        <li><a href="favplaces.php">Favourite Places</a></li>
        <li><a href="favplaces.php?logout=True"><span class="glyphicon glyphicon-log-out"></a></li>
        <li class="user_wrapper"><div class="last-child">Welcome: <?php session_start(); echo $_SESSION["user"]; ?></span></div></li>
      </ul>
    </nav>
  </head>
  <body>
    <div class="body-wrapper">
      <h3>Favourite Places</h3>
    <?php
      //Import session and database values
      session_start();
      include 'database.php';

      //Check for valid session existing
      if (!$_SESSION["valid"]) {
        session_destroy();
        $_SESSION = array();
		// Edited out URL
        header("Location: login.php");
      }
      //Handler for logout button
      //GET requests on this page are caused by direct link or from logging in
      if ($_SERVER["REQUEST_METHOD"] == "GET") {
        if ($_GET["logout"] == True) {
          session_destroy();
          $_SESSION = array();
		  // Edited out URL
          header("Location: login.php");
        }
        $sql = "SELECT * FROM tbl_places";
      //If the request is not GET, it is POST from the filter
      } else {

        //Check for each condition of each field being set
        if ($_POST["id"] == "" && $_POST["name"] == "") {
          $sql = "SELECT * FROM tbl_places";
        } else if ($_POST["id"] != "" && $_POST["name"] == "") {
          $sql = "SELECT * FROM tbl_places WHERE place_id = '{$_POST["id"]}'";
        } else if ($_POST["id"] == "" && $_POST["name"] != "") {
          $sql_name = '%' . "{$_POST["name"]}" . '%';
          //Like is used with wildcards to return all queries where the filter is in the name anywhere.
          $sql = "SELECT * FROM tbl_places WHERE place_name LIKE '{$sql_name}'";
        } else {
          $sql = "SELECT * FROM tbl_places WHERE place_id = '{$_POST["id"]}'";
        }
      }
      //Code for building the table itself
      echo '<table>';
      echo '<th>Id</th>';
      echo '<th>Name</th>';
      echo '<th>address</th>';
      echo '<th>Open / Close</th>';
      echo '<th>Information</th>';
      echo '<th>URL</th>';

      //SQL connection
      $conn=new mysqli($db_host,$db_username,$db_password,$db_name,$db_port);
      if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
      } else {
        //Query the database, then place each returned row in the table
        $response = $conn->query($sql);
        while ($row = mysqli_fetch_row($response)) {
          echo '<tr>';
            echo "<td>{$row[0]}</td>";
            echo "<td>{$row[1]}</td>";
            echo "<td>{$row[2]} / {$row[3]}</td>";
            echo "<td>{$row[4]} / {$row[5]}</td>";
            echo "<td>{$row[6]}</td>";
            echo "<td>{$row[7]}</td>";
          echo '</tr>';
        }
      }
      //Close the connection and table tag
      mysqli_close($conn);
      echo '</table>';
    ?>

    <h3>Filter Criteria</h3>
    <form method="post" action="favplaces.php">
      <p>Place Id:</p>
      <input type="text" name="id" placeholder="Enter place id" class="break" />
      <p>Place Name:</p>
      <input type="text" name="name" placeholder="Enter place name"/> <br />
      <button class="submit">Submit</button>
    </form>
  </div>
  </body>
</html>
