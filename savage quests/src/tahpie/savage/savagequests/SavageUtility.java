package tahpie.savage.savagequests;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SavageUtility {
    public static void displayQuestMessage(String s, LivingEntity p, String npc_name){
        if(p instanceof Player) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&9SR&8] &d"+npc_name+":"+"&3 " + s));
        }
    }
    public static void displayClassMessage(String s, LivingEntity p){
        if(p instanceof Player) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&9SR&8] &6" + s));
        }
    }

}
