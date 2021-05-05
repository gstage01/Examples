
//Initialize map and service variables
function initMap() {
  // Create a map object and specify the DOM element for display.
  map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: 44.9727, lng: -93.235},
    zoom: 14
  });
  directionsService = new google.maps.DirectionsService();
  directionsDisplay = new google.maps.DirectionsRenderer();
  var geocoder = new google.maps.Geocoder();
  add_marks(map);
}

//Function to change pages
function change_to_places() {
  document.location.href = "hw3.html";
}
function change_to_form() {
  document.location.href = "forms.html";
}
//Initializes markers from table
markers = [];
function add_marks(map) {
  var geocoder = new google.maps.Geocoder();
  var address;
  var i;
  for (i = 1; i<5; i++) {
    address = document.getElementById('fav_table').rows[i].cells[1].innerHTML;
    name = document.getElementById('fav_table').rows[i].cells[0].innerHTML;
    var infowindow = new google.maps.InfoWindow();
    geocoder.geocode( { 'address': address}, function(results, status) {
      if (status == 'OK') {
        var marker = new google.maps.Marker({
          map: map,
          position: results[0].geometry.location,
          animation: google.maps.Animation.DROP,
          title: name
        });

        markers.push(marker);
        google.maps.event.addListener(marker, 'click', function () {
          infowindow.setContent(this.title);
          infowindow.open(map, this);
        });
      } else {
        alert('Geocode was not successful for the following reason: ' + status);
      }
    });
  }
}

// Clear markers
var service;
function clearMarkers() {
  for (i=0; i<markers.length; i++) {
    markers[i].setMap(null);
  }
  markers = [];
}

// Function to search for nearby places
function perform_search() {
  clearMarkers();
  searchType = document.getElementById("radiusType").value;
  var request = {
    location: new google.maps.LatLng(44.9727, -93.23540000000003),
    radius: '' + document.getElementById("radius").value,
    query: searchType
  }
  service = new google.maps.places.PlacesService(map);
  service.textSearch(request, callback);
}

// Callback function for search
function callback(results, status) {
  if (status == google.maps.places.PlacesServiceStatus.OK) {
    var infowindow = new google.maps.InfoWindow();
    for (var i = 0; i < results.length; i++) {
      var marker = new google.maps.Marker({
        map: map,
        position: results[i].geometry.location,
        animation: google.maps.Animation.DROP,
        title: results[i].name
      });
      markers.push(marker);
      var marker = markers[i];
      google.maps.event.addListener(marker, 'click', function () {
        infowindow.setContent(this.title);
        infowindow.open(map, this);
      });
    }
  }
}

// Function for getting directions from current location to destination
function get_directions() {
  navigator.geolocation.getCurrentPosition(function (position) {
    currentPos = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
    document.getElementById("right-panel").innerHTML="";
    directionsDisplay.setMap(map);
    var start = currentPos;
    var end = document.getElementById("getDest").value;
    var request = {
      origin: start,
      destination: end,
      travelMode: document.getElementById("traveltype").value
    };
    directionsService.route(request, function(result,status) {
      if (status == "OK") {
        directionsDisplay.setDirections(result);
        directionsDisplay.setPanel(document.getElementById("right-panel"));
      } else {
        alert("Destination failed. Error: " + status);
      }
    });
  });
}


// Functions for forms.html
// Function to validate text input from form
function validate() {
  var x = document.getElementById("placename").value;

  //Regular expression for all alphanumerical characters
  var regex = regex = /^[a-z0-9]|^$/i;
  var afterTest = regex.test(x);
  var currentFails = [];
  if (!afterTest) {
    alert("Place name and Address must be alphanumeric");
    return false;
  }
  x = document.getElementById("addressline1").value;
  afterTest = regex.test(x);
  if (!afterTest) {
    alert("Place name and Address must be alphanumeric");
    return false;
  }
  x = document.getElementById("addressline2").value;
  afterTest = regex.test(x);
  if (!afterTest) {
    alert("Place name and Address must be alphanumeric");
    return false;
  }
  return true;
}
