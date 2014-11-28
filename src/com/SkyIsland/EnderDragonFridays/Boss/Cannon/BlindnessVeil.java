package com.SkyIsland.EnderDragonFridays.Boss.Cannon;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import com.SkyIsland.EnderDragonFridays.EnderDragonFridaysPlugin;
import com.SkyIsland.EnderDragonFridays.Boss.Boss;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.Events.BlindnessVeilEvent;

/**
 * A blindnessVeil continuously casts blindness on all players in the world.
 * @author Skyler
 */
public class BlindnessVeil extends BossComponent {
	
	private double d_min;
	private double d_max;
	private double d_incr;
	private int d_incr_range;
	private Random rand;
	private Boss boss;
	private double offsetX, offsetY, offsetZ;
	
	/**
	 * Instantiates a BlindnessViel. 
	 * @param boss The {@link com.SkyIsland.EnderDragonFridays.Boss.Boss Boss} to fire cannonballs from.
	 * @param type What {@link com.SkyIsland.EnderDragonFridays.Boss.Cannon.TargetType targeting type} is used.
	 * @param min_delay The smallest amount of time (<b>in ticks</b>) to wait before firing again
	 * @param max_delay The largest amount of time (<b>also in ticks</b>) to wait before firing again
	 */
	public BlindnessVeil(Boss boss, double min_delay, double max_delay) {
		this(boss, min_delay, max_delay, (max_delay-min_delay)/10); //default to 1/10 the range as increments
	}
	
	/**
	 * Creates a cannon with the passed delay and increments.
	 * The offset from where the fireballs are launched relative to the boss is defaulted to 0. <u>This
	 * is not encouraged</u>, as multiple fireballs spawned at the same location either explode or bounce off
	 * in a random direction.
	 * @param boss The {@link com.SkyIsland.EnderDragonFridays.Boss.Boss Boss} to fire cannonballs from.
	 * @param type What {@link com.SkyIsland.EnderDragonFridays.Boss.Cannon.TargetType targeting type} is used.
	 * @param min_delay The smallest amount of time (<b>in ticks</b>) to wait before firing again
	 * @param max_delay The largest amount of time (<b>also in ticks</b>) to wait before firing again
	 * @param increments The smallest amount of time (<b>again, in ticks</b>) in-between different firing times.
	 */
	public BlindnessVeil(Boss boss, double min_delay, double max_delay, double increments) {
		this(boss, min_delay, max_delay, increments, 0.0, 0.0, 0.0);
	}
	
	/**
	 * Creates a cannon with the passed delay and increments.
	 * The offset from where the fireballs are launched relative to the boss is defaulted to 0. <u>This
	 * is not encouraged</u>, as multiple fireballs spawned at the same location either explode or bounce off
	 * in a random direction.
	 * @param boss The {@link com.SkyIsland.EnderDragonFridays.Boss.Boss Boss} to fire cannonballs from.
	 * @param type What {@link com.SkyIsland.EnderDragonFridays.Boss.Cannon.TargetType targeting type} is used.
	 * @param min_delay The smallest amount of time (<b>in ticks</b>) to wait before firing again
	 * @param max_delay The largest amount of time (<b>also in ticks</b>) to wait before firing again
	 * @param offsetX The offset the fireball will be created from in relation to the boss
	 * @param offsetY The offset the fireball will be created from in relation to the boss
	 * @param offsetZ The offset the fireball will be created from in relation to the boss
	 */
	public BlindnessVeil(Boss boss, double min_delay, double max_delay, double offsetX, double offsetY, double offsetZ) {
		this(boss, min_delay, max_delay, (max_delay - min_delay) / 10, offsetX, offsetY, offsetZ);
	}
	
	/**
	 * Creates a cannon with the passed delay and increments.
	 * The offset from where the fireballs are launched relative to the boss is defaulted to 0. <u>This
	 * is not encouraged</u>, as multiple fireballs spawned at the same location either explode or bounce off
	 * in a random direction.
	 * @param boss The {@link com.SkyIsland.EnderDragonFridays.Boss.Boss Boss} to fire cannonballs from.
	 * @param type What {@link com.SkyIsland.EnderDragonFridays.Boss.Cannon.TargetType targeting type} is used.
	 * @param min_delay The smallest amount of time (<b>in ticks</b>) to wait before firing again
	 * @param max_delay The largest amount of time (<b>also in ticks</b>) to wait before firing again
	 * @param increments The smallest amount of time (<b>again, in ticks</b>) in-between different firing times.
	 * @param offsetX The offset the fireball will be created from in relation to the boss
	 * @param offsetY The offset the fireball will be created from in relation to the boss
	 * @param offsetZ The offset the fireball will be created from in relation to the boss
	 */
	public BlindnessVeil(Boss boss, double min_delay, double max_delay, double increments, double offsetX, double offsetY, double offsetZ) {
		this.boss = boss;
		
		this.d_min = min_delay;
		this.d_max = max_delay;
		
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		
		//make sure min and max aren't reversed
		if (d_min > d_max) {
			d_max = min_delay;
			d_min = max_delay;
		}
		
		//make sure increments aren't negative
		d_incr = Math.abs(increments);
		
		//we need to make sure the increments are sound
		if ( ((d_max - d_min) / d_incr) % 1 <= .0001) {
			//if increments doesn't 'evenly' divide our range, we're in trouble
			//so we'll round to nearest increment that would
			float even = (int) Math.round((d_max - d_min) / d_incr);
			d_incr = (d_max - d_min) / even; 
		}
		
		d_incr_range = (int) Math.round((d_max - d_min) / d_incr); //store so we don't have to do this math over and over again
		
		//create out random
		rand = new Random();
		
		Long time = (long) (d_min + (d_incr * (rand.nextInt(d_incr_range)))); 
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(EnderDragonFridaysPlugin.plugin, this, time);
	}
	
	@Override
	public void run() {
		LivingEntity dDragon = boss.getEntity();

		//very first, make sure boss is still alive. If not, kill self
		if (boss == null || !boss.isAlive()) {
			return;
		}
		Long time = (long) (d_min + (d_incr * (rand.nextInt(d_incr_range))));
		
		//make sure there are players to fire at
		if (dDragon.getWorld().getPlayers().isEmpty()) {
			//reschedule the task for a later time
			Bukkit.getScheduler().scheduleSyncDelayedTask(EnderDragonFridaysPlugin.plugin, this, time);
			return;
		}
		
		//there are players in the world
		
						
		//reschedule this event to run
		Bukkit.getScheduler().scheduleSyncDelayedTask(EnderDragonFridaysPlugin.plugin, this, time);
		
		//actually launch blindness 
		Bukkit.getPluginManager().callEvent(new BlindnessVeilEvent(boss.getEntity(), null, boss.getEntity().getLocation().add(offsetX, offsetY, offsetZ)));
		
		
	}
}
