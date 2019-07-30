package tahpie.savage.savagequests.events;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import tahpie.savage.savagequests.SavageQuest;
import tahpie.savage.savagequests.SavageUtility;
import tahpie.savage.savagequests.quests.QuestNPC;
import tahpie.savage.savagequests.quests.types.FindAnotherNPC;

public class QuestManager implements Listener{

	public SavageQuest savagequests;
	public static HashMap<String, String> questMapper;
	HashMap <String, Integer> counter;
	HashMap <String, String> talkingTo;
	HashMap <String, Long> timeout;
	public static HashMap<String,QuestNPC> characterToClass;
	HashMap <String, Long> antispam = new HashMap<String, Long>();

	public QuestManager(SavageQuest savagequests) {

		this.savagequests = savagequests;

		questMapper = new HashMap<String, String>();
		counter = new HashMap<String, Integer>();
		talkingTo = new HashMap<String, String>();
		characterToClass = new HashMap<String,QuestNPC>();
		
	}

	public void reset(Player player) {
		if(questMapper.containsKey(player.getName()) == false || counter.containsKey(player.getName()) == false) {
			questMapper.put(player.getName(), "empty");
			counter.put(player.getName(), 0);
			talkingTo.put(player.getName(), "empty");
			antispam.put(player.getName(), (long)0);
		}
	}

	@EventHandler
	public void on_join(PlayerJoinEvent event) {
		reset(event.getPlayer());
	}
	
	@EventHandler
	public void right_click(NPCRightClickEvent event) {
		
		if(!(questMapper.containsKey(event.getClicker().getName()))) {
			questMapper.put(event.getClicker().getName(), "empty");
			counter.put(event.getClicker().getName(), 0);
			talkingTo.put(event.getClicker().getName(), "empty");
			antispam.put(event.getClicker().getName(), (long)0);
		}
		boolean character = characterToClass.containsKey(event.getNPC().getName());
		if (System.currentTimeMillis() <= antispam.get(event.getClicker().getName())) {
			return;
		}
		antispam.replace(event.getClicker().getName(), System.currentTimeMillis() + (long)(0.8*1000) );	

		if(questMapper.get(event.getClicker().getName()) == "empty" && character) {
			characterToClass.get(event.getNPC().getName()).talk(event.getClicker(),talkingTo);
		}
		else if(questMapper.get(event.getClicker().getName()).equals(event.getNPC().getName()) && character){
			QuestNPC quest = characterToClass.get(event.getNPC().getName());
			quest.quest(event.getClicker());
			if(quest.complete.containsKey(event.getClicker().getName())) {
				SavageUtility.displayClassMessage("You Have Already Started This Quest."+ChatColor.DARK_PURPLE+" /Quest" + ChatColor.GOLD+" to view quest information.", event.getClicker());		
			}
		}
		else {
			String npc = questMapper.get(event.getClicker().getName());
			QuestNPC quest = characterToClass.get(npc);
			if(quest==null) {
				return;
			}
			if(quest instanceof FindAnotherNPC) {
				if(!(quest.quest_event(event.getClicker(),event))) {
					SavageUtility.displayQuestMessage(ChatColor.GOLD+"Hey! That's not who I want."+ ChatColor.DARK_PURPLE+" /Quest" + ChatColor.GOLD+" for help.", event.getClicker(), quest.name);
				}
			}
			else {
				event.getClicker().sendMessage(ChatColor.GOLD+"You can only have one quest at a time."+ ChatColor.DARK_PURPLE+" /Quest" + ChatColor.GOLD+" to leave your current quest.");
			}
		}
	}
	public static void abandon_quest(Player player) {
		if(questMapper.containsKey(player.getName())) {
			String  npc = questMapper.get(player.getName());
			if(characterToClass.containsKey(npc)) {
				QuestNPC quest = characterToClass.get(npc);
				quest.complete.remove(player.getName());
				questMapper.remove(player.getName());
			}
		}
	}

	@EventHandler
	public void kill_entity(EntityDeathEvent event) {
		if(event.getEntity() instanceof LivingEntity) {
			
			LivingEntity entity = event.getEntity();
			Player player = entity.getKiller();
			
			if(player == null) {
				return;
			}
			if(questMapper.containsKey(player.getName())) {
				String NPC = questMapper.get(player.getName());
				if(QuestManager.characterToClass.containsKey(NPC)) {
					QuestNPC quest = QuestManager.characterToClass.get(NPC);
					if(quest.type.equalsIgnoreCase("Defeat Mobs.")){
						quest.quest_event(player, event);	
					}
				}
			}
		}
	}

	public static void clearQuests() {
		characterToClass = new HashMap<String, QuestNPC>();
		questMapper = new HashMap<String, String>();
	}
}


