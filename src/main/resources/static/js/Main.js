function parseCommand(input = "") {
  return JSON.parse(input);
}
var socket;
window.onload = function() {

  //declareren van de variabelen
  var camera, scene, renderer;
  var cameraControls;
  var worldObjects = {};
  var mtlLoader = new THREE.MTLLoader();
  //variabel voor auto die op de achtergrond te zien is
  var truckObjectProp;
  function socket() {
    /*
     * Hier wordt de socketcommunicatie geregeld. Er wordt een nieuwe websocket aangemaakt voor het webadres dat we in
     * de server geconfigureerd hebben als connectiepunt (/connectToSimulation). Op de socket wordt een .onmessage
     * functie geregistreerd waarmee binnenkomende berichten worden afgehandeld.
     */
    socket = new WebSocket(
      "ws://" +
        window.location.hostname +
        ":" +
        window.location.port +
        "/connectToSimulation"
    );
    socket.onmessage = function(event) {
      //Hier wordt het commando dat vanuit de server wordt gegeven uit elkaar gehaald
      var command = parseCommand(event.data);
      //Wanneer het commando is "object_update", dan wordt deze code uitgevoerd. Bekijk ook de servercode om dit goed te begrijpen.
      if (command.command == "object_update") {
        var mtlLoader = new THREE.MTLLoader();
        //Wanneer het object dat moet worden geupdate nog niet bestaat (komt niet voor in de lijst met worldObjects op de client),
        //dan wordt het 3D model eerst aangemaakt in de 3D wereld.
        if (Object.keys(worldObjects).indexOf(command.parameters.uuid) < 0) {
         
          //Als "robot" als type wordt verstuurd naar de browser wordt er een robot gemaakt
          if (command.parameters.type == "robot") {
            var geometry = new THREE.BoxGeometry(0.9, 0.3, 0.9);
            var cubeMaterials = [
              new THREE.MeshBasicMaterial({
                map: new THREE.TextureLoader().load(
                  "textures/robot/robot_side.png"
                ),
                side: THREE.DoubleSide
              }), //LEFT
              new THREE.MeshBasicMaterial({
                map: new THREE.TextureLoader().load(
                  "textures/robot/robot_side.png"
                ),
                side: THREE.DoubleSide
              }), //RIGHT
              new THREE.MeshBasicMaterial({
                map: new THREE.TextureLoader().load(
                  "textures/robot/robot_top.png"
                ),
                side: THREE.DoubleSide
              }), //TOP
              new THREE.MeshBasicMaterial({
                map: new THREE.TextureLoader().load(
                  "textures/robot/robot_bottom.png"
                ),
                side: THREE.DoubleSide
              }), //BOTTOM
              new THREE.MeshBasicMaterial({
                map: new THREE.TextureLoader().load(
                  "textures/robot/robot_front.png"
                ),
                side: THREE.DoubleSide
              }), //FRONT
              new THREE.MeshBasicMaterial({
                map: new THREE.TextureLoader().load(
                  "textures/robot/robot_front.png"
                ),
                side: THREE.DoubleSide
              }) //BACK
            ];

            var material = new THREE.MeshFaceMaterial(cubeMaterials);
            var robot = new THREE.Mesh(geometry, material);
            
            robot.position.y = 0.15;
            robot.receiveShadow = true;
            robot.castShadow = true;
            var group = new THREE.Group();
            group.add(robot);
            scene.add(group);
            worldObjects[command.parameters.uuid] = group;
          }
          //Als "crate" als type wordt verstuurd naar de browser wordt er een crate gemaakt
          if (command.parameters.type == "box") {
            var BoxGeometry = new THREE.BoxGeometry(0.9, 0.9, 0.9);
            var BoxMaterials = new THREE.MeshPhongMaterial({
              color: 0xffffff,
              map: new THREE.TextureLoader().load(
                "textures/crate/crate1_diffuse.png"
              ),
              bumpMap: new THREE.TextureLoader().load(
                "textures/crate/crate1_bump.png"
              )
            });
            crate = new THREE.Mesh(BoxGeometry, BoxMaterials);
            crate.castShadow = true;
            crate.receiveShadow = true;
            scene.add(crate);
            worldObjects[command.parameters.uuid] = crate;
          }
          
          //Als "robotpath" als type wordt verstuurd naar de browser wordt er een robotpath gemaakt
          if (command.parameters.type == "robotpath") {
            var geometry = new THREE.PlaneGeometry(1, 1);
            var material = new THREE.MeshPhongMaterial({
              map: new THREE.TextureLoader().load("textures/concrete.jpeg"),
              side: THREE.DoubleSide
            });
            var plane = new THREE.Mesh(geometry, material);
            plane.rotation.x = Math.PI / 2;
            plane.receiveShadow = true;
            plane.castShadow = true;
            var group = new THREE.Group();
            group.add(plane);
            scene.add(group);
            worldObjects[command.parameters.uuid] = group;
          }
          
          //Als "truck" als type wordt verstuurd naar de browser wordt er een vrachtwagen gemaakt
          if (command.parameters.type == "truck") {
            var truckObject = new THREE.Object3D();
            scene.add(truckObject);
            worldObjects[command.parameters.uuid] = truckObject;
            mtlLoader.load("textures/truck/truckwithoutback.mtl", function(materials) {
              materials.preload();
              const objLoader = new THREE.OBJLoader();
              objLoader.setMaterials(materials);
              objLoader.load("textures/truck/truckwithoutback.obj", function(truck) {
                truck.traverse(function(node) {
                  if (node instanceof THREE.Mesh) {
                    node.castShadow = true;
                    node.receiveShadow = true;
                  }
                });
                truckObject.scale.set(5, 5, 5);
                truckObject.model = truck;
                truckObject.add(truckObject.model);
              });
            });
          }
        }
        /*
         * Deze code wordt elke update uitgevoerd. Het update alle positiegegevens van het 3D object.
         */
        var object = worldObjects[command.parameters.uuid];
        object.position.x = command.parameters.x;
        object.position.y = command.parameters.y;
        object.position.z = command.parameters.z;
        object.rotation.x = command.parameters.rotationX;
        object.rotation.y = command.parameters.rotationY;
        object.rotation.z = command.parameters.rotationZ;
      }
    };
  }
  
  function init() {
  	//funties om de wereld op te bouwen
    setRenderer();
    createScene();
    addCamera();
    addlighting();
    addSkybox();
    addWorldPlane();
    addWarehouse();
    window.addEventListener("resize", onWindowResize, false);
  }
  
  //Als de browser van formaat verandert doet de wereld dat ook
  function onWindowResize() {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth, window.innerHeight);
  }
  
  //elke frame wordt deze functie uitgevoerd om de wereld te updaten
  function animate() {
  	//Loop voor de vrachwagen
  	if (truckObjectProp.position.z > 500) 
  		truckObjectProp.position.z = -480;
    truckObjectProp.position.z += 1.5;
    requestAnimationFrame(animate);
    cameraControls.update();
    renderer.render(scene, camera);
  }
  
  //WebGLRenderer
  function setRenderer() {
    //Renderer
    renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.setSize(window.innerWidth, window.innerHeight + 5);
    document.body.appendChild(renderer.domElement);
  }
  
  //Nieuwe scene
  function createScene() {
    //Scene
    scene = new THREE.Scene();
  }
  
  //Camera toevoegen
  function addCamera() {
    //Camera
    camera = new THREE.PerspectiveCamera(
      70,
      window.innerWidth / window.innerHeight,
      1,
      1000
    );
    cameraControls = new THREE.OrbitControls(camera);
    cameraControls.enableDamping = true; // an animation loop is required when either damping or auto-rotation are enabled
    cameraControls.dampingFactor = 0.05;
    cameraControls.screenSpacePanning = false;
    cameraControls.minDistance = 0;
    cameraControls.maxDistance = 300;
    cameraControls.maxPolarAngle = Math.PI / 2.1;
    cameraControls.rotateSpeed = 0.04;
    cameraControls.panSpeed = 0.1;
    camera.position.z = 50;
    camera.position.y = 50;
    camera.position.x = 100;
    cameraControls.update();
  }
  
  //HemisphereLight, AmbientLight en Pointlight
  function addlighting() {
    //AmbientLight
    var ambientLight = new THREE.AmbientLight(0xffffff, 0.3);
    scene.add(ambientLight);
    
    //PointLight
    light = new THREE.PointLight(0xffffff, 0.5, 180);
    light.position.set(50, 40, 40);
    light.castShadow = true;
    // Will not light anything closer than 0.1 units or further than 25 units
    //light.shadow.camera.near = -1;
    //light.shadow.camera.far = 100;
    scene.add(light);
    
    //HemisphereLight
    hemiLight = new THREE.HemisphereLight( 0xffffbb, 0x080820, 0.6 ); 
    scene.add(hemiLight);

  }
  
  //SkyBox toevoegen als achtergrond
  function addSkybox() {
    //Skybox
    const loader = new THREE.CubeTextureLoader();
    //dust == daytime
    //Divine == nighttime
    const texture = loader.load([
      "textures/skybox/dust_ft.jpg",
      "textures/skybox/dust_bk.jpg",
      "textures/skybox/dust_up.jpg",
      "textures/skybox/dust_dn.jpg",
      "textures/skybox/dust_rt.jpg",
      "textures/skybox/dust_lf.jpg"
    ]);
    scene.background = texture;
  }
  
  //Wereld platform aanmaken met bomen op willekeurige plekken behalve in magazijn of op de weg
  function addWorldPlane() {
    //Worldplane
    var randomnumberx = 0;
    var randomnumberz = 0;
    
    var tiles = ["tile_tree", "tile_treeDouble", "tile_treeQuad"];
    for (var i = 0; i < 75; i++) {
    mtlLoader.load("textures/tiles/" + tiles[Math.floor(Math.random() * tiles.length)] + ".mtl", function(materials) {
      materials.preload();
      const objLoader = new THREE.OBJLoader();
      objLoader.setMaterials(materials);
      objLoader.load("textures/tiles/" + tiles[Math.floor(Math.random() * tiles.length)] + ".obj", function(mesh) {
        mesh.traverse(function(node) {
          if (node instanceof THREE.Mesh) {
            node.castShadow = true;
            node.receiveShadow = true;
          }
        });
        while ((randomnumberx < 20 && randomnumberx > -20) || (randomnumberx > -230 && randomnumberx < -180)) {
        	randomnumberx = Math.random() * 480;
        	if (Math.random() > 0.5)
        		randomnumberx *= -1;
	        }
        while (randomnumberz < 20 && randomnumberz > -20) {
        	randomnumberz = Math.random() * 480;
        	if (Math.random() > 0.5)
        		randomnumberz *= -1;
	        }
        scene.add(mesh);
        mesh.position.set(randomnumberx, -4 ,randomnumberz);
    	randomnumberx = 0;
    	randomnumberz = 0;
        mesh.scale.set(20, 20, 20);
	      });
	    });
	  }
  	//Groene platform
    var tile = "tile";
    mtlLoader.load("textures/tiles/" + tile + ".mtl", function(materials) {
      materials.preload();
      const objLoader = new THREE.OBJLoader();
      objLoader.setMaterials(materials);
      objLoader.load("textures/tiles/" + tile + ".obj", function(mesh) {
        mesh.traverse(function(node) {
          if (node instanceof THREE.Mesh) {
            node.castShadow = true;
            node.receiveShadow = true;
          }
        });
        scene.add(mesh);
        mesh.position.x = 0;
        mesh.position.y = -0.2;
        mesh.position.z = 0;
        mesh.scale.set(1000, 1, 1000);
      });
    });
  }
  function addWarehouse() {
  	    //Logo m.b.v. GLTFLoader
		var loader = new THREE.GLTFLoader();
		loader.load('textures/logo/Capture.gltf', handle_load);
	  
	    function handle_load(gltf) {

        console.log(gltf);
        mesh = gltf.scene;
        console.log(mesh.children[0]);
        mesh.children[0].material = new THREE.MeshLambertMaterial();
		scene.add( mesh );
		mesh.scale.set(100,10,100);
		mesh.position.set(21.2,14,21.5);
        mesh.rotation.x = -Math.PI / 2;
        mesh.rotation.y = Math.PI;
        mesh.rotation.z = Math.PI /2;
    }
  	  
      //vloer van het magazijn
      mtlLoader.load("textures/warehouse/roadTile_001.mtl", function(materials) {
        materials.preload();
        const objLoader = new THREE.OBJLoader();
        objLoader.setMaterials(materials);
        objLoader.load("textures/warehouse/roadTile_001.obj", function(mesh) {
          mesh.traverse(function(node) {
            if (node instanceof THREE.Mesh) {
              node.castShadow = true;
              node.receiveShadow = true;
            }
          });
          scene.add(mesh);
          mesh.position.z = 30;
          mesh.position.x = 20.5;
          mesh.position.y = 0.5;
          mesh.rotation.y = Math.PI / 2;
          mesh.scale.set(10.5, 3, 6.8);
        });
      });
      
	  //Rustplek robots
      mtlLoader.load("textures/warehouse/roadTile_001.mtl", function(materials) {
        materials.preload();
        const objLoader = new THREE.OBJLoader();
        objLoader.setMaterials(materials);
        objLoader.load("textures/warehouse/roadTile_001.obj", function(mesh) {
          mesh.traverse(function(node) {
            if (node instanceof THREE.Mesh) {
              node.castShadow = true;
              node.receiveShadow = true;
            }
          });
          scene.add(mesh);
          mesh.position.z = 19.5;
          mesh.position.x = 24.5;
          mesh.position.y = 0.5;
          mesh.rotation.y = Math.PI / 2;
          mesh.scale.set(3, 3, 2);
        });
      });
      
      //Magazijn
      mtlLoader.setMaterialOptions( { side: THREE.DoubleSide } );
      mtlLoader.load("textures/warehouse/old_warehouse01_upgrade.mtl", function(materials) {
        materials.preload();
        const objLoader = new THREE.OBJLoader();
        objLoader.setMaterials(materials);
        objLoader.load("textures/warehouse/old_warehouse01_upgrade.obj", function(mesh) {
          mesh.traverse(function(node) {
            if (node instanceof THREE.Mesh) {
              node.castShadow = true;
              node.receiveShadow = true;
            }
          });
          scene.add(mesh);
          mesh.position.z = 30;
          mesh.position.x = 10.5;
          mesh.position.y = 10;
          mesh.rotation.y = Math.PI / 2;
          mesh.rotation.y = Math.PI / 2;
          mesh.scale.set(0.05, 0.05, 0.026);
        });
      });
      //Vrachtwagen prop die achter het magazijn rijdt
      truckObjectProp = new THREE.Object3D();
          scene.add(truckObjectProp);
          mtlLoader.load("textures/truck/delivery.mtl", function(materials) {
            materials.preload();
            const objLoader = new THREE.OBJLoader();
            objLoader.setMaterials(materials);
            objLoader.load("textures/truck/delivery.obj", function(truck) {
              truck.traverse(function(node) {
                if (node instanceof THREE.Mesh) {
                  node.castShadow = true;
                  node.receiveShadow = true;
                }
              });
              
              truckObjectProp.scale.set(5, 5, 5);
              truckObjectProp.model = truck;
              truckObjectProp.position.set(-200, 0, -480); 
              truckObjectProp.add(truckObjectProp.model);
            });
          });
    

      //Weg voor de vrachtwagen van het magazijn
      mtlLoader.load("textures/road/tile_wideStraight.mtl", function(materials) {
        materials.preload();
        const objLoader = new THREE.OBJLoader();
        objLoader.setMaterials(materials);
        objLoader.load("textures/road/tile_wideStraight.obj", function(mesh) {
          mesh.traverse(function(node) {
            if (node instanceof THREE.Mesh) {
              node.castShadow = true;
              node.receiveShadow = true;
            }
          });
          scene.add(mesh);
          mesh.position.set(260, 0.2, 15);
          mesh.rotation.y = Math.PI / 2;
          mesh.scale.set(5, 1, 480);
        });
      });
      
      //Weg voor de vrachtwagen prop
      mtlLoader.load("textures/road/tile_wideStraight.mtl", function(materials) {
        materials.preload();
        const objLoader = new THREE.OBJLoader();
        objLoader.setMaterials(materials);
        objLoader.load("textures/road/tile_wideStraight.obj", function(mesh) {
          mesh.traverse(function(node) {
            if (node instanceof THREE.Mesh) {
              node.castShadow = true;
              node.receiveShadow = true;
            }
          });
          scene.add(mesh);
          mesh.position.set(-200, 0.2, 0);
          mesh.scale.set(5, 1, 1000);
        });
      });
  }
  //Socket, wereld aanmaken en animate loop
  socket();
  init();
  animate();
};
