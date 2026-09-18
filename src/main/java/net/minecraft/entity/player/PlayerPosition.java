package net.minecraft.entity.player;

import java.util.Set;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPosition;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.util.math.Vec3d;

// Совместимый шим: в 1.21.11 ванильного PlayerPosition нет, логика сведена к EntityPosition.
public record PlayerPosition(Vec3d pos, Vec3d deltaMovement, float yaw, float pitch) {
    public Vec3d position() {
        return this.pos();
    }

    public static PlayerPosition fromEntity(ClientPlayerEntity entity) {
        return new PlayerPosition(
            new Vec3d(entity.getX(), entity.getY(), entity.getZ()), entity.getVelocity(), entity.getYaw(), entity.getPitch()
        );
    }

    public static PlayerPosition apply(PlayerPosition current, EntityPosition change, Set<PositionFlag> relatives) {
        EntityPosition base = new EntityPosition(current.pos(), current.deltaMovement(), current.yaw(), current.pitch());
        EntityPosition result = EntityPosition.apply(base, change, relatives);
        return new PlayerPosition(result.position(), result.deltaMovement(), result.yaw(), result.pitch());
    }
}
