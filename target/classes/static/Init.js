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
                camera.position.z = 50;
                camera.position.y = 50;
                camera.position.x = 100;
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