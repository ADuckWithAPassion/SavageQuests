package tahpie.savage.savagequests;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import tahpie.savage.savagequests.events.QuestManager;
import tahpie.savage.savagequests.quests.QuestNPC;
import tahpie.savage.savagequests.quests.types.Adventure;
import tahpie.savage.savagequests.quests.types.Collect_Items_For_NPC;
import tahpie.savage.savagequests.quests.types.Defeat_Mobs;
import tahpie.savage.savagequests.quests.types.FindAnotherNPC;

public class GUI implements Listener {
	SavageQuest savagequests;
    public HashMap<String,HashMap<String,ArrayList<String>>> information = new HashMap<String, HashMap<String,ArrayList<String>>>();
    public HashMap<String,String> lines = new HashMap<String,String>();
    
	public GUI(SavageQuest savagequests) {
		this.savagequests = savagequests;
	}
	public void createGUI(String gui_name, Player player) {
		Inventory inv = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&', "&8[&9SR&8]&7 "+gui_name));
		Set<String> gui = savagequests.GUIConfig.getConfigurationSection(gui_name).getKeys(false);
		for(String tag: gui) {
			String parent = gui_name;
			String name = savagequests.GUIConfig.getString(gui_name+"."+tag+".name");
			String material = savagequests.GUIConfig.getString(gui_name+"."+tag+".material");
			String position = savagequests.GUIConfig.getString(gui_name+"."+tag+".position");
			String colour = savagequests.GUIConfig.getString(gui_name+"."+tag+".colour");
			ItemStack itemStack = createItem(name,material,colour,tag,parent);
			inv.setItem(Integer.parseInt(position), itemStack);
		}
		player.openInventory(inv);
	}
	public void getNumber(String title, Player player) {
		title.substring(8);
		title = "Num entries: "+title;
		Inventory inv = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', "&d"+title));
		for(int i=1; i<=9;i++) {
			ItemStack itemStack = createItem(String.valueOf(i), "WRITTEN_BOOK", "b","numberOfLines","None");
			ItemMeta meta = itemStack.getItemMeta();
			BookMeta bookMeta = (BookMeta)meta;
			bookMeta.setAuthor("ADMIN");
			meta = (ItemMeta)bookMeta;
			itemStack.setItemMeta(meta);
			inv.addItem(itemStack);
		}
		player.openInventory(inv);
	}
	public ItemStack createItem(String name, String material, String colour, String tag, String parent) {
		ItemStack item = new ItemStack(Material.getMaterial(material));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.getByChar(colour)+name);
		List<String> lore = meta.getLore();
		lore = new ArrayList<>();
		lore.add(convertToInvisibleString(tag));
		lore.add(convertToInvisibleString(parent));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
    public static String convertToInvisibleString(String s) {
        String hidden = "";
        for (char c : s.toCharArray()) hidden += ChatColor.COLOR_CHAR+""+c;
        return hidden;
    }
    public void setType(Player player, List<Method> binds) {
    	binds.remove(0);
    	if(binds.isEmpty()) {
    		return;
    	}
    	getName(player,binds);
    }
    public void getName(Player player, List<Method> binds) {    	
    	binds.remove(0);
    	if(binds.isEmpty()) {
    		return;
    	}
    	player.sendMessage(ChatColor.GREEN+"Please enter name of quest NPC.");
    	savagequests.uievents.addToChatWatch(player, "1", binds, "name");
    }
    public void getQuestIntroText(Player player, List<Method> binds) {
    	binds.remove(0);
    	if(binds.isEmpty()) {
    		return;
    	}
    	player.sendMessage(ChatColor.GREEN+"Please enter quest intro text.");
    	savagequests.uievents.addToChatWatch(player, "9", binds,"questIntroText");
    }
    public void getQuestRewardText(Player player, List<Method> binds) {
    	binds.remove(0);
    	if(binds.isEmpty()) {
    		return;
    	}
    	player.sendMessage(ChatColor.GREEN+"Please enter quest reward text.");
    	savagequests.uievents.addToChatWatch(player, "9", binds,"questRewardText");
    }
    public void getQuestRewardLocation(Player player, List<Method> binds) {
    	binds.remove(0);
    	if(binds.isEmpty()) {
    		return;
    	}
    	player.sendMessage(ChatColor.GREEN+"Please enter quest reward location. (X,Y,Z)");
    	savagequests.uievents.addToChatWatch(player, "1", binds,"questRewardLocation");
    }
    public void getQuestRequirementsLocation(Player player, List<Method> binds) {
    	binds.remove(0);
    	if(binds.isEmpty()) {
    		return;
    	}
    	player.sendMessage(ChatColor.GREEN+"Please enter quest requirements location. (X,Y,Z)");
    	savagequests.uievents.addToChatWatch(player, "1", binds,"questRequirementLocation");
    }
    public void getQuestCooldown(Player player, List<Method> binds) {
    	binds.remove(0);
    	if(binds.isEmpty()) {
    		return;
    	}
    	player.sendMessage(ChatColor.GREEN+"Please enter quest cooldown.");
    	savagequests.uievents.addToChatWatch(player, "1", binds,"questCooldown");
    }
    public void getQuestMobsName(Player player, List<Method> binds) {
    	binds.remove(0);
    	if(binds.isEmpty()) {
    		return;
    	}
    	player.sendMessage(ChatColor.GREEN+"Please enter name of mobs to slay.");
    	savagequests.uievents.addToChatWatch(player, "9", binds,"questMobsName");
    }
    public void getQuestMobsNumber(Player player, List<Method> binds) {
    	binds.remove(0);
    	if(binds.isEmpty()) {
    		return;
    	}
    	player.sendMessage(ChatColor.GREEN+"Please enter number of mobs to slay.");
    	savagequests.uievents.addToChatWatch(player, "9", binds,"questMobsNumber");
    }
    public void getQuestDestinationLocation(Player player, List<Method> binds) {
    	binds.remove(0);
    	if(binds.isEmpty()) {
    		return;
    	}
    	player.sendMessage(ChatColor.GREEN+"Please enter quest destination location.");
    	savagequests.uievents.addToChatWatch(player, "1", binds,"questDestinationLocation");
    }
    public void getQuestNPCName(Player player, List<Method> binds) {
    	binds.remove(0);
    	if(binds.isEmpty()) {
    		return;
    	}
    	player.sendMessage(ChatColor.GREEN+"Please enter quest NPC target's name.");
    	savagequests.uievents.addToChatWatch(player, "1", binds,"questNPCName");
    }
    public void currentQuest(Player player, List<Method> binds) {
    	binds.remove(0);
    	if(binds.isEmpty()) {
    		return;
    	}
    	player.sendMessage(ChatColor.GREEN+"You begin to view your current quest info.");
    	String npc = QuestManager.questMapper.get(player.getName());
    	QuestNPC quest = QuestManager.characterToClass.get(npc);
    	if(quest==null) {
    		Inventory inv = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&', "&d"+"&8[&9SR&8]&7 Current Quest Info"));
    		ItemStack item = createItem("You Have No Current Quest", "BARRIER", "b", "None", "None");
    		inv.setItem(22, item);
    		item = createItem("Return", "RED_STAINED_GLASS_PANE", "c", "return", "current_quest");
    		inv.setItem(44, item);
    		player.openInventory(inv);
    		return;
    	}
		Inventory inv = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&', "&d"+"&8[&9SR&8]&7 Current Quest Info"));
		SkullMeta  skullmeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        skullmeta.setOwner(npc);
        ItemStack item = createItem("Quest NPC", "PLAYER_HEAD", "b", "abandon", "");
        List<String> lore = item.getItemMeta().getLore();
        lore.set(1, npc);
        skullmeta.setDisplayName(ChatColor.YELLOW+"Abandon Quest");
        skullmeta.setLore(lore);
        item.setItemMeta(skullmeta);
        inv.setItem(4,item);
        
        HashMap<String,String>itemMap=new HashMap<String,String>();
        itemMap.put("Collect Items For NPC", "COOKED_BEEF");
        itemMap.put("Defeat Mobs.", "BONE");
        itemMap.put("Adventure.", "OAK_BOAT");
        itemMap.put("Find Another NPC.", "GLASS_BOTTLE");
        
        item = createItem("Quest Type", itemMap.get(quest.type), "b", "", "");
        ItemMeta meta = item.getItemMeta();
        lore = new ArrayList<String>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6"+quest.type));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(20,item);
        
        item = createItem("Quest Information", "MAP", "b", "", "");
        meta = item.getItemMeta();
        lore = new ArrayList<String>();
        for(String text: quest.questIntroText) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&6"+text));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(22,item);
        
        item = createItem("Quest Progress", "LADDER", "b", "", "");
        meta = item.getItemMeta();
        lore = new ArrayList<String>();
        for(String text: quest.getProgress(player)) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&6"+text));

        }
        meta.setLore(new ArrayList<String>(lore));
        item.setItemMeta(meta);
        inv.setItem(24,item);
        
		item = createItem("Return", "RED_STAINED_GLASS_PANE", "c", "return", "current_quest");
		inv.setItem(44, item);

        player.openInventory(inv);
        
    }
    public void deleteQuest(Player player, List<Method> binds) {
    	binds.remove(0);
    	if(binds.isEmpty()) {
    		return;
    	}
		Inventory inv = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&', "&d"+"&8[&9SR&8]&7 Delete Quest"));
		ItemStack item;
		for(NPC npc: CitizensAPI.getNPCRegistry().sorted()) {
			if(!(QuestManager.characterToClass.containsKey(npc.getName()))) {
				continue;
			}
			item = createItem(npc.getName(), "PLAYER_HEAD", "b", "remove", String.valueOf(npc.getId()));
			SkullMeta skullmeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
			skullmeta.setLore(item.getItemMeta().getLore());
	        skullmeta.setOwner(npc.getName()); // THIS CAUSES LAG. MIGHT REMOVE IN FUTURE/FIND BETTER METHOD.
	        item.setItemMeta(skullmeta);
	        inv.addItem(item);
		}
		player.openInventory(inv);
    }
    public void bossMenu(Player player, List<Method> binds) {
    	binds.remove(0);
    	if(binds.isEmpty()) {
    		return;
    	}
		Inventory inv = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&', "&d"+"&8[&9SR&8]&7 Delete Quest"));
		ItemStack item;
		for(NPC npc: CitizensAPI.getNPCRegistry().sorted()) {
			if(!(QuestManager.characterToClass.containsKey(npc.getName()))) {
				continue;
			}
			item = createItem(npc.getName(), "PLAYER_HEAD", "b", "remove", String.valueOf(npc.getId()));
			SkullMeta skullmeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
			skullmeta.setLore(item.getItemMeta().getLore());
	        skullmeta.setOwner(npc.getName()); // THIS CAUSES LAG. MIGHT REMOVE IN FUTURE/FIND BETTER METHOD.
	        item.setItemMeta(skullmeta);
	        inv.addItem(item);
		}
		player.openInventory(inv);    	
    }
    public void remove(Player player, int id) {
    	NPC npc = CitizensAPI.getNPCRegistry().getById(id);
    	for(String name: QuestManager.questMapper.keySet()) {
    		if(QuestManager.questMapper.get(player.getName()).equalsIgnoreCase(npc.getFullName())) {
    			SavageUtility.displayClassMessage("Your current quest has been deleted.", Bukkit.getPlayer(name));
    			QuestManager.questMapper.remove(name);
    		}
    	}
    	QuestManager.characterToClass.remove(npc.getName());
    	savagequests.NpcConfig.set(npc.getName(), null);
    	npc.destroy();
    	try {
    		savagequests.NpcConfig.save(savagequests.NpcConfigFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	SavageUtility.displayClassMessage("NPC has been destroyed", player);
    	player.closeInventory();
    }
    public void abandon(Player player) {
    	QuestManager.abandon_quest(player);
    	SavageUtility.displayClassMessage("Your quest has been abandoned", player);
    	player.closeInventory();
    }
    public void complete(Player player, List<Method> binds) {
    	HashMap<String,ArrayList<String>> data = new HashMap<String,ArrayList<String>>(); 
    	
    	for(String key: information.get(player.getName()).keySet()) {
    		ArrayList<String> value;
			if(key.equalsIgnoreCase("questRewardLocation") || key.equalsIgnoreCase("questRequirementLocation") || key.equalsIgnoreCase("questDestinationLocation") ) {
    			ArrayList<String> input = information.get(player.getName()).get(key);
    			ArrayList<String> coords = new ArrayList<String>(Arrays.asList(input.get(0).split(", ")));
    			coords.add(player.getWorld().getName());
    			value = coords;
			}
    		else {
    			value = information.get(player.getName()).get(key);
    		}
    		data.put(key, value);
    	}
    	
    	if(data.get("type").get(0).equalsIgnoreCase("Collect Items For NPC.")) {
    		new Collect_Items_For_NPC(data);
    	}
    	else if(data.get("type").get(0).equalsIgnoreCase("Defeat Mobs.")) {
    		new Defeat_Mobs(data);
    	}
    	else if(data.get("type").get(0).equalsIgnoreCase("Adventure.")) {
    		new Adventure(data);
    	}
    	else if(data.get("type").get(0).equalsIgnoreCase("Find Another NPC.")) {
    		new FindAnotherNPC(data);
    	}
        try {
        	savagequests.NpcConfig.set(information.get(player.getName()).get("name").get(0),data);
			savagequests.NpcConfig.save(savagequests.NpcConfigFile);
		} catch (IOException e) {
			player.sendMessage(ChatColor.DARK_RED+"Error saving NPC.");
			e.printStackTrace();
		}
    	NPCRegistry registry = CitizensAPI.getNPCRegistry();
        NPC npc = registry.createNPC(EntityType.PLAYER, information.get(player.getName()).get("name").get(0));
        npc.spawn(player.getLocation());
        
        player.sendMessage(ChatColor.AQUA+"An NPC Has Been Created.");
       
    }
}	


