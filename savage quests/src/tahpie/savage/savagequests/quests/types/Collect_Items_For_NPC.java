package tahpie.savage.savagequests.quests.types;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tahpie.savage.savagequests.quests.QuestNPC;

public class Collect_Items_For_NPC extends QuestNPC{
	Location questRequirementLocation;

	public Collect_Items_For_NPC(HashMap<String,ArrayList<String>> args) {
		super(args);
		ArrayList<String> loc = args.get("questRequirementLocation");
		this.questRequirementLocation = new Location(Bukkit.getWorld(loc.get(3)), Integer.parseInt(loc.get(0)), Integer.parseInt(loc.get(1)), Integer.parseInt(loc.get(2)));

	}
	public boolean quest_requirement(Player player) {
		boolean completed = true;
		for (ItemStack item : getChest(player,questRequirementLocation)) {
			if(item instanceof ItemStack) {
				if(!(player.getInventory().containsAtLeast(item,item.getAmount()))) {
					completed = false;
				}
			}
		}
		if (completed) {
			for (ItemStack item : getChest(player,questRequirementLocation)) {
				if(item instanceof ItemStack) {
					removeInventoryItems(player.getInventory(), Material.getMaterial(item.getType().toString()), item.getAmount());
				}
			}
			return true;
		}			
		else {	
			player.sendMessage(ChatColor.DARK_RED+"Quest requirements are not met.");
		}
		return false;
	}
	public void on_accept(Player player) {
		return;
	}
	public boolean quest_requirement(Player player, Object... args) {
		return false;
	}
	@Override
	public ArrayList<String> getProgress(Player player) {
		ArrayList<String> progress = new ArrayList<String>();
		for (ItemStack item : getChest(player,questRequirementLocation)) {
			if(item instanceof ItemStack) {
				int i = 0;
				for (ItemStack is : player.getInventory().getContents()) {
				  if (is != null && is.getType() == item.getType()){
				  i = i + is.getAmount();
				  }
				  progress.add(String.valueOf(i)+"/"+String.valueOf(item.getAmount()));
				}
			}
		}
		return progress;
	}
}