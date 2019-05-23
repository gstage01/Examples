var canvas;
var gl;
var programId;

var numAngles = 8;
var stepsPerCurve = 6;
var shape = "profile1";

// The OpenGL ID of the vertex buffer containing the current shape
var positionBufferId;

// Vertex buffer containing texture coordinates
var textureBuffer;

// Buffer for light normals
var normalBuffer;

// The number of vertices in the current vertex buffer
var vertexCount;

// Binds "on-change" events for the controls on the web page
function initControlEvents() {
    // Use one event handler for all of the shape controls
    document.getElementById("shape-select").onchange =
    document.getElementById("numAngles").onchange =
    document.getElementById("stepsPerCurve").onchange =
        function(e) {
            shape = document.getElementById("shape-select").value;
            numAngles = parseFloat(document.getElementById("numAngles").value);
            stepsPerCurve = parseFloat(document.getElementById("stepsPerCurve").value);

            // Regenerate the vertex data
            vertexCount = buildSurfaceOfRevolution(getControlPoints(), numAngles, stepsPerCurve);
        };

    // Event handler for the material control
    document.getElementById("material").onchange =
        function(e) {
            updateMaterial(getMaterial());
        }
}

// The current view matrix
var viewMatrix;

// Sets up keyboard and mouse events
function initWindowEvents() {

    // Whether or not the mouse button is currently being held down for a drag.
    var mousePressed = false;

    // Affects how much the camera moves when the mouse is dragged.
    var sensitivity = 0.01;
    var lightSens = 0.001;

    var theta = 0, phi = 0, radius = 5;

    // The place where a mouse drag was started.
    var prevX, prevY;

    var grabbedPoint;

    canvas.onmousedown = function(e) {
        // A mouse drag started.
        mousePressed = true;

        // Remember where the mouse drag started.
        prevX = e.clientX;
        prevY = e.clientY;
    }

    canvas.onmousemove = function(e) {
        if (mousePressed) {

            if (e.shiftKey) {
                // Handle light movement here.
                theta += (e.clientX - prevX) * lightSens;
                phi += (e.clientY - prevY) * lightSens;

                if (theta < -2 * Math.PI) {
                    theta += 2 * Math.PI;
                } else if (theta > 2 * Math.PI) {
                    theta -= 2 * Math.PI;
                }

                if (phi < -Math.PI / 2) {
                    phi = -Math.PI / 2;
                } else if (phi > Math.PI / 2) {
                    phi = Math.PI / 2;
                }

                var light = gl.getUniformLocation(programId, "u_reverseLightDirection");
                var lightValue = gl.getUniform(programId, light);
                var lightProjection = normalize(vec3(radius * Math.cos(theta) * Math.cos(phi),
                            radius * Math.cos(phi),
                            radius * Math.sin(theta) * Math.cos(phi)));

                gl.uniform3fv(light, lightProjection);

            } else {

                // Handle a mouse drag
                theta += (e.clientX - prevX) * sensitivity;
                phi += (e.clientY - prevY) * sensitivity;

                if (theta < -2 * Math.PI) {
                    theta += 2 * Math.PI;
                } else if (theta > 2 * Math.PI) {
                    theta -= 2 * Math.PI;
                }

                if (phi < -Math.PI / 2) {
                    phi = -Math.PI / 2;
                } else if (phi > Math.PI / 2) {
                    phi = Math.PI / 2;
                }

                // Update the model-view matrix.
                gl.useProgram(programId);
                updateModelView(lookAt(
                    vec3(radius * Math.cos(theta) * Math.cos(phi),
                         radius * Math.sin(phi),
                         radius * Math.sin(theta) * Math.cos(phi)),
                    vec3(0), vec3(0, 1, 0)));

                prevX = e.clientX;
                prevY = e.clientY;
            }


        }
    }

    window.onmouseup = function(e) {
        // A mouse drag ended.
        mousePressed = false;
    }

    var speed = 0.1; // Affects how fast the camera "zooms"

    window.onkeydown = function(e) {

        if (e.keyCode === 190) { // '>' key
            // "Zoom" in
            radius -= speed;
        }
        else if (e.keyCode === 188) { // '<' key
            // "Zoom" out
            radius += speed;
        }
        if (!e.shiftKey) {
          // Update the model-view matrix.
          gl.useProgram(programId);
          updateModelView(lookAt(
              vec3(radius * Math.cos(theta) * Math.cos(phi),
                   radius * Math.sin(phi),
                   radius * Math.sin(theta) * Math.cos(phi)),
              vec3(0), vec3(0, 1, 0)));
        }
    }
}

function calculateNormal(p1, p2, p3) {
  return normalize(cross(subtract(p2, p1), subtract(p3, p1)));
}

function getControlPoints1() {

    var controlPoints = [];

    // Initialize control point data
    for (var i = 0; i < 7; i++)
    {
        controlPoints[i] = vec4(0.5, i / 6.0 * 1.6 - 0.8, 0, 1);
    }

    return controlPoints;
}

function getControlPoints2() {

    var controlPoints = [];
    controlPoints[0] = vec4(0.1, -1.0, 0.0, 1);
    controlPoints[1] = vec4(0.3, -0.8, 0.0, 1);
    controlPoints[2] = vec4(0.4, -0.4, 0.0, 1);
    controlPoints[3] = vec4(0.45,  0.0, 0.0, 1);
    controlPoints[4] = vec4(0.5,  0.4, 0.0, 1);
    controlPoints[5] = vec4(0.7,  0.8, 0.0, 1);
    controlPoints[6] = vec4(0.9,  1.0, 0.0, 1);
    return controlPoints;
}

function getControlPoints3() {

    var controlPoints = [];
    controlPoints[0] = vec4(0.9, -1.0, 0.0, 1);
    controlPoints[1] = vec4(0.7, -0.8, 0.0, 1);
    controlPoints[2] = vec4(0.5, -0.4, 0.0, 1);
    controlPoints[3] = vec4(0.5,  0.0, 0.0, 1);
    controlPoints[4] = vec4(0.5,  0.4, 0.0, 1);
    controlPoints[5] = vec4(0.7,  0.8, 0.0, 1);
    controlPoints[6] = vec4(0.9,  1.0, 0.0, 1);
    return controlPoints;
}

function getControlPoints4() {

    var controlPoints = [];
    controlPoints[0] = vec4(0.1, -1.0, 0.0, 1);
    controlPoints[1] = vec4(0.5, -0.8, 0.0, 1);
    controlPoints[2] = vec4(0.7, -0.4, 0.0, 1);
    controlPoints[3] = vec4(0.7,  0.0, 0.0, 1);
    controlPoints[4] = vec4(0.7,  0.4, 0.0, 1);
    controlPoints[5] = vec4(0.5,  0.8, 0.0, 1);
    controlPoints[6] = vec4(0.1,  1.0, 0.0, 1);
    return controlPoints;
}

function getControlPoints5() {

    var controlPoints = [];
    controlPoints[0] = vec4(0.1, -1.0, 0.0, 1);
    controlPoints[1] = vec4(0.5, -0.8, 0.0, 1);
    controlPoints[2] = vec4(0.3, -0.4, 0.0, 1);
    controlPoints[3] = vec4(0.2,  0.0, 0.0, 1);
    controlPoints[4] = vec4(0.1,  0.4, 0.0, 1);
    controlPoints[5] = vec4(0.1,  0.8, 0.0, 1);
    controlPoints[6] = vec4(0.1,  1.0, 0.0, 1);
    return controlPoints;
}

function getControlPoints() {

    if (shape == "profile1") {
        return getControlPoints1()
    }
    else if (shape == "profile2") {
        return getControlPoints2()
    }
    else if (shape == "profile3") {
        return getControlPoints3()
    }
    else if (shape == "profile4") {
        return getControlPoints4()
    }
    else if (shape == "profile5") {
        return getControlPoints5()
    }
}

function getTVector(vt)
{
    // Compute value of each basis function
    var mt = 1.0 - vt;
    return vec4(mt * mt * mt, 3 * vt * mt * mt, 3 * vt * vt * mt, vt * vt * vt);
}

function dotProduct(pnt1, pnt2, pnt3, pnt4, tVal)
{
    // Take dot product between each basis function value and the x, y, and z values
    // of the control points
    return vec3(pnt1[0]*tVal[0] + pnt2[0]*tVal[1] + pnt3[0]*tVal[2] + pnt4[0]*tVal[3],
                pnt1[1]*tVal[0] + pnt2[1]*tVal[1] + pnt3[1]*tVal[2] + pnt4[1]*tVal[3],
                pnt1[2]*tVal[0] + pnt2[2]*tVal[1] + pnt3[2]*tVal[2] + pnt4[2]*tVal[3]);
}


// You will want to edit this function to compute the additional attribute data
// for texturing and lighting

function buildSurfaceOfRevolution(controlPoints, angles, steps)
{
    if (steps % 2 == 1) {
        steps++;
    }

    var dt = 2.0 / steps;
    var da = 360.0 / (angles);

    var vertices = [];

    var normal;
    var p = 0;
    for (var i = 0; i < 2; i++)
    {
        var bp1 = controlPoints[i * 3 + 0];
        var bp2 = controlPoints[i * 3 + 1];
        var bp3 = controlPoints[i * 3 + 2];
        var bp4 = controlPoints[i * 3 + 3];

        for (var t = 0; t < steps / 2; t++) {
            var p1 = dotProduct(bp1, bp2, bp3, bp4, getTVector(t * dt));
            var p2 = dotProduct(bp1, bp2, bp3, bp4, getTVector((t + 1) * dt));

            var savedP = p;

            // UV coordinates are computed using angles and steps
            // Surface normals are calculated using normalized vertices for each triangle calculated
            for (var a = 0; a < angles; a++) {

                vertices[p] = vec3(Math.cos(a * da * Math.PI / 180.0) * p1[0], p1[1],
                                     Math.sin(a * da * Math.PI / 180.0) * p1[0]);
                texcoords[p] = vec2(a / angles, t / steps);
                p++;

                vertices[p] = vec3(Math.cos(a * da * Math.PI / 180.0) * p2[0], p2[1],
                                     Math.sin(a * da * Math.PI / 180.0) * p2[0]);
                texcoords[p] = vec2(a / angles, (t+1) / steps);
                p++;

                vertices[p] = vec3(Math.cos((a + 1) * da * Math.PI / 180.0) * p1[0], p1[1],
                                     Math.sin((a + 1) * da * Math.PI / 180.0) * p1[0]);
                texcoords[p] = vec2((a+1) / angles, t / steps);
                normals[p-2] = calculateNormal(vertices[p-2], vertices[p-1], vertices[p]);
                normals[p-1] = calculateNormal(vertices[p-1], vertices[p], vertices[p-2]);
                normals[p] = calculateNormal(vertices[p], vertices[p-2], vertices[p-1]);
                p++;

                vertices[p] = vec3(Math.cos((a + 1) * da * Math.PI / 180.0) * p1[0], p1[1],
                                     Math.sin((a + 1) * da * Math.PI / 180.0) * p1[0]);
                texcoords[p] = vec2((a+1) / angles, t / steps);
                p++;

                vertices[p] = vec3(Math.cos(a * da * Math.PI / 180.0) * p2[0], p2[1],
                                     Math.sin(a * da * Math.PI / 180.0) * p2[0]);
                texcoords[p] = vec2(a / angles, (t+1) / steps);
                p++;

                vertices[p] = vec3(Math.cos((a + 1) * da * Math.PI / 180.0) * p2[0], p2[1],
                                     Math.sin((a + 1) * da * Math.PI / 180.0) * p2[0]);
                texcoords[p] = vec2((a+1) / angles, (t+1) / steps);
                normals[p] = calculateNormal(vertices[p], vertices[p-2], vertices[p-1]);
                normals[p-2] = calculateNormal(vertices[p-2], vertices[p-1], vertices[p]);
                normals[p-1] = calculateNormal(vertices[p-1], vertices[p], vertices[p-2]);
                p++;

            }
        }
    }
    // Pass the new set of vertices to the graphics card
    gl.bindBuffer(gl.ARRAY_BUFFER, positionBufferId );
    gl.bufferData(gl.ARRAY_BUFFER, flatten(vertices), gl.DYNAMIC_DRAW);

    // Pass the texture coordinates to the graphics card
    gl.bindBuffer(gl.ARRAY_BUFFER, textureBuffer);
    var texcoordLoc = gl.getAttribLocation(programId, "a_texcoord");

    gl.enableVertexAttribArray(texcoordLoc);
    gl.vertexAttribPointer(texcoordLoc, 2, gl.FLOAT, false, 0, 0);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(texcoords), gl.DYNAMIC_DRAW);

    // Pass the surface normals to the graphics card
    gl.bindBuffer(gl.ARRAY_BUFFER, normalBuffer);
    var normalLocation = gl.getAttribLocation(programId, "a_normal");

    gl.enableVertexAttribArray(normalLocation);
    gl.vertexAttribPointer(normalLocation, 3, gl.FLOAT, false, 0, 0);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(normals), gl.DYNAMIC_DRAW);

    return vertices.length;
}
var normals = [];
var normalCount = 0;
var texcoords = []
var texInfo;
// Render the scene
function viewMethod(vertexCount) {
    // Clear the canvas
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);

    // Use 3D program
    gl.useProgram(programId);

    // Associate vertex buffers with vertex attributes
    var vPosition = gl.getAttribLocation(programId, "vPosition");
    gl.bindBuffer(gl.ARRAY_BUFFER, positionBufferId);
    gl.vertexAttribPointer(vPosition, 3, gl.FLOAT, false, 0, 0)

    // Draw the triangles
    gl.drawArrays(gl.TRIANGLES, 0, vertexCount);
}

function render() {
    viewMethod(vertexCount);
}

// The locations of the required GLSL uniform variables.
var locations = {};

// Looks up the locations of uniform variables once.
function findShaderVariables() {
    locations.modelView = gl.getUniformLocation(programId, "modelView");
    locations.projection = gl.getUniformLocation(programId, "projection");
    locations.triangleColor = gl.getUniformLocation(programId, "triangleColor");
    locations.u_texture = gl.getUniformLocation(programId, "u_texture");
    locations.u_world = gl.getUniformLocation(programId, "u_world");
}

// Pass an updated model-view matrix to the graphics card.
function updateModelView(modelView) {
    gl.uniformMatrix4fv(locations.modelView, false, flatten(modelView));
    gl.uniformMatrix4fv(locations.u_world, false, flatten(modelView));
}

// Pass an updated projection matrix to the graphics card.
function updateProjection(projection) {
    gl.uniformMatrix4fv(locations.projection, false, flatten(projection));
}

// Function for querying the current material
// Returns "plastic", "brass", or "texture"
function getMaterial() {
    return document.getElementById("material").value;
}

// Function called when the material changes
// Parameter will be one of "plastic", "brass", or "texture"
function updateMaterial(material) {
    // Initialize a random texture for giving the image time to load
    var texture = gl.createTexture();
    gl.bindTexture(gl.TEXTURE_2D, texture);
    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, 1, 1, 0, gl.RGBA, gl.UNSIGNED_BYTE,
            new Uint8Array([0, 0, 255, 255]));

    // Material updates
    if (material == "plastic") {
      gl.bindTexture(gl.TEXTURE_2D, texture);
      gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, 1, 1, 0, gl.RGBA, gl.UNSIGNED_BYTE,
              new Uint8Array([254, 221, 0, 255]));
    } else if (material == "brass") {
      gl.bindTexture(gl.TEXTURE_2D, texture);
      gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, 1, 1, 0, gl.RGBA, gl.UNSIGNED_BYTE,
              new Uint8Array([181, 166, 66, 255]));
    } else {
      var image = new Image();
      image.crossOrigin = "anonymous";
      image.src = "http://i.imgur.com/23TQCyu.png";
      image.addEventListener('load', () => {
        gl.bindTexture(gl.TEXTURE_2D, texture);
        gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, true);
        gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGB, gl.RGB, gl.UNSIGNED_BYTE, image);
        gl.generateMipmap(gl.TEXTURE_2D);
      });
    }

}

window.onload = function() {

    // Get initial angles and steps
    numAngles = parseFloat(document.getElementById("numAngles").value);
    stepsPerCurve = parseFloat(document.getElementById("stepsPerCurve").value);

    // Find the canvas on the page
    canvas = document.getElementById("gl-canvas");

    // Initialize a WebGL context
    gl = WebGLUtils.setupWebGL(canvas);
    if (!gl) {
        alert("WebGL isn't available");
    }

    gl.enable(gl.DEPTH_TEST);

    // Load shaders
    programId = initShaders(gl, "vertex-shader", "fragment-shader");
    gl.useProgram(programId);

    // Configure WebGL
    gl.viewport(0, 0, canvas.width, canvas.height);
    gl.clearColor(0.0, 0.0, 0.0, 1.0);

    // Create a vertex buffer object for position
    positionBufferId = gl.createBuffer();

    // Enable the shader variable for position for use with a vertex buffer.
    var vPosition = gl.getAttribLocation(programId, "vPosition");
    gl.enableVertexAttribArray(vPosition);

    // Find all of the shader uniform variables that we need.
    findShaderVariables();

    // Initialize the view matrix
    viewMatrix = lookAt(vec3(0,0,5), vec3(0,0,0), vec3(0,1,0));
    updateModelView(viewMatrix);

    // Initialize the projection matrix
    updateProjection(perspective(50, 1.28, 0.01, 100));

    var lightDirectionLocation = gl.getUniformLocation(programId, "u_reverseLightDirection");
    // Create the surface of revolution
    // (this should load the initial shape into one of the vertex buffer objects you just created)
    // Initialize texture and surface normal buffers
    textureBuffer = gl.createBuffer();
    gl.uniform3fv(lightDirectionLocation, vec3(0.1, 0.0, 1.0));
    normalBuffer = gl.createBuffer();
    vertexCount = buildSurfaceOfRevolution(getControlPoints(), numAngles, stepsPerCurve);

    // Initialize the texture
    updateMaterial(getMaterial());

    // Set up events for the HTML controls
    initControlEvents();

    // Setup mouse and keyboard input
    initWindowEvents();

    // Start continuous rendering
    window.setInterval(render, 33);
};
