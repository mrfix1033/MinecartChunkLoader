package mrfix1033.minecartchunkloader;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.vehicle.EntityMinecartAbstract;
import net.minecraft.world.level.entity.EntityTickList;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftMinecart;
import org.bukkit.entity.Minecart;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class main extends JavaPlugin implements CommandExecutor, Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Object obj : Bukkit.getWorld("world").getEntities().stream().filter(x -> x.getType().toString().contains("MINECART")).toArray()) {
                Minecart minecart = (Minecart) obj;
                Location loc = minecart.getLocation();
                Chunk chunk = loc.getChunk();
                int x = chunk.getX();
                int z = chunk.getZ();
                World world = chunk.getWorld();
                for (Chunk chunk1 : new Chunk[]{chunk, world.getChunkAt(x - 1, z),
                        world.getChunkAt(x + 1, z),
                        world.getChunkAt(x, z + 1),
                        world.getChunkAt(x, z - 1)}) {
                    chunk1.load(true);
                }
                EntityMinecartAbstract minecartNMS = ((CraftMinecart) minecart).getHandle();
                WorldServer worldNMS = minecartNMS.dI().getMinecraftWorld();
                EntityTickList entitiesList;
                try {
                    Field field = worldNMS.getClass().getDeclaredField("L");
                    field.setAccessible(true);
                    entitiesList = (EntityTickList) field.get(worldNMS);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                    return;
                }
                boolean flag = entitiesList.c(minecartNMS);
                if (!flag) minecartNMS.l();
            }
        }, 0, 1);
        getLogger().info("Плагин MinecartChunkLoader включен");
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин MinecartChunkLoader выключен");
    }
}
