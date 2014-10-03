package com.SkyIsland.EnderDragonFridays.Items;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Generates a chest-ful of equipment for players!<br />
 * Takes a mapping of players to their contribution and generates loot. The results are returned
 * as a map between players and chests.<br />The chest the players are mapped to contain the loot
 * generated for that player.
 * @author Skyler
 * @note This should be abstracted out when converting to a framework. Let people make their own ChestGenerators that don't,<br />
 * for example, exclude 0% contribution
 */
public class ChestContentGenerator {
	
	private static LootGenerator gen;
	
	public static Map<Player, Inventory> generate(double rarity, Map<Player, Double> inputMap) {
		
		//First, create our generator
		gen = new LootGenerator(rarity);
		
		//Next, we set up our new map that will connect players to their chests
		Map<Player, Inventory> output = new HashMap<Player, Inventory>();
		
		Inventory chest;
		List<Integer> rec = new LinkedList<Integer>();
		//dragon drops up to 3 eggs, so choose up to 3 people to receive it.
		Random rand = new Random();		
		rec.add(rand.nextInt(inputMap.keySet().size())); //0, 1, 2, ... , n
		rec.add(rand.nextInt(inputMap.keySet().size())); //if we get the same number as before, that just
		rec.add(rand.nextInt(inputMap.keySet().size())); //means we wont get 3 eggs
		int number = 0;
		
		for (Player player : inputMap.keySet()) {
			
			//Before anything, make sure they contributed!
			if (inputMap.get(player) <= .01) {
				//they have only contributed 1% or less of total health!
				continue; //nothing for them!
			}
			
			//Create a chest
			chest = Bukkit.getServer().createInventory(null, 27);
			//we are going to populate it with two items
			chest.addItem(gen.generateItem(  inputMap.get(player)  )); //generate item. Use the double passed with player as weight
			chest.addItem(gen.generateItem(  inputMap.get(player)  ));
			//chest.addItem(new ItemStack(Material.DIAMOND_AXE));
			
			//check if they got a dragon egg
			if (rec.contains(number)) {
				//add an egg to their chest
				ItemStack egg = new ItemStack(Material.MONSTER_EGG);
				ItemMeta meta = egg.getItemMeta();
				List<String> lore = meta.getLore();
				
				if (lore != null) {
					lore.set(0, "Dragon Egg");
					meta.setLore(lore);
					meta.setDisplayName("Dragon Egg");
					egg.setItemMeta(meta);
					chest.addItem(egg);
				}
			}
			
			//add this inventory to the map
			output.put(player, chest);
			number++;
		}
		
		return output;
	}
}
