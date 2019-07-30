package tahpie.savage.savagequests.quests.types;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import tahpie.savage.savagequests.SavageUtility;
import tahpie.savage.savagequests.events.QuestManager;
import tahpie.savage.savagequests.quests.QuestNPC;

public class Defeat_Mobs extends QuestNPC{
	ArrayList<String> questMobsName;
	ArrayList<Integer> questMobsNumber;
	
	HashMap<String,ArrayList<String>> questMobsNameMap = new HashMap<String,ArrayList<String>>();
	HashMap<String,ArrayList<Integer>> questMobsNumberMap = new HashMap<String,ArrayList<Integer>>();
	
	HashMap<String, ArrayList<Integer>> progress = new HashMap<String, ArrayList<Integer>>();
	ArrayList<Integer> target = new ArrayList<Integer>();

	public Defeat_Mobs(HashMap<String,ArrayList<String>> args) {
		super(args);		
		questMobsName = args.get("questMobsName");
		questMobsNumber = new ArrayList<Integer>();
		for(String s : args.get("questMobsNumber")) questMobsNumber.add(Integer.valueOf(s));
		
	}
	public boolean quest_requirement(Player player) {
		if(complete.containsKey(player.getName())) {
			if(complete.get(player.getName())) {
				complete.remove(player.getName());
				questMobsNameMap.remove(player.getName());
				questMobsNumberMap.remove(player.getName());
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean quest_event(Player player, Object event) {
		LivingEntity entity = ((EntityDeathEvent)event).getEntity();
		Log.info(entity.getName());
		Log.info(entity.getName().replaceAll("[^A-Za-z]", ""));
		Log.info(questMobsNameMap);
//		if(questMobsNameMap.get(player.getName()).contains(entity.getType().getName())) { // nametags disabled so this shouldn't be problematic... I hope
		if(questMobsNameMap.get(player.getName()).contains(entity.getName().replaceAll("[^A-Za-z]", ""))) { 
			int index = questMobsNameMap.get(player.getName()).indexOf(entity.getName().replaceAll("[^A-Za-z]", ""));
			ArrayList<Integer> playerMobsNumber = questMobsNumberMap.get(player.getName());
			ArrayList<String> playerMobsName = questMobsNameMap.get(player.getName());
			
			playerMobsNumber.set(index, playerMobsNumber.get(index)-1);
			
			player.sendMessage(ChatColor.RED+String.valueOf(playerMobsNumber.get(index))+" "+StringUtils.capitalize(playerMobsName.get(index))+"s"+ChatColor.DARK_PURPLE+" remaining");
			if(playerMobsNumber.get(index)==0) {
				playerMobsName.remove(index);
				playerMobsNumber.remove(index);
			}
		}
		if(questMobsNameMap.get(player.getName()).size()==0 && complete.get(player.getName())==false) {
			complete.put(player.getName(), true);
			player.sendMessage(ChatColor.GOLD+"Quest complete. Return to where you accepted your quest to retrieve your reward.");
		}
		return false;
	}
	public void on_accept(Player player) {
		if(!(complete.containsKey(player.getName()))) {
			SavageUtility.displayClassMessage("You Have Accepted A Quest", player);
			complete.put(player.getName(), false);
			QuestManager.questMapper.put(player.getName(),name);
			questMobsNameMap.put(player.getName(), new ArrayList<String>(questMobsName));
			questMobsNumberMap.put(player.getName(), new ArrayList<Integer>(questMobsNumber));	
		}
	}
	@Override
	public ArrayList<String> getProgress(Player player) {
		ArrayList<String> progress = new ArrayList<String>();
		for(String target: questMobsName) {
			if(questMobsNameMap.get(player.getName()).contains(target)) {
				int i=0;
				for(String monster: questMobsNameMap.get(player.getName())) {
					if(target.equalsIgnoreCase(monster)) {
						int num = questMobsNumber.get(i) - questMobsNumberMap.get(player.getName()).get(i);
						progress.add(String.valueOf(monster+": "+String.valueOf(num)+"/"+String.valueOf(questMobsNumber.get(i))));
					}
					i++;
				}	
			}
			else {
				progress.add(target+": Complete");
			}
		}
		return progress;
	}
}