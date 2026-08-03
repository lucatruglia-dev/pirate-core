package lucatruglia.piratecore.models;

import java.util.UUID;

public record BarrelData(
    UUID armorStandUUID,
    UUID blockDisplayUUID,
    UUID textDisplayUUID,
    String config_id
) {}