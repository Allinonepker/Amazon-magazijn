package com.nhlstenden.amazonsimulatie.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/*
 * Deze class is een versie van het model van de simulatie. In dit geval is het
 * de 3D wereld die we willen modelleren (magazijn). De zogenaamde domain-logic,
 * de logica die van toepassing is op het domein dat de applicatie modelleerd, staat
 * in het model. Dit betekent dus de logica die het magazijn simuleert.
 */
public class World implements Model {
    /*
     * De wereld bestaat uit objecten, vandaar de naam worldObjects. Dit is een lijst
     * van alle objecten in de 3D wereld. Deze objecten zijn in deze voorbeeldcode alleen
     * nog robots. Er zijn ook nog meer andere objecten die ook in de wereld voor kunnen komen.
     * Deze objecten moeten uiteindelijk ook in de lijst passen (overerving). Daarom is dit
     * een lijst van Object3D onderdelen. Deze kunnen in principe alles zijn. (Robots, vrachrtwagens, etc)
     */

	
    private List<Object3D> worldObjects;
    private List<Dock> docks;
    private List<Robot> robots;
    private List<Box> boxes;
    private List<RobotPath> robotpaths;
    private Truck truck;
    
    /*
     * Dit onderdeel is nodig om veranderingen in het model te kunnen doorgeven aan de controller.
     * Het systeem werkt al as-is, dus dit hoeft niet aangepast te worden.
     */
    PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /*
     * De wereld maakt een lege lijst voor worldObjects aan. Daarin wordt nu één robot gestopt.
     * Deze methode moet uitgebreid worden zodat alle objecten van de 3D wereld hier worden gemaakt.
     */
    public World() {

    	this.docks = new ArrayList<Dock>();
    	this.robots = new ArrayList<Robot>();
    	this.boxes = new ArrayList<Box>();
    	this.robotpaths = new ArrayList<RobotPath>();
    	
    	
    	int[][] layout = new Layout().Getlayout();
    	
    	
        this.worldObjects = new ArrayList<>();
        
       
        this.truck = new Truck(100, 0, 15);
        
        
        for (int i = 0; i < layout[0].length; i++) {
            for (int j = 0; j < layout[1].length; j++)  {
            	if (layout[i][j] == 1) 
            		this.robotpaths.add(new RobotPath(i,j,0.1));
            	
            	if (layout[i][j] == 2) {
                	this.robots.add(new Robot(i,j,0.15));
                	this.robotpaths.add(new RobotPath(i,j,0.1));
            	}

            	if (layout[i][j] == 3) 
                	this.boxes.add(new Box(i,j,0.5));
            	
            	if (layout[i][j] == 4) 
                	this.docks.add(new Dock(i,j,0.1));	
            }
        }
        
        this.worldObjects.addAll(this.robots);
        this.worldObjects.addAll(this.robotpaths);
        this.worldObjects.addAll(this.docks);
        this.worldObjects.addAll(this.boxes);
        this.worldObjects.add(this.truck);
        
        
    }

    /*
     * Deze methode wordt gebruikt om de wereld te updaten. Wanneer deze methode wordt aangeroepen,
     * wordt op elk object in de wereld de methode update aangeroepen. Wanneer deze true teruggeeft
     * betekent dit dat het onderdeel daadwerkelijk geupdate is (er is iets veranderd, zoals een positie).
     * Als dit zo is moet dit worden geupdate, en wordt er via het pcs systeem een notificatie gestuurd
     * naar de controller die luisterd. Wanneer de updatemethode van het onderdeel false teruggeeft,
     * is het onderdeel niet veranderd en hoeft er dus ook geen signaal naar de controller verstuurd
     * te worden.
     */
    @Override
    public void update() {
		if (this.truck.standingStill() == true && this.truck.getSpawnedBoxes() == false) {
    			spawnBoxes(this.truck);
		}
		
//		for (Object3D box : this.boxes) {
//			if (((Box) box).getToTruck() == true) {
//				this.boxes.remove(box);
//				this.worldObjects.remove(box);
//			}
//		}
		
        for (Object3D object : this.worldObjects) {
            if(object instanceof Updatable) {
                if (((Updatable)object).update()) {
                    pcs.firePropertyChange(Model.UPDATE_COMMAND, null, new ProxyObject3D(object));
                }
            }
        }
    }

    public void spawnBoxes(Object3D objectTruck) {
    	for (Object3D objectDock : this.docks) {
    				this.worldObjects.add(new Box(objectDock.getX(), objectDock.getZ(), 0.5));
    			}
    	this.truck.setSpawnedBoxes(true);
		}
	
	
    
    /*
     * Standaardfunctionaliteit. Hoeft niet gewijzigd te worden.
     */
    @Override
    public void addObserver(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    
    /*
     * Deze methode geeft een lijst terug van alle objecten in de wereld. De lijst is echter wel
     * van ProxyObject3D objecten, voor de veiligheid. Zo kan de informatie wel worden gedeeld, maar
     * kan er niks aangepast worden.
     */
    @Override
    public List<Object3D> getWorldObjectsAsList() {
        ArrayList<Object3D> returnList = new ArrayList<>();

        for(Object3D object : this.worldObjects) {
            returnList.add(new ProxyObject3D(object));
        }

        return returnList;
    }
}