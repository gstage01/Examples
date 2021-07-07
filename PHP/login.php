<<<<<<< HEAD

<!DOCTYPE html>
<html lang="en" dir="ltr">
  <head>
    <meta charset="utf-8">
    <link rel="stylesheet" href="style.css" />
    <title>Login Page</title>
  </head>
  <body>
    <div class="textArea">
      <h1>Login Page</h1>
      <h2>Please enter your user name and password. Both are case Sensitive.</h2>
  </div>
  <div class="formContainer">
  <form method="post">
    <div class="formText">User:<br /></div>
    <input type="text" name="user" placeholder="Enter user here" required/><br />
    <div class="formText">Password:<br /></div>
    <input type="text" name="pass" placeholder = "Enter Pass here" required/><br />
    <button class="submit">Submit</button>
  </form>

  <?php
  include 'database.php';

  $username = $_POST["user"];
  $password = $_POST["pass"];

  $sql = "SELECT acc_password FROM tbl_accounts WHERE acc_login='{$username}'";
  $conn=new mysqli($db_host,$db_username,$db_password,$db_name,$db_port);
  if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
  } else {
    $response = $conn->query($sql);
    while ($row = mysqli_fetch_row($response)) {
      if (sha1($password) == $row[0]) {
        session_start();
        //valid is used to ensure the user logged in or has a session.
        //user is used to pass the user information onto favplaces.php
        $_SESSION["valid"] = True;
        $_SESSION["user"] = $username;
		// Edited out database URL
        header("Location: XXXX");
      }
    }
  }
  mysqli_close($conn);
  ?>
  </div>
  </body>
</html>
=======

<!DOCTYPE html>
<html lang="en" dir="ltr">
  <head>
    <meta charset="utf-8">
    <link rel="stylesheet" href="style.css" />
    <title>Login Page</title>
  </head>
  <body>
    <div class="textArea">
      <h1>Login Page</h1>
      <h2>Please enter your user name and password. Both are case Sensitive.</h2>
  </div>
  <div class="formContainer">
  <form method="post">
    <div class="formText">User:<br /></div>
    <input type="text" name="user" placeholder="Enter user here" required/><br />
    <div class="formText">Password:<br /></div>
    <input type="text" name="pass" placeholder = "Enter Pass here" required/><br />
    <button class="submit">Submit</button>
  </form>

  <?php
  include 'database.php';

  $username = $_POST["user"];
  $password = $_POST["pass"];

  $sql = "SELECT acc_password FROM tbl_accounts WHERE acc_login='{$username}'";
  $conn=new mysqli($db_host,$db_username,$db_password,$db_name,$db_port);
  if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
  } else {
    $response = $conn->query($sql);
    while ($row = mysqli_fetch_row($response)) {
      if (sha1($password) == $row[0]) {
        session_start();
        //valid is used to ensure the user logged in or has a session.
        //user is used to pass the user information onto favplaces.php
        $_SESSION["valid"] = True;
        $_SESSION["user"] = $username;
		// Edited out database URL
        header("Location: XXXX");
      }
    }
  }
  mysqli_close($conn);
  ?>
  </div>
  </body>
</html>
>>>>>>> fac084134fc8c24124d12babe72cf259ce33f12b
