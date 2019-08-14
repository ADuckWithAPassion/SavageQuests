package tahpie.savage.savagequests.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import tahpie.savage.savagequests.GUI;
import tahpie.savage.savagequests.SavageQuest;
import tahpie.savage.savagequests.SavageUtility;

public class UIEvents implements Listener {
	SavageQuest savagequests;
	HashMap<String,Integer> listenFor = new HashMap<String,Integer>();
	HashMap<String,ArrayList<String>> messagesSent = new HashMap<String, ArrayList<String>>();
	HashMap<String,String> keyStored = new HashMap<String, String>();
	HashMap<String, List<Method>> bindsStored = new HashMap<String, List<Method>>();

	public UIEvents(SavageQuest savagequests) {
		this.savagequests = savagequests;
	} // Player events related to UI
	@EventHandler
	public void inventoryClick(InventoryClickEvent event){
		if(!(event.getWhoClicked() instanceof Player) || event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
			return;
		}
		Player player = (Player)event.getWhoClicked();
		if(event.getView().getTitle().contains(ChatColor.translateAlternateColorCodes('&',"&8[&9SR&8]&7 "))) {
			event.setCancelled(true);
			if(event.getClickedInventory().getHolder() != null) {
				return;
			}
			if(event.getView().getTitle().contains(ChatColor.translateAlternateColorCodes('&',"&8[&9SR&8]&7 &cLines"))) {
				ItemStack item = event.getCurrentItem();
				String name = item.getItemMeta().getDisplayName();
				savagequests.gui.lines.put(player.getName(), name.substring(2));
				try {
					bindsStored.get(player.getName()).get(0).invoke(savagequests.gui, player, bindsStored.get(player.getName()));
					return;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			String itemTag = event.getCurrentItem().getItemMeta().getLore().get(0).replaceAll("§", "");
			String itemParent = event.getCurrentItem().getItemMeta().getLore().get(1).replaceAll("§", "");

			String binds_string = savagequests.GUIConfig.getString(itemParent+"."+itemTag+".bind");
			
			List<String> binds_list = new ArrayList<String>(Arrays.asList(itemTag));
			if(binds_string != null) {
				binds_list = new ArrayList<String>(Arrays.asList(binds_string.split(", ")));
			}
			String first_bind = binds_list.get(0);				

			if(itemTag.equalsIgnoreCase("admin_edit")) {
				if (!(player.hasPermission("savage.quest.manage"))) {
					SavageUtility.displayClassMessage(ChatColor.RED+"Insufficient permissions.",player);
					return;
				}
			}
			
			if(first_bind.equalsIgnoreCase("None")){
			}
			else if(first_bind.equalsIgnoreCase("inprogress")) {
				player.sendMessage("Sorry, this feature is currently in development. It will be released soon!");
			}
			else if(first_bind.equalsIgnoreCase("child")) {
				savagequests.gui.createGUI(itemTag, player);
			}
			else if(first_bind.equalsIgnoreCase("remove")) {
				savagequests.gui.remove(player, Integer.parseInt(itemParent));
			}
			else if(first_bind.equalsIgnoreCase("abandon")) {
				savagequests.gui.abandon(player);
			}
			else if(first_bind.equalsIgnoreCase("return")) {
				for(String gui: savagequests.GUIConfig.getKeys(false)) {
					if(savagequests.GUIConfig.getConfigurationSection(gui).getKeys(false).contains(itemParent)) {
						savagequests.gui.createGUI(gui, player);
					}
				}
			}
			else if(first_bind.equalsIgnoreCase("exit")) {
				player.closeInventory();
			}
			else {
				player.closeInventory();
				if(!(savagequests.gui.information.containsKey(player.getName()))) {
		    		savagequests.gui.information.put(player.getName(), new HashMap<String,ArrayList<String>>());
		    	}
				if(first_bind.equalsIgnoreCase("setType")) {
					String type = event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
					savagequests.gui.information.get(player.getName()).put("type", new ArrayList<>(Arrays.asList(type)));
				}
				Class<? extends GUI> cls = savagequests.gui.getClass();
				Method method = null;
				List<Method> binds = new ArrayList<Method>();
				for(String bind: binds_list) {
					try {
						method = cls.getDeclaredMethod(bind, Player.class, List.class);
						binds.add(method);
					} catch (NoSuchMethodException | SecurityException e1) {
						Log.info("Debugger (Ignore unless people complain that a menu is not opening)");
						Log.info("Bind that was not found: "+bind);
						// player.sendMessage(ChatColor.DARK_RED+bind+" is broken - contact TahPie");
						// TODO Auto-generated catch block
						// e1.printStackTrace();
					}
				}
				try {
					binds.get(0).invoke(savagequests.gui, player, binds);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	@EventHandler
	public void playerChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		if(listenFor.containsKey(player.getName())) {
			event.setCancelled(true);
			if(event.getMessage().equalsIgnoreCase("abort")) {
				listenFor.remove(player.getName());
				bindsStored.remove(player.getName());
				messagesSent.remove(player.getName());
				savagequests.gui.information.remove(player.getName());
				savagequests.gui.lines.remove(player.getName());
				player.sendMessage(ChatColor.DARK_RED+"Quest creation aborted.");
				return;
			}
			ArrayList<String>messages = messagesSent.get(player.getName());
			if(!(event.getMessage().equalsIgnoreCase("done"))) {
				messages.add(event.getMessage());	
			}
			messagesSent.replace(player.getName(), messages);
			int counter = listenFor.get(player.getName());
			counter = counter - 1;
			if(counter <=0 || event.getMessage().equalsIgnoreCase("done")) {
				listenFor.remove(player.getName());
				String key = keyStored.get(player.getName());
				savagequests.gui.information.get(player.getName()).put(key, messagesSent.get(player.getName()));
				try {					
					bindsStored.get(player.getName()).get(0).invoke(savagequests.gui, player, bindsStored.get(player.getName()));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				listenFor.replace(player.getName(), counter);
				player.sendMessage(ChatColor.LIGHT_PURPLE+"Your message has been recorded");
			}
		}

	}
	public void addToChatWatch(Player player, String lines, List<Method> resavagequestsing_binds, String key) {
		messagesSent.put(player.getName(), new ArrayList<String>());
		listenFor.put(player.getName(), Integer.decode(lines));
		keyStored.put(player.getName(), key);
		bindsStored.put(player.getName(), resavagequestsing_binds);
	}
}

