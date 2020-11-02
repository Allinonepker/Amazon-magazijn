package com.nhlstenden.amazonsimulatie.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/*
 * Deze class is een versie van het model van de simulatie. In dit geval is het
 * de 3D wereld die we willen modelleren (magazijn). De zogenaamde domain-logic,
 * de logica die van toepassing is op het domein dat de applicatie modelleerd, staat
 * in het model. Dit betekent dus de logica die het magazijn simuleert.
 */
public class World implements Model, PropertyChangeListener {
    /*
     * De wereld bestaat uit objecten, vandaar de naam worldObjects. Dit is een lijst
     * van alle objecten in de 3D wereld. Deze objecten zijn in deze voorbeeldcode alleen
     * nog robots. Er zijn ook nog meer andere objecten die ook in de wereld voor kunnen komen.
     * Deze objecten moeten uiteindelijk ook in de lijst passen (overerving). Daarom is dit
     * een lijst van Object3D onderdelen. Deze kunnen in principe alles zijn. (Robots, vrachrtwagens, etc)
     */
    int[][] layout = new Layout().Getlayout();

    private List<Robot> robots;
    private List<Object3D> worldObjects;
    private List<Box> boxes;
    private List<Storageplace> storageplaces;
    private List<Dock> dockplaces;
    private List<RobotTask> robottasks;


    private Shortestpath shortestpath = new Shortestpath();
    private Animator animator = new Animator(2);

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

        this.robots = new ArrayList<>();
        this.boxes = new ArrayList<>();
        this.storageplaces = new ArrayList<>();
        this.dockplaces = new ArrayList<>();
        this.robottasks = new ArrayList<>();
        this.worldObjects = new ArrayList<>();
        
        this.worldObjects.add(new Truck(37, 0, 15));

        for (int i = 0; i < layout[0].length; i++) { 
            for (int j = 0; j < layout[1].length; j++) {
            	if (layout[i][j] == 0) {
            		this.worldObjects.add(new Tile(i,j,0));
            	}
            	if (layout[i][j] == 1) {
            		this.worldObjects.add(new RobotPath(i,j,0.1));
            	}
            	
            	if (layout[i][j] == 3) {
                    Storageplace storageplace = new Storageplace(i, j, 0.1);
                    Box box = new Box(i, j, 0.5);
                    storageplace.FillPlace(box);
                    boxes.add(box);
                    storageplaces.add(storageplace);
                	this.worldObjects.add(box);
                }
                if (layout[i][j] == 4) {
                    Dock dock = new Dock(i, j, 0.1);
                    dockplaces.add(dock);
                }
                if (layout[i][j] == 2) {
                    Robot robot = new Robot(i,j,0.15);
                    robots.add(robot);
                	this.worldObjects.add(robot);
                    this.worldObjects.add(new RobotPath(i,j,0.1)); 
                    robot.addPropertyChangeListener(this);
                   
            	}
            }
        } 

       robottasks.add(new RobotTask(boxes.get(0),false));

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "Robot"){

            int i = (int) evt.getNewValue();
            Robot robot = (Robot)evt.getSource();

            if(i == 2){

                if(robottasks.size() != 0)
                StartNewTask(robot);
                else
                robot.ChangeState(0);
            }
            if(i == 1){
                Pickupbox(robot);
            }

        }

        if (evt.getPropertyName() == "Truck"){

        }
        
    }

    public void StartNewTask(Robot robot){

        RobotTask task = robottasks.remove(0);
        Position robotposition = robot.getPosition();

        Box box = task.Getbox();

        double boxX = box.getX();
        double boxZ = box.getZ();        
        
        List<int[]> path = shortestpath.getShortestpath(layout, (int)robotposition.getX(), (int)robotposition.getZ(), (int)boxX, (int)boxZ);
        List<Position> newpositions = animator.GetAnimation(path, robotposition);

        robot.ChangeState(1);
        robot.AddTask(task);
        robot.FeedQueue(newpositions);
        
    }

        
    public void Pickupbox(Robot robot){ 
        
        RobotTask task = robot.GetTask();
        Box box = task.Getbox();
        Boxplace currentplace = GetPlacewithbox(box);
        Position robotposition = robot.getPosition();

        currentplace.EmptyPlace();
        robot.PickupBox(box);
        
        Boxplace storagelocation;

        if (task.IsNewBox())
        {
            storagelocation = GetEmptyplace(0);
        } 
        else {
            storagelocation = GetEmptyplace(1);
        }
        if (storagelocation == null){
            return;
        }
        storagelocation.FillPlace(box);

        List<int[]> path = shortestpath.getShortestpath(layout, (int)robotposition.getX(), (int)robotposition.getZ(), (int)storagelocation.getX(), (int)storagelocation.getZ());
        List<Position> newpositions = animator.GetAnimation(path, robotposition);

        robot.ChangeState(2);
        robot.FeedQueue(newpositions);
        box.FeedQueue(newpositions);
    }

    public void Putdownbox(Robot robot){

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
            for(Boxplace i : dockplaces){
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

    public Boxplace GetPlacewithbox(Box box){
        Boxplace place = null;
        for(Boxplace i : storageplaces ){
            if (i.GetBox() == box){
                place = i;
            }
        }
        for(Boxplace i : dockplaces ){
            if (i.GetBox() == box){
                place = i;
            }
        }
        return place;
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
        for (Object3D object : this.worldObjects) {
            if(object instanceof Updatable) {
                if (((Updatable)object).update()) {
                    pcs.firePropertyChange(Model.UPDATE_COMMAND, null, new ProxyObject3D(object));
                }
            }
        }
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