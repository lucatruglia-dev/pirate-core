package lucatruglia.piratecore.models;

import java.util.UUID;

/**
 * Record immutabile che rappresenta i dati persistenti di un player
 * relativi al sistema livelli / esperienza.
 *
 * @param uuid     UUID del player
 * @param name     Nome attuale del player
 * @param totalXp  XP totali accumulate (supporta decimali)
 * @param level    Livello attuale
 */
public record PlayerData(
    UUID uuid,
    String name,
    double totalXp,
    int level
) {}
