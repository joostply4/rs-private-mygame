package com.rs.game.player.content.slayer;

/**
 * This is a list of assignable tasks
 * 
 * @author Emperial
 * 
 */
public enum SlayerTasks {
	//CHICKENS("Chickens", TaskSet.EASY, 0, 15, 50, "Chicken"), 
	//BEARS("Bears",TaskSet.EASY, 0, 15, 50, "Grizzly bear cub", "Grizzly bear"), 
	//COWS("Cows", TaskSet.EASY, 0, 15, 50, "Cow calf", "Cow"), 
	//GOBLINS("Goblins", TaskSet.EASY, 0, 15, 50, "Goblin", "Cave goblin guard"), 
	//ICEFIENDS("Icefiends", TaskSet.EASY, 0, 10, 20, "Icefiend"), 
	//MINOTAURS("Minotaurs", TaskSet.EASY, 0, 15, 50, "Minotaur"), 
	//GHOSTS("Ghosts", TaskSet.EASY, 0, 15, 50, "Ghost"), 
	//BATS("Bats", TaskSet.EASY, 0, 15, 50, "Bat", "Giant bat"), 
	//BIRDS("Birds", TaskSet.EASY, 0, 15, 50, "Chicken", "Terrorbird"),
	
	//Easy
	ROCK_CRABS("Rock Crabs", TaskSet.EASY, 0, 15, 70, "Rock Crab", "Giant Rock Crab"), 
	HILL_GIANTS("Hill Giants", TaskSet.EASY, 0, 15, 40, "Hill Giant"), 
	SKELETONS("Skeletons", TaskSet.EASY, 0, 15, 40, "Skeleton", "Giant Skeleton"), 
	ZOMBIES("Zombies", TaskSet.EASY, 0, 15, 50, "Zombie", "Armoured Zombie"), 
	SCORPIONS("Scorpions", TaskSet.EASY, 0, 15, 50, "Scorpion", "King Scorpion"), 
	CHAOS_DRUIDS("Chaos Druids", TaskSet.EASY, 0, 15, 50, "Chaos Druid"),
	
	//Medium
	BABY_BLUE_DRAGONS("Baby Blue Dragons", TaskSet.MEDIUM, 0, 50, 95, "Baby Blue Dragon"), 
	CHAOS_DWARVES("Chaos Dwarves", TaskSet.MEDIUM, 0, 50, 95, "Chaos Dwarf"), 
	MAGIC_AXE("Magic Axes", TaskSet.MEDIUM, 0, 50, 95, "Magic Axe"), 
	HOBGOBLINS("Hobgoblins", TaskSet.MEDIUM, 0, 50, 95, "Hobgoblin"),
	DEADLY_RED_SPIDERS("Deadly Red Spiders", TaskSet.MEDIUM, 0, 50, 95, "Deadly Red Spider"),
	
	//Hard
	LESSER_DEMONS("Lesser Demons", TaskSet.HARD, 0, 95, 199, "Lesser Demon"),
	BLUE_DRAGONS("Blue Dragons", TaskSet.HARD, 0, 25, 120, "Blue Dragon", "Baby Blue Dragon"),
	BLACK_DEMONS("Black Demons", TaskSet.HARD, 0, 95, 199, "Black Demon"),
	HELLHOUNDS("Hellhounds", TaskSet.HARD, 0, 95, 199, "Hellhound"),
	BRONZE_DRAGONS("Bronze Dragons", TaskSet.HARD, 0, 5, 75, "Bronze Dragon"),
	BLACK_DRAGONS("Black Dragons", TaskSet.HARD, 0, 5, 25, "Black Dragon", "King Black Dragon"),
	IRON_DRAGON("Iron Dragons", TaskSet.HARD, 0, 5, 120, "Iron Dragon"),
	STEEL_DRAGONS("Steel Dragons", TaskSet.HARD, 0, 5, 150, "Steel Dragon"),
	ELVARG_DRAGONS("Dragons Named Elvarg", TaskSet.HARD, 0, 5, 15, "Elvarg"),
	GREEN_DRAGONS("Green Dragons", TaskSet.HARD, 0, 25, 99, "Green Dragon"),
	
	//Slayer Only Monsters
	CRAWLING_HANDS("Crawling Hands", TaskSet.EASY, 5, 20, 100, "Crawling Hand"),
	BANSHEES("Banshees", TaskSet.EASY, 15, 15, 105, "Banshee", "Mighty Banshee"),
	INFERNAL_MAGES("Infernal Mages", TaskSet.MEDIUM, 45, 50, 150, "Infernal Mage"),
	BLOODVELDS("Bloodvelds", TaskSet.HARD, 50, 60, 170, "Bloodveld", "Mutated Bloodveld"),
	ABERRANT_SPECTRES("Aberrant Spectres", TaskSet.HARD, 60, 65, 180, "Aberrant Spectre"),
	GARGOYLES("Gargoyles", TaskSet.HARD, 75, 70, 190, "Gargoyle"),
	NECHRYAEL("Nechryaels", TaskSet.HARD, 80, 80, 199, "Nechryael"),
	ABYSSAL_DEMONS("Abyssal Demons", TaskSet.HARD, 85, 90, 225, "Abyssal Demon");
	
	private SlayerTasks(String simpleName, TaskSet type, int requiredLevel, int min, int max,
			String... monsters) {
		this.type = type;
		this.slayable = monsters;
		this.simpleName = simpleName;
		this.requiredLevel = requiredLevel;
		this.min = min;
		this.max = max;
	}
	
	public int requiredLevel;

	/**
	 * A simple name for the task
	 */
	public String simpleName;

	/**
	 * The task set
	 */
	public TaskSet type;
	/**
	 * The monsters that will effect this task
	 */
	public String[] slayable;
	/**
	 * The minimum amount of monsters the player may be assigned to kill
	 */
	public int min;
	/**
	 * The maximum amount of monsters the player may be assigned to kill
	 */
	public int max;
}
