package com.nhlstenden.amazonsimulatie.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.nhlstenden.amazonsimulatie.models.Box.StateBox;

/*
 * Deze class is een versie van het model van de simulatie. In dit geval is het
 * de 3D wereld die we willen modelleren (magazijn). De zogenaamde domain-logic,
 * de logica die van toepassing is op het domein dat de applicatie modelleerd, staat
 * in het model. Dit betekent dus de logica die het magazijn simuleert.
 */
public class World implements Model, PropertyChangeListener {

    // Hier wordt de layout ingeladen.
    Layout layoutObj = new Layout();
    int[][] layout = layoutObj.Getlayout();

    // Hier worden alle lijsten aangemaakt waar alle objecten in komen te staan .
    private List<Object3D> worldObjects;
    private List<Dock> docks;
    private List<Robot> robots;
    private List<Box> boxes;
    private List<RobotPath> robotpaths;
    private List<RobotTask> robottasks;
    private List<Storageplace> storageplaces;
    private List<SleepPlace> sleepplaces;
    private Truck truck;

    // Hier wordt een shortestpath instantie aangemaakt, hiermee kunnen we het
    // kortste pad opvragen.
    private Shortestpath shortestpath = new Shortestpath();

    // Hier wordt een animator instantie aangemaakt, deze kan van het pad een
    // animatie maken die de robot kan uitvoeren.
    private Animator animator = new Animator(5);

    /*
     * Dit onderdeel is nodig om veranderingen in het model te kunnen doorgeven aan
     * de controller. Het systeem werkt al as-is, dus dit hoeft niet aangepast te
     * worden.
     */
    PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /*
     * De wereld maakt een lege lijst voor worldObjects aan. Daarin wordt nu één
     * robot gestopt. Deze methode moet uitgebreid worden zodat alle objecten van de
     * 3D wereld hier worden gemaakt.
     */
    public World() {

        // Hier wordt bepaald dat de lijsten een arraylist zijn.
        this.docks = new ArrayList<>();
        this.robots = new ArrayList<>();
        this.boxes = new ArrayList<>();
        this.robotpaths = new ArrayList<>();
        this.robottasks = new ArrayList<>();
        this.storageplaces = new ArrayList<>();
        this.sleepplaces = new ArrayList<>();
        this.truck = new Truck(103.5, 0, 15);
        this.worldObjects = new ArrayList<>();

        // Hier wordt de property change listener toegevoegd aan de truck hiermee kan de
        // truck met de wereld communiceren.
        truck.addPropertyChangeListener(this);

        // Hier wordt de layout uitgelezen en word op elke positie de nodige objecten
        // gezet.
        for (int i = 0; i < layoutObj.getRows(); i++) {
            for (int j = 0; j < layoutObj.getCol(); j++) {

                // Hier worden alle robot paden toegevoegd.
                if (layout[i][j] == 1) {
                    this.robotpaths.add(new RobotPath(i, j, 1.87));
                }

                // Hier worden alle robots toegevoegd op een robotpad.
                if (layout[i][j] == 2) {
                    Robot robot = new Robot(i, j, 1.87);
                    this.robots.add(robot);
                    this.robotpaths.add(new RobotPath(i, j, 1.87));
                    robot.addPropertyChangeListener(this);
                }

                // Hier worden alle plaatsen toegevoegd waar de robots kisten op kunnen bewaren.
                if (layout[i][j] == 3) {
                    Storageplace storageplace = new Storageplace(i, j, 1.87);
                    storageplaces.add(storageplace);
                }

                // Hier worden alle plaatsen toegevoegd waar de robot de doos in de vrachtwagen
                // kan afleveren.
                if (layout[i][j] == 4) {
                    Dock dock = new Dock(i, j, 1.87);
                    docks.add(dock);
                }

                // Hier worden alle plaatsen toegevoegd waar een doos van te voren word
                // ingespawned, hij plaatst deze op een opslagplaats.
                if (layout[i][j] == 5) {
                    Storageplace storageplace = new Storageplace(i, j, 1.87);
                    Box box = new Box(i, j, 2.37);
                    box.setStateBox(StateBox.OLD);
                    storageplace.FillPlace(box);
                    boxes.add(box);
                    storageplaces.add(storageplace);
                }

                // Hier worden alle plaatsen toegevoegd waar de robot naar toe kan gaan als hij
                // geen taken meer heeft.
                if (layout[i][j] == 7) {
                    SleepPlace sleepplace = new SleepPlace(i, j, 1.87);
                    sleepplaces.add(sleepplace);
                }
            }
        }
        // Hier worden alle objecten die een texture hebben toegevoegd aan worldobjects.
        this.worldObjects.addAll(this.robots);
        this.worldObjects.addAll(this.robotpaths);
        this.worldObjects.addAll(this.docks);
        this.worldObjects.addAll(this.boxes);
        this.worldObjects.add(this.truck);

    }

    // Dit is de functie die door de truck en de robots aangeroepen word als de
    // staat veranderd, daarna worden dan weer andere functies aangeroepen die weer
    // in verband staan met de staat van het object.
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "Robot") {

            // hier worden de object en de staat opgeslagen die meegestuurd zijn met het
            // propertychange event.
            int i = (int) evt.getNewValue();
            Robot robot = (Robot) evt.getSource();

            // Als de robot in staat 2 komt start hij een nieuwe taak.
            if (i == 2) {
                StartNewTask(robot);
            }
            // Als de robot in staat 3 komt is de robot bij de doos en wordt de functie
            // aangeroepen om de doos op te pakken en naar zijn eindbestemming te brengen.
            if (i == 3) {
                Pickupbox(robot);
            }
            // Als de robot in staat 4 komt is hij bij de eindbestemming en word de functie
            // aangeroepen om de doos neer te zetten.
            if (i == 4) {
                Putdownbox(robot);
            }
            if (i == 5) {
                // Als er nog taken zijn wordt de robot in een staat gebracht zodat hij weer een
                // nieuwe taak start, als dat niet het geval is wordt de robot naar een
                // rustplaats gestuurd en in staat 0 gezet.
                if (robottasks.size() != 0) {
                    robot.UpdateState(1);
                } else {
                    Gotosleepplace(robot);
                    robot.UpdateState(0);
                }
            }
        }

        // Hier worden de propertychange events van de truck geregeld
        if (evt.getPropertyName() == "Truck") {

            int i = (int) evt.getNewValue();

            // Als de robot bij het dock is gearriveerd worden deze functies uitgevoerd. Hij
            // spawnt eerst dozen in de truck, daarna voegt hij taken toe aan de taken lijst
            // van de robot om een bepaald aantal dozen terug te brengen, daarna activeert
            // hij alle robots die in slaapstand staan. Dan wordt de truck in slaapstand
            // gezet en wacht hij tot de truck is geleegd en gevult.
            if (i == 3) {
                SpawnBoxesonDock(8);
                BringBoxestodock(8);
                StartRobots();
                truck.UpdateState(0);
            }

            // Als de truck in deze staat komt zijn de dozen in de truck en zijn de robots
            // uit de truck, daarna worden alle dozen verwijderd uit de truck.
            if (i == 4) {
                EmptyDock();
            }
        }

    }

    // Dit is de functie om een nieuwe taak aan een robot toe te voegen en om de
    // robot naar deze doos toe te lopen.
    public void StartNewTask(Robot robot) {

        // Hier wordt de eerste taak uit de robottaken gehaald en word de positie van de
        // robot opgehaald.
        RobotTask task = robottasks.remove(0);
        Position robotposition = robot.getPosition();

        // Als de robot uit slaapstand komt word de slaapplek geleegd en verwijderd uit
        // de robot, hierdoor komt hij weer vrij voor ander robots.
        if (robot.getSleepplace() != null) {
            robot.getSleepplace().EmptySleepplace();
            robot.setSleepplace(null);
        }

        // Hier wordt de doos uit de taak gehaald die moet worden verplaatst, hij wordt
        // daarna ook gezet zodat die is opgepakt.
        Box box = task.Getbox();
        box.setTaken(true);

        // Hier wordt de positie van de doos opgehaald.
        double boxX = box.getX();
        double boxZ = box.getZ();

        // Hier wordt het kortste pad bepaald van de robot naar de doos.
        List<int[]> path = shortestpath.getShortestpath(layout, (int) robotposition.getX(), (int) robotposition.getZ(),
                (int) boxX, (int) boxZ);
        // Hier wordt de animatie opgehaald.
        List<Position> newpositions = animator.GetAnimation(path, robotposition);

        // Hier wordt de taak toegevoegd aan de robot en wordt de animatie voor het
        // lopen naar de doos doorgegeven.
        robot.SetTask(task);
        robot.FeedPositions(newpositions);
    }

    // Dit is de functie die wordt aan geroepen wanneer de robot bij de doos is en
    // de doos opgepakt moet worden.
    public void Pickupbox(Robot robot) {

        // Hier worden de taak, doos die opgepakt moet worden, positie van de doos en de
        // positie van de robot opgehaald.
        RobotTask task = robot.GetTask();
        Box box = task.Getbox();
        Boxplace currentplace = GetPlacewithbox(box);
        Position robotposition = robot.getPosition();

        // Hier wordt de opslagplaats van de doos leeg gehaald.
        currentplace.EmptyPlace();

        Boxplace storagelocation;

        // Hier wordt een lege plek opgezocht om de doos op neer te zetten.
        // 0 is voor opslagplaasen, 1 is voor de plaatsen in de truck.
        if (task.IsNewBox()) {
            storagelocation = GetEmptyplace(0);
        } else {
            storagelocation = GetEmptyplace(1);
        }
        if (storagelocation == null) {
            return;
        }
        // Hier wordt de lege pek opgevult
        storagelocation.FillPlace(box);

        // Hier word het pad en animatie opgehaald om naar de eindlocatie te gaan.
        List<int[]> path = shortestpath.getShortestpath(layout, (int) robotposition.getX(), (int) robotposition.getZ(),
                (int) storagelocation.getX(), (int) storagelocation.getZ());
        List<Position> robotpositions = animator.GetAnimation(path, robotposition);

        // Hier word de animatie aangepast voor de doos omdat die 0.8 hoger moet staan
        // dan de robot.
        List<Position> boxpositions = new ArrayList<>();
        for (Position i : robotpositions) {
            boxpositions.add(new Position(i.getX(), i.getZ(), i.getY() + 0.8, i.getrotationX(), i.getrotationZ(),
                    i.getrotationY()));
        }

        // Hier worden de animaties doorgegeven aan de doos en de robot
        box.FeedPositions(boxpositions);
        robot.FeedPositions(robotpositions);

    }

    // Dit is de functie om de doos neer te zetten op een opslaglocatie
    public void Putdownbox(Robot robot) {

        // Hier worden de taak en de doos opgehaald.
        RobotTask task = robot.GetTask();
        Box box = task.Getbox();

        // Hier wordt de robot weer vrij gegeven voor ander robots om opgepakt te worden
        box.setTaken(false);

        // Hier wordt als de doos nieuw uit de vrachtwagen aangepast dat de doos oud is
        // wanneer die wordt neergezet, hierdoor het programma dat de dozen die op het
        // dock staan uit het magazijn komen
        if (box.getStateBox() == StateBox.NEW)
            box.setStateBox(StateBox.OLD);

        // Hier wordt de positie bepaald waar de robot moet staan.
        Position endposition = new Position(robot.getX(), robot.getZ(), robot.getY() + 0.35, robot.getRotationX(),
                robot.getRotationZ(), robot.getRotationY());

        // Hier wordt de positie door gegeven aan de doos zodat hij op de opslagplaats
        // gaat staan.
        List<Position> list = new ArrayList<>();
        list.add(endposition);
        box.FeedPositions(list);

        // Hier wordt de taak uit de robot geleegd omdat hij klaar is met zijn taak
        robot.SetTask(null);
    }

    // Dit is de functie die een plaats kan opzoeken voor de robot om een doos op
    // neer te zetten, meegegeven word een integer die als hij 0 is een vrije
    // opslagplaats in het magazijn opzoekt en 1 is als hij een vrijge plaats moet
    // zoeken in de truck.
    public Boxplace GetEmptyplace(int k) {

        // Dit is de lijst waar alle lege plaatsen in worden gezet.
        List<Boxplace> emptyplaces = new ArrayList<Boxplace>();

        // Hier wordt gelust door de opslagplaatsen en de lege plaatsen worden aan
        // emptyplaces toegevoegd.
        if (k == 0) {
            for (Boxplace i : storageplaces) {
                if (i.IsEmpty())
                    emptyplaces.add(i);
            }
        }

        if (k == 1) {
            for (Boxplace i : docks) {
                if (i.IsEmpty())
                    emptyplaces.add(i);
            }
        }

        // Als er geen lege opslagplaats kan worden gevonden geeft de functie niks
        // terug.
        if (emptyplaces.size() == 0) {
            return null;
        }

        // Hier wordt een random plaats opgezocht in de lege plekken lijst.
        Random random = new Random();
        int randomplace = random.nextInt(emptyplaces.size());

        // Hier wordt de lege plaats terug gegeven
        return emptyplaces.get(randomplace);
    }

    // Dit is de functie die de opslagplaats van de doos terug geeft.
    public Boxplace GetPlacewithbox(Box box) {

        // dit is de variable waar de opslagplaats in wordt opgeslagen.
        Boxplace place = null;

        // Hier wordt gelust door de plaatsen in het magazijn en de truck om te zoeken
        // naar de opslagplaats waar de doos op staat.
        for (Boxplace i : storageplaces) {
            if (i.GetBox() == box) {
                place = i;
            }
        }
        for (Boxplace i : docks) {
            if (i.GetBox() == box) {
                place = i;
            }
        }
        // Hier wordt de plek terug gegeven waar de doos op staat .
        return place;
    }

    // Dit is de functie die de robot naar een lege slaapplek stuurt.
    public void Gotosleepplace(Robot robot) {

        // Hier wordt de huidige positie van de robot opgehaald.
        Position robotpos = robot.getPosition();

        // Hier komt de slaapplek van de robot in te staan en wordt de lijst aangemaakt
        // waar alle lege slaapplekken in komen te staan.
        SleepPlace sleepplace;
        List<SleepPlace> emptySleepplace = new ArrayList<>();

        // Hier worden alle lege slaaplekken opgehaald
        for (SleepPlace i : sleepplaces) {
            if (i.getTaken() == false)
                emptySleepplace.add(i);
        }
        if (emptySleepplace.size() == 0) {
            return;
        }
        // Hier wordt een random lege slaapplek bepaald.
        Random random = new Random();
        int randomplace = random.nextInt(emptySleepplace.size());
        sleepplace = emptySleepplace.get(randomplace);

        // Hier wordt het pad en animatie opgehaald om naar de slaapplek te gaan.
        List<int[]> list = shortestpath.getShortestpath(layout, (int) robotpos.getX(), (int) robotpos.getZ(),
                (int) sleepplace.getX(), (int) sleepplace.getZ());
        List<Position> positions = animator.GetAnimation(list, robotpos);

        // Hier wordt de slaapplek waar de robot opgaat rusten op bezet gezet en de plek
        // toegevoegd aan de robot.
        sleepplace.Usesleepplace();
        robot.setSleepplace(sleepplace);

        // Hier wordt de animatie toegestuurd naar de robot om naar de slaapplek te
        // gaan.
        robot.FeedPositions(positions);
    }

    // Dit is de functie om dozen in de truck te spawnen.
    public void SpawnBoxesonDock(int amount) {
        System.out.println("Spawning boxes");

        for (int i = 0; i < amount; i++) {
            // hier wordt een lege plek in de truck opgehaald
            Boxplace dock = GetEmptyplace(1);
            if (dock != null) {

                // Hier worden de nieuwe dozen aangemaakt en op een dock gezet en toegevoegd aan
                // worldobjects.
                Box box = new Box(dock.getX(), dock.getZ(), dock.getY() + 0.5);
                dock.FillPlace(box);
                boxes.add(box);
                this.worldObjects.add(box);

                // Hier worden de taken toegevoegd om de doos in het magazijn te zetten.
                robottasks.add(new RobotTask(box, true));
            }
        }
    }

    // Dit is de functie om taken toe te voegen om de dozen in het magazijn naar de
    // truck te brengen.
    public void BringBoxestodock(int amount) {
        // Dit is de lijst waar alle dozen in komen te staan die in het magazijn staan
        // en nog niet opgepakt zijn.
        List<Box> boxesnottaken = new ArrayList<>();
        // Hier worden alle dozen opgehaald die in het magazijn staan en toegevoegd aan
        // de lijst.
        for (Box i : boxes) {
            if (i.getTaken() == false && i.stateBox == StateBox.OLD) {
                boxesnottaken.add(i);
            }
        }

        // Hier worden de taken toegevoegd zodat de robots dozen uit het magazijn halen.
        for (int i = 0; i < amount; i++) {
            if (boxesnottaken.size() == 0)
                return;
            // Hier wordt een random doos bepaald om op te halen.
            Random random = new Random();
            int randomplace = random.nextInt(boxesnottaken.size());

            // Hier wordt een nieuwe taak aangemaakt om een doos op te halen
            robottasks.add(new RobotTask(boxesnottaken.remove(randomplace), false));
        }
    }

    // Deze functie geeft het aantal plekken terug waar een doos op staat die al
    // eens in het magazijn heeft gestaan.
    public int CountFullDocks() {
        int k = 0;

        // Hier wordt gelust door alle docks en bepaald of hij vol is en of er een doos
        // op staat die uit het magazijn komt dan verhoogt hij k met 1.
        for (Dock i : docks) {
            if (!i.IsEmpty()) {
                Box box = i.GetBox();
                if (box.getStateBox() == StateBox.OLD && box.getTaken() == false) {
                    k++;
                }
            }
        }
        // geeft de waarde terug hoeveel dozen afgeleverd zijn in de truck.
        return k;
    }

    // Deze functie verwijderd alle dozen uit de truck
    public void EmptyDock() {
        for (Dock i : docks) {
            if (!i.IsEmpty()) {
                Box box = i.GetBox();
                if (box.getStateBox() == StateBox.OLD) {
                    // Zorgt er voor dat de doos op een plek komt waar hij niet meer kan worden
                    // gezien, zo lijkt het voor ons alsof hij verwijderd is.
                    box.launch();
                    // verwijderd de doos uit de lijst met dozen en maakt de opslagplek vrij.
                    this.boxes.remove(box);
                    i.EmptyPlace();
                }
            }
        }
    }

    // Deze functie activeert alle Robots die op ruststand staan in een staat waar
    // ze naar nieuwe taken gaan zoeken.
    public void StartRobots() {
        for (Robot i : robots) {
            if (i.getState() == 0) {
                // Als de robot in beweging is om naar een rustplek te gaan en ondertussen
                // worden er nieuwe taken toegevoegd wordt de taak afgebroken om naar het
                // rustplek te gaan.
                i.StopActions();
                i.UpdateState(5);
            }
        }
    }

    /*
     * Deze methode wordt gebruikt om de wereld te updaten. Wanneer deze methode
     * wordt aangeroepen, wordt op elk object in de wereld de methode update
     * aangeroepen. Wanneer deze true teruggeeft betekent dit dat het onderdeel
     * daadwerkelijk geupdate is (er is iets veranderd, zoals een positie). Als dit
     * zo is moet dit worden geupdate, en wordt er via het pcs systeem een
     * notificatie gestuurd naar de controller die luisterd. Wanneer de
     * updatemethode van het onderdeel false teruggeeft, is het onderdeel niet
     * veranderd en hoeft er dus ook geen signaal naar de controller verstuurd te
     * worden.
     */
    @Override
    public void update() {
        try {
            for (Object3D object : this.worldObjects) {
                if (object instanceof Updatable) {
                    if (((Updatable) object).update()) {
                        pcs.firePropertyChange(Model.UPDATE_COMMAND, null, new ProxyObject3D(object));
                    }
                }
            }
        } catch (Exception e) {

        }
        // Hier wordt bepaald hoeveel robots er in de truck staan.
        int robotsintruck = 0;
        for (Robot i : robots) {
            if (i.getX() > 24) {
                robotsintruck++;
            }
        }
        // Hier wordt steeds gechecked hoeveel dozen er in de truck staan, als hij vol
        // is en alle robots zijn uit de truck gereden wordt de truck geactiveerd om de
        // dozen weg te brengen en weer nieuwe dozen op te halen
        if (CountFullDocks() >= 8 && truck.getState() == 0 && robotsintruck == 0) {
            truck.UpdateState(4);
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
     * Deze methode geeft een lijst terug van alle objecten in de wereld. De lijst
     * is echter wel van ProxyObject3D objecten, voor de veiligheid. Zo kan de
     * informatie wel worden gedeeld, maar kan er niks aangepast worden.
     */
    @Override
    public List<Object3D> getWorldObjectsAsList() {
        ArrayList<Object3D> returnList = new ArrayList<>();

        for (Object3D object : this.worldObjects) {
            returnList.add(new ProxyObject3D(object));
        }

        return returnList;
    }
}