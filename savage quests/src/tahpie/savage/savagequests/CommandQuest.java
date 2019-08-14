package tahpie.savage.savagequests;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CommandQuest implements CommandExecutor {
	SavageQuest savagequests;
	public CommandQuest(SavageQuest savagequests) {
		this.savagequests = savagequests;

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
					if (!(player.isOp() || player.getName().equals("TahPie"))) { // temp fix
						SavageUtility.displayClassMessage(ChatColor.RED+"Insufficient permissions.",player);
						return true;
					}
					if(arg3[0].equalsIgnoreCase("save")) {
						SavageQuest.saveQuests();					
						SavageUtility.displayClassMessage(ChatColor.GOLD+"Saving Quests...", player);					
					}
					else if(arg3[0].equalsIgnoreCase("load")) {
						SavageQuest.reloadNpcConfig();
						SavageQuest.loadQuests();
						SavageUtility.displayClassMessage(ChatColor.GOLD+"Reloading Quests...", player);
					}
					else {
						SavageUtility.displayClassMessage(ChatColor.GOLD+"Please use "+ChatColor.DARK_PURPLE+"/Quest"+ChatColor.GOLD+" to view quest info.",player);						
					}
				}
				else {
					SavageUtility.displayClassMessage(ChatColor.GOLD+"Please use "+ChatColor.DARK_PURPLE+"/Quest"+ChatColor.GOLD+" to view quest info.",player);
				}
			}
		}
		return true;
	}
}