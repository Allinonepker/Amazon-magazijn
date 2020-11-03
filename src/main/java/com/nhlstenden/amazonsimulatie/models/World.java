package com.nhlstenden.amazonsimulatie.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import javax.management.InstanceNotFoundException;

import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter;
import com.nhlstenden.amazonsimulatie.models.Box.StateBox;

import ch.qos.logback.core.rolling.helper.ArchiveRemover;

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
    int[][] layout = new Layout().Getlayout();
	
    private List<Object3D> worldObjects;
    private List<Dock> docks;
    private List<Robot> robots;
    private List<Box> boxes;
    private List<RobotPath> robotpaths;
    private Truck truck;
    private List<RobotTask> robottasks;
    private List<Storageplace> storageplaces;
    
    private int amountBoxesToBePickedUp = 0;
    
    private Shortestpath shortestpath = new Shortestpath();
    private Animator animator = new Animator(5);
    
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
        this.robottasks = new ArrayList<>();
        this.storageplaces = new ArrayList<>();
    	
    	int[][] layout = new Layout().Getlayout();
        this.worldObjects = new ArrayList<>();
        this.truck = new Truck(100, 0, 15);
        
        this.truck.addPropertyChangeListener(e -> {
        	System.out.println("test truck");
        	if ((boolean) e.getNewValue() == true && truck.getSpawnedBoxes() == false && truck.standingStill() == true) {
        		spawnBoxes(truck);
        	}	
        });
        
        
        
        for (int i = 0; i < layout[0].length; i++) {
            for (int j = 0; j < layout[1].length; j++)  {
            	if (layout[i][j] == 1) 
            		this.robotpaths.add(new RobotPath(i,j,0.1));
            	
            	if (layout[i][j] == 2) {
                	this.robots.add(new Robot(i,j,0.15));
                	this.robotpaths.add(new RobotPath(i,j,0.1));
            	}

            	if (layout[i][j] == 3) {
            		Box box = new Box(i,j,0.5);
                	this.boxes.add(box);
            		box.addPropertyChangeListener(e -> {
            			if (this.amountBoxesToBePickedUp > 4 && box.getStateBox() == StateBox.OLD) {
                			removeBox(box);
                			this.amountBoxesToBePickedUp -= 1;
            			}
            		});
            	}

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

    private void pickingUp() {
    	ListIterator<Box> iterator = this.boxes.listIterator();
    	while (iterator.hasNext()) {
			if (iterator.next().getStateBox() == StateBox.OLD) {
				iterator.remove();
				this.worldObjects.remove(iterator);
			}
		}
		this.truck.setToGoBack();
	}
    
    public void removeBox(Box box) {
    	System.out.println("Box removed");
    	this.worldObjects.remove(box);
    	this.boxes.remove(box);
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
    	ListIterator iterator = this.worldObjects.listIterator();
    	try {
    		System.out.println(this.amountBoxesToBePickedUp);
            for (Object3D object : this.worldObjects) {
                if(object instanceof Updatable) {
                    if (((Updatable)object).update()) {
                        pcs.firePropertyChange(Model.UPDATE_COMMAND, null, new ProxyObject3D(object));
                    }
                }
            }
    	}
    	catch (Exception e) {
			System.out.println("SAME FUCKING ERROR");
		}

    }

    
    public void StartNewTask(Robot robot){

        RobotTask task = this.robottasks.remove(0);
        Position robotposition = robot.getPosition();

        Box box = task.Getbox();

        double boxX = box.getX();
        double boxZ = box.getY();        
        
        List<int[]> path = shortestpath.getShortestpath(layout, (int)robotposition.getX(), (int)robotposition.getZ(), (int)boxX, (int)boxZ);
        List<Position> newpositions = animator.GetAnimation(path, robotposition);

        robot.ChangeState(1);
        robot.FeedQueue(newpositions);
        
        Boxplace storagelocation;

        if (task.IsNewBox()){
            storagelocation = GetEmptyplace(0);

        } 
        else {
            storagelocation = GetEmptyplace(1);
        }
        if (storagelocation != null){

        }
    }

    public Boxplace GetEmptyplace(int k){
        List<Boxplace> emptyplaces = new ArrayList<Boxplace>();
        if (k == 0){
            for(Boxplace i : storageplaces){
                if(i.IsEmpty())
                emptyplaces.add(i);
            }
        }
        
        if (k == 1){
            for(Dock i : docks){
                if(i.IsEmpty())
                emptyplaces.add(i);
            }
        }
        
        if(emptyplaces.size() == 0){
            return null;
        }

        Random random = new Random();
        int randomplace = random.nextInt(emptyplaces.size());

        return emptyplaces.get(randomplace);
    }
    
    public void spawnBoxes(Object3D objectTruck) {
    	System.out.println("Spawning boxes");
    	for (Object3D objectDock : this.docks) {
    				Box box = new Box(objectDock.getX(), objectDock.getZ(), 0.5);
    				this.boxes.add(box);
    				this.worldObjects.add(box);
            		box.addPropertyChangeListener(e -> {
            			if (e.getNewValue() == StateBox.OLD)
            				this.amountBoxesToBePickedUp += 1;
            			if (this.amountBoxesToBePickedUp > 4 && box.getStateBox() == StateBox.OLD) {
                			removeBox(box);
                			this.amountBoxesToBePickedUp -=1;
            			}
            		});
//    				box.setStateBox(StateBox.OLD);
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