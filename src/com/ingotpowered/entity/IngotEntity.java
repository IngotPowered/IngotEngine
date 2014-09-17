package com.ingotpowered.entity;

import com.ingotpowered.api.Orientation;
import com.ingotpowered.api.Position;
import com.ingotpowered.api.entity.Entity;
import com.ingotpowered.api.entity.EntityAnimation;
import com.ingotpowered.api.entity.EntityTickManager;
import com.ingotpowered.api.entity.Rideable;
import com.ingotpowered.api.world.World;
import com.ingotpowered.world.IngotChunk;

/**
 * Created by Joe on 9/16/2014.
 */
public class IngotEntity implements Entity {

    /*
    CHANGE AT YOUR OWN RISK!
     */
    public int id;
    public double x;
    public double y;
    public double z;
    public float pitch;
    public float yaw;
    public IngotChunk chunk;
    public EntityTickManager manager;
    public boolean isAlive;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Position getPosition() {
        return new Position(x,y,z);
    }

    @Override
    public Orientation getOrientation() {
        return new Orientation(yaw,pitch);
    }

    @Override
    public void moveRelative(double x, double y, double z) {
        if(x > 4 || x < -4 || y > 4 || y < -4 || z > 4 || z < -4){
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void setOrientation(Orientation orientation) {

    }

    @Override
    public void moveRelative(double x, double y, double z, Orientation orientation) {
        if(x > 4 || x < -4 || y > 4 || y < -4 || z > 4 || z < -4){
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void teleport(Position position) {

    }

    @Override
    public void teleport(Position position, World world) {

    }

    @Override
    public void teleport(Position position, Orientation orientation) {

    }

    @Override
    public void teleport(Position position, World world, Orientation orientation) {

    }

    @Override
    public void mount(Rideable rideable) {

    }

    @Override
    public void dismount() {

    }

    @Override
    public void sendStatus(EntityAnimation animation) {

    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public EntityTickManager getTickManager() {
        return manager;
    }

    @Override
    public void setTickManager(EntityTickManager manager) {
        if(manager == null){
            throw new NullPointerException();
        }
        this.manager = manager;
    }

    public void tick(){
        try{
            manager.tick(this);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
