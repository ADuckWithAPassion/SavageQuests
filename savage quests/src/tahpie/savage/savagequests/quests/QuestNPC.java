package tahpie.savage.savagequests.quests;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import tahpie.savage.savagequests.SavageUtility;
import tahpie.savage.savagequests.events.QuestManager;

public abstract class QuestNPC{
	public String type;
	public String name;
	public ArrayList<String> questIntroText;
	public ArrayList<String> questRewardText;
	public Location questRewardLocation;
	public Integer CD;
		
	public Integer timeout = 10;
	
	public HashMap <String, Long> cooldown = new HashMap<String, Long>();
	public HashMap <String, Long> timeoutMap = new HashMap<String, Long>();
	public HashMap<String, Integer> counter =  new HashMap<String, Integer>();
	public HashMap<String,Boolean> complete = new HashMap<String,Boolean>();
	
	public ArrayList<Integer> target = new ArrayList<Integer>();
		
	public QuestNPC(HashMap<String,ArrayList<String>> args) {
		this.name = args.get("name").get(0);
		this.type = args.get("type").get(0);
		this.CD = Integer.parseInt(args.get("questCooldown").get(0));
		this.questIntroText = args.get("questIntroText");
		this.questRewardText = args.get("questRewardText");
		ArrayList<String> loc = args.get("questRewardLocation");
		this.questRewardLocation = new Location(Bukkit.getWorld(loc.get(3)), Integer.parseInt(loc.get(0)), Integer.parseInt(loc.get(1)), Integer.parseInt(loc.get(2)));
		
		QuestManager.characterToClass.put(name, this);
		
	}
	
	public void quest(Player player) {
		Boolean status = false;
		try {
			status = (Boolean) quest_requirement(player);
		} catch ( SecurityException  | IllegalArgumentException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(status) {
			reward(player);
		}	
	}
	
	public abstract boolean quest_requirement(Player player);
	public abstract void on_accept(Player player);
	public boolean quest_event(Player player, Object args) {
		return false;
	}
	public abstract ArrayList<String> getProgress(Player player);
	
	public void reward(Player player) {
		SavageUtility.displayClassMessage("Quest Completed!", player);
		for(String s: questRewardText) {
			SavageUtility.displayQuestMessage(s, player,name);
		}
		for (ItemStack item : getChest(player,questRewardLocation)) {
			if(item instanceof ItemStack) {
				player.getInventory().addItem(new ItemStack(item));
			}
		}
		
		player.updateInventory();
		player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,10,1);

		cooldown(player);
	}
	
	public void talk(Player player, HashMap<String,String> talkingTo) {
		if (cooldown.containsKey(player.getName()) == false || timeoutMap.containsKey(player.getName()) == false || counter.containsKey(player.getName()) == false) {
			cooldown.put(player.getName(), (long)0);
			timeoutMap.put(player.getName(), (long)0);
			counter.put(player.getName(), 0);
		}
		
		if (System.currentTimeMillis() <= cooldown.get(player.getName())) {			
			SavageUtility.displayClassMessage("This quest is on cooldown for another "+ (cooldown.get(player.getName()) - System.currentTimeMillis())/1000 + " seconds.", player);
			player.playSound(player.getLocation(),Sound.ENTITY_VILLAGER_NO,10,1);
			return;
		}
		if (talkingTo.get(player.getName()) != name || System.currentTimeMillis() >= timeoutMap.get(player.getName())){
			talkingTo.replace(player.getName(), name);
			counter.replace(player.getName(), 0);
		}
		if(counter.get(player.getName()) == questIntroText.size()) {
			on_accept(player);
			quest(player);
			return;
		}
		
		SavageUtility.displayQuestMessage(questIntroText.get((int)counter.get(player.getName())), player,name);
		counter.replace(player.getName(), (int)counter.get(player.getName()) + 1);
		timeoutMap.replace(player.getName(), System.currentTimeMillis() + (long)timeout*1000 );	
	}

	public void cooldown(Player player) {
		cooldown.replace(player.getName(), System.currentTimeMillis() + (long)CD*1000);
		counter.replace(player.getName(), 0);
		QuestManager.questMapper.put(player.getName(), "empty");
	}

	public static void removeInventoryItems(PlayerInventory inv, Material type, int amount) {
	    for (ItemStack is : inv.getContents()) {
	        if (is != null && is.getType() == type) {
	            int newamount = is.getAmount() - amount;
	            if (newamount > 0) {
	                is.setAmount(newamount);
	                return;
	            } else {
	            	is.setAmount(0);
	                amount = -newamount;
	                if (amount == 0) {
	                	return;
	                }
	            }
	        }
	    }
	}
	public ItemStack[] getChest(Player player, Location location) {
		Block block = player.getWorld().getBlockAt(location);
		if(!(block.getState() instanceof Chest)) {
			player.sendMessage(ChatColor.DARK_RED+"Chest not found.");
		}
		Chest chest = (Chest) block.getState();
	
		ItemStack[] items = chest.getInventory().getContents();
		return items;	
	}
}
