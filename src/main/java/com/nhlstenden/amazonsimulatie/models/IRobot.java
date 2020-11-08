package com.nhlstenden.amazonsimulatie.models;

import java.util.List;

public interface IRobot {
    public void setSleepplace(SleepPlace sleepplace);
    
    public SleepPlace getSleepplace();
    
    public void StopActions();

    // Zorgt er voor dat de robot in de volgende staat wordt gebracht deze verandering wordt ook naar de wereld verstuurt zodat de volgde acties worden uitgevoerd
    public void UpdateState(int newstate);
    
    public int getState();

    public Position getPosition();
    public void SetTask(RobotTask task);

    public RobotTask GetTask();

    // Zorgt ervoor zodat een animatie kan worden verstuurd naar de actielijst van de robot, zodat de robot een animatie kan uitvoeren.
    public void FeedPositions(List<Position> newactions);
}
