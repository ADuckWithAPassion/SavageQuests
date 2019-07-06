package tahpie.savage.savagequests;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

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
			savagequests.gui.createGUI("main_menu",player);
		}
		return true;
 // Where all commands are managed
	}
}