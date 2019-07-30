package tahpie.savage.savagequests;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import savageclasses.SavageUtility;
import savageclasses.Scout;
import tahpie.savage.savagequests.friendlyNPC.customZombie;

public class CommandQuest implements CommandExecutor, TabCompleter {
	SavageQuest savagequests;
	public CommandQuest(SavageQuest savagequests) {
		this.savagequests = savagequests;

	}
	
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if(arg0 instanceof Player) {
			Player player = (Player)arg0;
			if(arg1.getName().equalsIgnoreCase("quest")) {
				if(arg3.length == 0) {
					savagequests.gui.createGUI("main_menu",player);						
				}
				else if(arg3.length == 1) {
					if(arg3[0].equalsIgnoreCase("save")) {
						SavageQuest.saveQuests();					
						SavageUtility.displayMessage(ChatColor.GOLD+"Saving Quests...", player);					
					}
					else if(arg3[0].equalsIgnoreCase("load")) {
						SavageQuest.reloadNpcConfig();
						SavageQuest.loadQuests();
						SavageUtility.displayMessage(ChatColor.GOLD+"Reloading Quests...", player);
					}
				}
				else {
					SavageUtility.displayMessage(ChatColor.GOLD+"Please use "+ChatColor.DARK_PURPLE+"/Quest"+ChatColor.GOLD+" to view quest info.",player);
				}
			}
			else if(arg1.getName().equalsIgnoreCase("ban")) {
				Log.info("BAN");
			}
			
		}
		return true;
	}
}