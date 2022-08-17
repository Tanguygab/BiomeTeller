package io.github.tanguygab.biometeller;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class BiomeTeller extends JavaPlugin implements Listener {

    private final Map<Player,Biome> map = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        String chat = getConfig().getString("chat-message","You've just entered biome %biome%!");
        String actionbar = getConfig().getString("actionbar-message","You've just entered biome %biome%!");


        getServer().getPluginManager().registerEvents(this,this);
        getServer().getOnlinePlayers().forEach(p->map.put(p,getBiome(p)));
        getServer().getScheduler().runTaskTimerAsynchronously(this,()-> getServer().getOnlinePlayers().forEach(p->{
            if (!map.containsKey(p)) return;
            Biome biome = getBiome(p);
            if (map.get(p) == biome) return;
            map.put(p,biome);
            if (!chat.equals("")) p.sendMessage(getBiomeText(chat,biome));
            if (!actionbar.equals("")) p.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(getBiomeText(actionbar,biome)));
        }),0,20);
    }

    private Biome getBiome(Player p) {
        return p.getWorld().getBiome(p.getLocation());
    }
    private String getBiomeText(String str, Biome biome) {
        return ChatColor.translateAlternateColorCodes('&',str.replace("%biome%",biome.toString().toLowerCase().replace("_"," ")));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        map.put(p,getBiome(p));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        map.remove(e.getPlayer());
    }
}
