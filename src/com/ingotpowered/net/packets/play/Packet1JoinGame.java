package com.ingotpowered.net.packets.play;

import com.ingotpowered.api.definitions.Difficulty;
import com.ingotpowered.api.definitions.Dimension;
import com.ingotpowered.api.definitions.GameMode;
import com.ingotpowered.api.definitions.LevelType;
import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet1JoinGame extends Packet {

    public int entityId;
    public byte gameMode;
    public byte dimension;
    public byte difficulty;
    public byte maxPlayers;
    public String levelType;
    public boolean reducedDebugInfo;

    public Packet1JoinGame(int entityId, GameMode gameMode, Dimension dimension, Difficulty difficulty, int maxPlayers, LevelType levelType, boolean reducedDebugInfo) {
        this.entityId = entityId;
        switch (gameMode) {
            case SURVIVAL:
                this.gameMode = 0; break;
            case CREATIVE:
                this.gameMode = 1; break;
            case ADVENTURE:
                this.gameMode = 2; break;
        }
        switch (dimension) {
            case NETHER:
                this.dimension = -1; break;
            case OVERWORLD:
                this.dimension = 0; break;
            case END:
                this.dimension = 1; break;
        }
        switch (difficulty) {
            case PEACEFUL:
                this.difficulty = 0; break;
            case EASY:
                this.difficulty = 1; break;
            case NORMAL:
                this.difficulty = 2; break;
            case HARD:
                this.difficulty = 3; break;
        }
        this.maxPlayers = (byte) Math.min(maxPlayers, 80);
        this.levelType = levelType.toString();
        this.reducedDebugInfo = reducedDebugInfo;
    }

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 1);
        out.writeInt(entityId);
        out.writeByte(gameMode);
        out.writeByte(dimension);
        out.writeByte(difficulty);
        out.writeByte(maxPlayers);
        PacketConstants.writeString(out, levelType);
        out.writeBoolean(reducedDebugInfo);
    }

    public void handle(PacketHandler handler) { }
}
