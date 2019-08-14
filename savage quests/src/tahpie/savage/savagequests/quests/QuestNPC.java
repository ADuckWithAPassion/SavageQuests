package tahpie.savage.savagequests.quests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tahpie.savage.savagequests.SavageQuest;
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
	private HashMap<String, ArrayList<String>> data;
		
	public QuestNPC(HashMap<String,ArrayList<String>> args) {
		this.data = args;
		this.name = args.get("name").get(0);
		this.type = args.get("type").get(0);
		this.CD = Integer.parseInt(args.get("questCooldown").get(0));
		this.questIntroText = args.get("questIntroText");
		this.questRewardText = args.get("questRewardText");
		ArrayList<String> loc = args.get("questRewardLocation");
		ArrayList<String> cd = args.get("onCooldown");
		this.questRewardLocation = new Location(Bukkit.getWorld(loc.get(3)), Integer.parseInt(loc.get(0)), Integer.parseInt(loc.get(1)), Integer.parseInt(loc.get(2)));
		if(cd != null) {
			for(String onCooldown: cd) {
				String playerName = onCooldown.split(",")[0];
				long playerCooldown = Long.valueOf(onCooldown.split(",")[1].replaceAll(" ", ""));
				cooldown(playerName, playerCooldown);
			}	
		}

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
		player.playSound(player.getLocation(),Sound.LEVEL_UP,10,1);

		cooldown(player);
	}
	
	public void talk(Player player, HashMap<String,String> talkingTo) {
		if (cooldown.containsKey(player.getName()) == false || timeoutMap.containsKey(player.getName()) == false || counter.containsKey(player.getName()) == false) {
			cooldown.put(player.getName(), (long)0);
			timeoutMap.put(player.getName(), (long)0);
			counter.put(player.getName(), 0);
		}
		
		if (System.currentTimeMillis() <= cooldown.get(player.getName())) {			
			long time = (cooldown.get(player.getName()) - System.currentTimeMillis())/1000;
			String unit = " seconds.";
			if(time >= 24*60*60) {
				time = Math.round(time/(24*60*60)); // convert to days
				unit = " days.";
			}
			else if(time >= 60*60) {
				time = Math.round(time/(60*60)); // convert to hours
				unit = " hours.";
			}
			else if(time >= 60) {
				time = Math.round(time/(60)); // convert to minutes
				unit = " minutes.";
			}
			// otherwise continue as seconds.
			
			SavageUtility.displayClassMessage("This quest is on cooldown for another "+ time + unit, player);
			player.playSound(player.getLocation(),Sound.VILLAGER_NO,10,1);
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
		cooldown.put(player.getName(), System.currentTimeMillis() + (long)CD*1000);
		counter.put(player.getName(), 0);
		QuestManager.questMapper.put(player.getName(), "empty");
	}
	public void cooldown(String player, long manualCooldown) {
		cooldown.put(player, manualCooldown);
		counter.put(player, 0);
		timeoutMap.put(player, 0l);
		counter.put(player, 0);
		QuestManager.questMapper.put(player, "empty");
	}

	public static void removeInventoryItems(Player player, Material type, int amount) {
		PlayerInventory inv = player.getInventory();
	    for (int i=0; i<inv.getSize(); i++) {
	    	ItemStack is = inv.getItem(i);
	        if (is != null && is.getType() == type) {
	            int newamount = is.getAmount() - amount;
	            if (newamount > 0) {
	                is.setAmount(newamount);
	                return;
	            } else {
	            	player.getInventory().setItem(i, new ItemStack(Material.AIR));
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
			player.sendMessage(ChatColor.DARK_RED+"Chest not found. - Message TahPie");
		}
		Chest chest = (Chest) block.getState();
	
		ItemStack[] items = chest.getInventory().getContents();
		return items;	
	}
	public void save() {
		try {
			ArrayList<String> cooldowns = new ArrayList<String>();
			for(Entry<String, Long> cd: cooldown.entrySet()) {
				if(cd.getValue() >= System.currentTimeMillis()) {
					cooldowns.add(cd.getKey()+", "+cd.getValue());					
				}
			}
			data.put("onCooldown", cooldowns);
        	SavageQuest.NpcConfig.set(name ,data);
			SavageQuest.NpcConfig.save(SavageQuest.NpcConfigFile);
		} catch (IOException e) {
			Log.info("ERROR SAVING ON EXIT, CONFIG FIX REQUIRE (CORRUPTED???). CONTACT TAHPIE");
			e.printStackTrace();
		}
	}
}
