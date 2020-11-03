function parseCommand(input = "") {
    return JSON.parse(input);
}

var socket;

window.onload = function () {
    var camera, scene, renderer;
    var cameraControls;

    var worldObjects = {};

    function init() {
        camera = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 200);
        cameraControls = new THREE.OrbitControls(camera);
        cameraControls.enableDamping = true; // an animation loop is required when either damping or auto-rotation are enabled
        cameraControls.dampingFactor = 0.05;

        cameraControls.screenSpacePanning = false;

        cameraControls.minDistance = 0;
        cameraControls.maxDistance = 100;

        cameraControls.maxPolarAngle = Math.PI / 2.1;
        cameraControls.rotateSpeed = 0.04;
        cameraControls.panSpeed = 0.04;
        camera.position.z = 15;
        camera.position.y = 5;
        camera.position.x = 15;
        cameraControls.update();
        scene = new THREE.Scene();

        renderer = new THREE.WebGLRenderer({ antialias: true });
        renderer.setPixelRatio(window.devicePixelRatio);
        renderer.setSize(window.innerWidth, window.innerHeight + 5);
        document.body.appendChild(renderer.domElement);

        window.addEventListener('resize', onWindowResize, false);

        /*
        var geometry = new THREE.PlaneGeometry(200, 200, 32);
        var material = new THREE.MeshBasicMaterial({ color: 0xffffff, side: THREE.DoubleSide });
        var plane = new THREE.Mesh(geometry, material);
        plane.rotation.x = Math.PI / 2.0;
        plane.position.x = 15;
        plane.position.z = 15;
        scene.add(plane);
        */
        
        var light = new THREE.AmbientLight(0x404040);
        light.intensity = 4;
        scene.add(light);
    
        var pointlight = new THREE.PointLight(0xff0000, 1, 100)
        light.position.set( 50, 50, 50 );
        scene.add(light);
        
        var mtlLoader = new THREE.MTLLoader();
        
     
        
        /*
        //gebouw
        var geometry = new THREE.PlaneGeometry(17, 10);
        var material = new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader().load("textures/stone.jpg"), side: THREE.DoubleSide });
        var listbuilding = [[20,7.5, 0], [20, 22.5, 0]]
        for (var i = 0; i < listbuilding.length; i++) {
            var plane = new THREE.Mesh(geometry, material);
            
            plane.position.x = listbuilding[i][0];
            plane.position.y = 4.5;
            plane.position.z = listbuilding[i][1];
            
            //plane.rotation.x += Math.PI / listbuilding[i][2]
            scene.add(plane);
        }
        
        var geometry = new THREE.PlaneGeometry(15, 10);
        var listbuildingver = [[11.5,15], [28.5, 15, 0]]
        for (var i = 0; i < listbuildingver.length; i++) {
            var plane = new THREE.Mesh(geometry, material);
            
            plane.position.x = listbuildingver[i][0];
            plane.position.y = 4.5;
            plane.position.z = listbuildingver[i][1];
            
            plane.rotation.y += Math.PI / 2
            scene.add(plane);
        }
        */
        //bovenkant
        //var geometry = new THREE.PlaneGeometry(17, 15);
        //var plane = new THREE.Mesh(geometry, material);
        //
        //plane.position.x = 20;
        //plane.position.y = 9.5;
        //plane.position.z = 15;
        //
        //plane.rotation.x += Math.PI / 2
        //scene.add(plane);

        //road
        
        mtlLoader.load("textures/road/tile_wideStraight.mtl", function(materials) {
            materials.preload();
            const objLoader = new THREE.OBJLoader();
            objLoader.setMaterials(materials);
            objLoader.load("textures/road/tile_wideStraight.obj", function(mesh){
                
                mesh.traverse(function(node) {
                    if (node instanceof THREE.Mesh) {
                        node.castShadow = true;
                        node.receiveShadow = true;
                    }
                })
                
                scene.add(mesh);
                mesh.position.set(80, 0.2, 15);
                mesh.rotation.y = Math.PI / 2;
                mesh.scale.set(5,1,100);
                
            });
        });
        
        const loader = new THREE.CubeTextureLoader();
        //dust == daytime
        //Divine == nighttime
        const texture = loader.load([
            'textures/skybox/dust_ft.jpg',
            'textures/skybox/dust_bk.jpg',
            'textures/skybox/dust_up.jpg',
            'textures/skybox/dust_dn.jpg',
            'textures/skybox/dust_rt.jpg',
            'textures/skybox/dust_lf.jpg'
        ]);
        scene.background = texture;
        
        var tiles = ["tile_tree", "tile_treeDouble", "tile_treeQuad"];
        
        var tile = "tile";
        mtlLoader.load("textures/tiles/" + tile + ".mtl", function(materials) {

            materials.preload();
            const objLoader = new THREE.OBJLoader();
            objLoader.setMaterials(materials);
            objLoader.load("textures/tiles/" + tile + ".obj", function(mesh){
                
                mesh.traverse(function(node) {
                    if (node instanceof THREE.Mesh) {
                        node.castShadow = true;
                        node.receiveShadow = true;
                    }
                })
                                                
                scene.add(mesh);
                mesh.position.x = 0;
                mesh.position.y = -.2;
                mesh.position.z = 0;
                mesh.scale.set(1000, 1, 1000)
                
            });
        });
    }
    
    function onWindowResize() {
        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();
        renderer.setSize(window.innerWidth, window.innerHeight);
    }

    function animate() {
        requestAnimationFrame(animate);
        cameraControls.update();
        renderer.render(scene, camera);
    }

    /*
     * Hier wordt de socketcommunicatie geregeld. Er wordt een nieuwe websocket aangemaakt voor het webadres dat we in
     * de server geconfigureerd hebben als connectiepunt (/connectToSimulation). Op de socket wordt een .onmessage
     * functie geregistreerd waarmee binnenkomende berichten worden afgehandeld.
     */
    socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/connectToSimulation");
    socket.onmessage = function (event) {
        //Hier wordt het commando dat vanuit de server wordt gegeven uit elkaar gehaald
        var command = parseCommand(event.data);

        //Wanneer het commando is "object_update", dan wordt deze code uitgevoerd. Bekijk ook de servercode om dit goed te begrijpen.
        if (command.command == "object_update") {
            var mtlLoader = new THREE.MTLLoader();
            //Wanneer het object dat moet worden geupdate nog niet bestaat (komt niet voor in de lijst met worldObjects op de client),
            //dan wordt het 3D model eerst aangemaakt in de 3D wereld.
            if (Object.keys(worldObjects).indexOf(command.parameters.uuid) < 0) {
                //Wanneer het object een robot is, wordt de code hieronder uitgevoerd.
                   if (command.parameters.type == "dock") {
                    var geometry = new THREE.PlaneGeometry(1, 1);
                    var material = new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader().load("textures/stone.jpg"), side: THREE.DoubleSide });
                    var plane = new THREE.Mesh(geometry, material);

                    plane.rotation.x = Math.PI / 2;
                    var group = new THREE.Group();
                    group.add(plane);

                    scene.add(group);
                    worldObjects[command.parameters.uuid] = group;

                }
                
                if (command.parameters.type == "robot") {
                    var geometry = new THREE.BoxGeometry(0.9, 0.3, 0.9);
                    var cubeMaterials = [
                        new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader().load("textures/robot/robot_side.png"), side: THREE.DoubleSide }), //LEFT
                        new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader().load("textures/robot/robot_side.png"), side: THREE.DoubleSide }), //RIGHT
                        new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader().load("textures/robot/robot_top.png"), side: THREE.DoubleSide }), //TOP
                        new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader().load("textures/robot/robot_bottom.png"), side: THREE.DoubleSide }), //BOTTOM
                        new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader().load("textures/robot/robot_front.png"), side: THREE.DoubleSide }), //FRONT
                        new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader().load("textures/robot/robot_front.png"), side: THREE.DoubleSide }), //BACK
                    ];

                    var material = new THREE.MeshFaceMaterial(cubeMaterials);
                    var robot = new THREE.Mesh(geometry, material);
                    robot.position.y = 0.15;

                    var group = new THREE.Group();
                    group.add(robot);

                    scene.add(group);
                    worldObjects[command.parameters.uuid] = group;
                }

                if (command.parameters.type == "box") {
                    var BoxGeometry = new THREE.BoxGeometry(0.9,0.9,0.9)
                    var BoxMaterials = new THREE.MeshPhongMaterial({
                        color:0xffffff,
                        map: new THREE.TextureLoader().load("textures/crate/crate1_diffuse.png"),
                        bumpMap: new THREE.TextureLoader().load("textures/crate/crate1_bump.png"),
                    })
                    
                    crate = new THREE.Mesh(BoxGeometry, BoxMaterials);
                      
                    
                    crate.castShadow = true;
                    crate.receiveShadow = true;
                    scene.add(crate);
                    
                    worldObjects[command.parameters.uuid] = crate;
                }
                
                if (command.parameters.type == "robotpath") {
                    var geometry = new THREE.PlaneGeometry(1, 1);
                    var material = new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader().load("textures/concrete.jpeg"), side: THREE.DoubleSide });
                    var plane = new THREE.Mesh(geometry, material);

                    plane.rotation.x = Math.PI / 2
                    var group = new THREE.Group();
                    group.add(plane);

                    scene.add(group);
                    worldObjects[command.parameters.uuid] = group;
                }


            
            if (command.parameters.type == "truck") {
                var truckObject = new THREE.Object3D();
                scene.add(truckObject);
                worldObjects[command.parameters.uuid] = truckObject;
                mtlLoader.load("textures/truck/delivery.mtl", function(materials) {
                    materials.preload();
                    const objLoader = new THREE.OBJLoader();
                    objLoader.setMaterials(materials);
                    objLoader.load("textures/truck/delivery.obj", function(truck){
                        
                        truck.traverse(function(node) {
                            if (node instanceof THREE.Mesh) {
                                node.castShadow = true;
                                node.receiveShadow = true;
                            }
                        })
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
        
    }

    init();
    animate();
}