package tahpie.savage.savagequests;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import tahpie.savage.savagequests.events.QuestManager;
import tahpie.savage.savagequests.events.UIEvents;
import tahpie.savage.savagequests.quests.QuestNPC;
import tahpie.savage.savagequests.quests.types.Adventure;
import tahpie.savage.savagequests.quests.types.Collect_Items_For_NPC;
import tahpie.savage.savagequests.quests.types.Defeat_Mobs;
import tahpie.savage.savagequests.quests.types.FindAnotherNPC;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SavageQuest extends JavaPlugin implements Listener{

	public File NpcConfigFile;
	public File GUIConfigFile;
	public FileConfiguration GUIConfig;
	public FileConfiguration NpcConfig;
	public GUI gui;
	public UIEvents uievents;
	public QuestManager questmanager;

	@Override
	public void onEnable() {

		gui = new GUI(this);
		uievents = new UIEvents(this);
		questmanager = new QuestManager(this);

		Config();

		registerCommands();
		getServer().getPluginManager().registerEvents(this, this);

		getServer().getPluginManager().registerEvents(questmanager, this);
		getServer().getPluginManager().registerEvents(uievents, this);

		for(Player player: getServer().getOnlinePlayers()) {
			questmanager.reset(player);
		}
		
		getLogger().info("enabled!");
	}

	private void registerCommands(){
		Objects.requireNonNull(this.getCommand("quest")).setExecutor(new CommandQuest(this));
	}

	private void Config() {
		if(!(getDataFolder().exists())) {
			Bukkit.broadcastMessage("Created new directory for SavageQuests plugin");
			getDataFolder().mkdir();	        
            saveResource("npc.yml", false);
            saveResource("gui.yml", false);
		}

		NpcConfigFile = new File(getDataFolder(), "npc.yml");
		GUIConfigFile = new File(getDataFolder(), "gui.yml");

		NpcConfig = new YamlConfiguration();
		GUIConfig = new YamlConfiguration();

		try {
			NpcConfig.load(NpcConfigFile);
			GUIConfig.load(GUIConfigFile);
		} 
		catch (IOException | InvalidConfigurationException e) {
			Bukkit.broadcastMessage("Failed to load config files.");
			e.printStackTrace();
		}
		loadQuests();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, new Runnable() {

			@Override
			public void run() {
		    	for(String p: QuestManager.questMapper.keySet()) {
		    		if(QuestManager.questMapper.containsKey(p)) {
			    		String npc = QuestManager.questMapper.get(p);
			    		QuestNPC quest = QuestManager.characterToClass.get(npc);
			    		if(quest != null) {
			    			if(quest.type.equalsIgnoreCase("Adventure.")) {
			    				quest.quest(Bukkit.getPlayer(p));
			    			}
			    		}
		    		}
		    	}					
			}
		}, 0L, 100L);
		
	}
	@SuppressWarnings("unchecked")
	private void loadQuests() {
		for(String NPC: NpcConfig.getKeys(false)) {
			HashMap<String,ArrayList<String>>map = new HashMap<String,ArrayList<String>>();
			
			for(String key: NpcConfig.getConfigurationSection(NPC).getKeys(false)){
				map.put(key,(ArrayList<String>)NpcConfig.getConfigurationSection(NPC).get(key));
			}
			if(map.get("type").get(0).equalsIgnoreCase("Collect Items For NPC.")){
				new Collect_Items_For_NPC(map);
			}
			else if(map.get("type").get(0).equalsIgnoreCase("Defeat Mobs.")){
				new Defeat_Mobs(map);
			}
			else if(map.get("type").get(0).equalsIgnoreCase("Adventure.")){
				new Adventure(map);
			}
			else if(map.get("type").get(0).equalsIgnoreCase("Find Another NPC.")){
				new FindAnotherNPC(map);
			}
		}
	}
}
