const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const mysql = require('mysql');
const http = require('http');
const sha1 = require('sha1');
const path = require('path');
const session = require('express-session');

app.set('view engine', 'pug');
app.use(bodyParser.urlencoded({extended: true}));
app.use(session({valid: true, secret: '123456789', cookie: {maxAge: 100000}, resave: true, saveUninitialized: true}));

app.listen(9007, () => console.log("Listening on port 9007"));
app.get('/', (req,res) => mainPage(req,res));
app.get('/addPlace.html', (req,res) => addPlace(req,res));
app.get('/favourites.html', (req,res) => favourites(req,res));
app.get('/style.css', (req,res) => res.sendFile(path.join(__dirname + '/style.css')));
app.get("/logout", (req,res) => logout(req,res));

app.post('/',  (req,res) => checkQuery(req,res));
app.post('/addPlace.html', (req,res) => insertPlace(req,res));


function logout(req,res) {
  req.session.destroy();
  res.redirect("/");
}
function mainPage(req,res) {
  if (req.session.valid == null) {
    res.sendFile(path.join(__dirname + '/login.html'));
  } else if (req.session.valid) {
    res.redirect('/favourites.html');
  } else {
    res.render(path.join(__dirname + '/views/login.pug'));
  }
}

function insertPlace(req,res) {
    var row = {
      place_name: req.body.placename,
      addr_line1: req.body.addressline1,
      addr_line2: req.body.addressline2,
      open_time: req.body.opentime,
      close_time: req.body.closetime,
      add_info: req.body.addressinfo,
      add_info_url: req.body.additionalinfourl
    }
    var con = mysql.createConnection({
      host: "cse-curly.cse.umn.edu",
      user: "C4131S18U114", // replace with the database user provided to you
      password: "119", // replace with the database password provided to you
      database: "C4131S18U114", // replace with the database user provided to you
      port: 3306
    });
    con.connect(function (err) {
      if (err) {throw err;}
      con.query("INSERT tbl_places SET ?", row, function(err, results) {
        if (err) {throw err;}

        res.redirect("/favourites.html");
      });
    });


}

function addPlace(req,res) {
  if (req.session.valid) {
    res.sendFile(path.join(__dirname + '/addPlace.html'));
  } else {
    res.redirect('/');
  }
}

function favourites(req,res) {
  var con = mysql.createConnection({
    host: "cse-curly.cse.umn.edu",
    user: "C4131S18U114", // replace with the database user provided to you
    password: "119", // replace with the database password provided to you
    database: "C4131S18U114", // replace with the database user provided to you
    port: 3306
  });
  con.connect(function (err) {
    if (err) {throw err;}
    con.query("SELECT * FROM tbl_places T", function(err, results) {
      if (err) {throw err;}
      if (req.session.valid) {
        res.render(path.join(__dirname + '/views/favourites.pug'), {list: results});
      } else {
        res.redirect('/');
      }
    });
  });

}

function checkQuery(req, res) {
  var con = mysql.createConnection({
    host: "cse-curly.cse.umn.edu",
    user: "C4131S18U114", // replace with the database user provided to you
    password: "119", // replace with the database password provided to you
    database: "C4131S18U114", // replace with the database user provided to you
    port: 3306
  });
  con.connect(function(err) {
    if (err) {throw err;};
    con.query("SELECT * FROM tbl_accounts T HAVING T.acc_name = '" + req.body.user + "'", function (err, results) {
      if (err) {throw err;};
  		for (var i=0; i<results.length; i++) {
        if (results[i].acc_password == sha1(req.body.pass)) {
          req.session.valid = true;
          con.query("SELECT * FROM tbl_places T", function (err,results) {
            if (err) {throw err;}
              req.session.places = results;
              res.redirect('favourites.html');
            });
          return;
        }
      }
      req.session.valid = false;
      res.redirect('/');
    });
  });
}
