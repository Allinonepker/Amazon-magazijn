var cameraControls, camera;

function createCamera() {
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
}
