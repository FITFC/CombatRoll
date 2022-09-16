package net.rolling.api;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.registry.Registry;
import org.checkerframework.checker.units.qual.C;

import java.util.List;

public class EntityAttributes_Rolling {
    public static final String distanceId = "rolling.distance";
    public static final EntityAttribute DISTANCE = register(distanceId, new ClampedEntityAttribute("attribute.name" + distanceId, 0, 0.0, 1024.0));
    public static final String countId = "rolling.count";
    public static final EntityAttribute COUNT = register(countId, new ClampedEntityAttribute("attribute.name" + countId, 0, 0.0, 1024.0));
    public static final String cooldownId = "rolling.cooldown";
    public static final EntityAttribute COOLDOWN = register(cooldownId, new ClampedEntityAttribute("attribute.name" + cooldownId, 0, 0.0, 1024.0));

    public static List<EntityAttribute> all;
    static {
        all = List.of(DISTANCE, COUNT, COOLDOWN);
    }

    private static EntityAttribute register(String id, EntityAttribute attribute) {
        return Registry.register(Registry.ATTRIBUTE, id, attribute);
    }
}
