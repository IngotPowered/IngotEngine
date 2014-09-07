package com.ingotpowered.world;

public class WorldThread extends Thread {

    public IngotWorld ingotWorld;

    public WorldThread(IngotWorld ingotWorld) {
        this.ingotWorld = ingotWorld;
    }


}
