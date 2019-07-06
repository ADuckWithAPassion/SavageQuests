package tahpie.savage.savagequests.quests.types;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import tahpie.savage.savagequests.SavageUtility;
import tahpie.savage.savagequests.events.QuestManager;
import tahpie.savage.savagequests.quests.QuestNPC;

public class FindAnotherNPC extends QuestNPC{
	String questNPCName;
	
	public FindAnotherNPC(HashMap<String,ArrayList<String>> args) {
		super(args);
		questNPCName = args.get("questNPCName").get(0);
	}
	public boolean quest_requirement(Player player) {
		return false;
	}
	@Override
	public boolean quest_event(Player player, Object arg) {
		NPCRightClickEvent event = (NPCRightClickEvent)arg;
		if(event.getNPC().getName().equalsIgnoreCase(questNPCName)) {
			reward(player);
			complete.remove(player.getName());
			return true;
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
		for(NPC npc: CitizensAPI.getNPCRegistry().sorted()) {
			if(npc.getName().equalsIgnoreCase(questNPCName)) {
				if(npc.getStoredLocation().getWorld().equals(player.getLocation().getWorld())) {
					double d = npc.getStoredLocation().distance(player.getLocation());
					BigDecimal bd = new BigDecimal(d).setScale(2, RoundingMode.HALF_EVEN);
					d = bd.doubleValue();
					progress.add("Distance: "+String.valueOf(d));
				}
				else {
					progress.add("NPC is in another world.");
				}
			}
			
		}
		return progress;
	}
}