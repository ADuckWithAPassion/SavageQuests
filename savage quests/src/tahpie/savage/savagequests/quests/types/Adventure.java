package tahpie.savage.savagequests.quests.types;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import tahpie.savage.savagequests.SavageUtility;
import tahpie.savage.savagequests.events.QuestManager;
import tahpie.savage.savagequests.quests.QuestNPC;

public class Adventure extends QuestNPC{
	Location questDestinationLocation;
	Integer distanceSquared = 4;
	
	public Adventure(HashMap<String,ArrayList<String>> args) {
		super(args);
		ArrayList<String> loc = args.get("questDestinationLocation");
		this.questDestinationLocation = new Location(Bukkit.getWorld(loc.get(3)), Integer.parseInt(loc.get(0)), Integer.parseInt(loc.get(1)), Integer.parseInt(loc.get(2)));

	}
	public boolean quest_requirement(Player player) {
		if(player.getLocation().getWorld().equals(questDestinationLocation.getWorld())) {
			if(player.getLocation().distanceSquared(questDestinationLocation) <=distanceSquared) {
				complete.remove(player.getName());
				return true;
			}
		}
		return false;
	}
	public void on_accept(Player player) {
		if(!(complete.containsKey(player.getName()))) {
			SavageUtility.displayClassMessage("You Have Accepted A Quest", player);
			complete.put(player.getName(), false);
			QuestManager.questMapper.put(player.getName(),name);
		}
	}
	@Override
	public ArrayList<String> getProgress(Player player) {
		ArrayList<String> progress = new ArrayList<String>();
		if(player.getLocation().getWorld().equals(questDestinationLocation.getWorld())) {
			double d = player.getLocation().distance(questDestinationLocation);
			BigDecimal bd = new BigDecimal(d).setScale(2, RoundingMode.HALF_EVEN);
			d = bd.doubleValue();
			progress.add("Distance: "+String.valueOf(d));
		}
		else {
			progress.add("Destination is in another world");
		}
		return progress;
	}
}