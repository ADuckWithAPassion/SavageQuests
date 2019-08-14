package tahpie.savage.savagequests.quests.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tahpie.savage.savagequests.SavageUtility;
import tahpie.savage.savagequests.events.QuestManager;
import tahpie.savage.savagequests.quests.QuestNPC;

public class Collect_Items_For_NPC extends QuestNPC{
	Location questRequirementLocation;

	public Collect_Items_For_NPC(HashMap<String,ArrayList<String>> args) {
		super(args);
		ArrayList<String> loc = args.get("questRequirementLocation");
		this.questRequirementLocation = new Location(Bukkit.getWorld(loc.get(3)), Integer.parseInt(loc.get(0)), Integer.parseInt(loc.get(1)), Integer.parseInt(loc.get(2)));

	}
	public boolean quest_requirement(Player player) {
		complete.put(player.getName(),true);
		for (ItemStack item : getChest(player,questRequirementLocation)) {
			if(item instanceof ItemStack) {
				if(!(player.getInventory().containsAtLeast(item,item.getAmount()))) {
					complete.put(player.getName(), false);
				}
			}
		}
		if(complete.containsKey(player.getName())) {
			if(complete.get(player.getName())) {
				complete.remove(player.getName());
				for (ItemStack item : getChest(player,questRequirementLocation)) {
					if(item instanceof ItemStack) {
						removeInventoryItems(player, Material.getMaterial(item.getType().toString()), item.getAmount());
					}
				}
				return true;
			}
		}
		SavageUtility.displayClassMessage("Your quest requirements have not been met."+ChatColor.DARK_PURPLE+" /Quest" + ChatColor.GOLD+" to view your progress.", player);		
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
		HashMap<String,Integer> chestSum = new HashMap<String,Integer>();
		HashMap<String,Integer> playerSum = new HashMap<String,Integer>();

		for (ItemStack item : getChest(player,questRequirementLocation)) {
			int i = 0;
			if(item instanceof ItemStack) {
				if(chestSum.containsKey(item.getType().name())) {
					chestSum.put(item.getType().name(), chestSum.get(item.getType().name())+item.getAmount());
				}
				else {
					chestSum.put(item.getType().name(), item.getAmount());
				}
			}
		}
		for (ItemStack is : player.getInventory().getContents()) {
			if (is != null && chestSum.containsKey(is.getType().name())){
				if(playerSum.containsKey(is.getType().name())) {
					Log.info(is.getAmount());
					Log.info(playerSum.get(is.getType().name())+is.getAmount());
					playerSum.put(is.getType().name(), playerSum.get(is.getType().name())+is.getAmount());
				}
				else {
					Log.info(is.getAmount());
					playerSum.put(is.getType().name(), is.getAmount());
				}						
			}
		}
		for(Entry<String, Integer> requiredItem: chestSum.entrySet()) {
			String material = requiredItem.getKey();
			Log.info(requiredItem);
			Log.info(playerSum);
			if(requiredItem.getValue()<1) {
				continue;
			}
			if(playerSum.containsKey(material)) {
				Log.info(material);
				progress.add(material+": "+String.valueOf(playerSum.get(material))+"/"+String.valueOf(requiredItem.getValue()));				
			}
			else {
				Log.info(material);
				progress.add(material+": "+"0"+"/"+String.valueOf(requiredItem.getValue()));					
			}
		}
//		if(item!=null && item.getAmount() > 0) {
//			if(!(player.getInventory().containsAtLeast(item,item.getAmount()))) {
//				progress.add(item.getType()+": Completed");									
//			}
//			else {
//				progress.add(item.getType()+": "+String.valueOf(i)+"/"+String.valueOf(item.getAmount()));				
//			}
//		}
//		
		return progress;
	}
}