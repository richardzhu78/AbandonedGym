package textadventure;/* * Class Room - a room in an adventure game. * * Author:  Michael Kolling * Version: 1.1 * Date:    August 2000 *  * This class is part of Zork. Zork is a simple, text based adventure game. * * "Room" represents one location in the scenery of the game.  It is  * connected to at most four other rooms via exits.  The exits are labelled * north, east, south, west.  For each direction, the room stores a reference * to the neighbouring room, or null if there is no exit in that direction. */import java.util.ArrayList;import java.util.Collection;import java.util.HashMap;import java.util.Iterator;import java.util.List;import java.util.Set;import interfaces.Container;import interfaces.Lockable;import items.CloseableContainer;import items.Item;import items.Scenery;public class Room implements Container, Lockable {	/** The title of this room diplayed in the game (usually CAPITALIZED) */	private String name;		/**	 * A rich description of this Room.  Note that if a player takes Items	 * from this room, you will need to update the description so it doesn't	 * include things that are no longer in the room.  Because of this, you	 * should generally only include Scenery and NON-TAKEABLE items in the	 * room description.	 */	private String description;	/** Stores a map of exits this room leads to */	private HashMap<String, Room> exits;	/** Stores the Items contained in this room as required by the Container interface. */	private HashMap<String, Item> items; // stores items in this room	/** Whether this Room is locked.  If locked, players can't travel to or from this room. */	private boolean isLocked;	/**	 * Create an unlocked room with the given title name and description.  Titles should	 * be CAPITALIZED by convention and descriptions should be rich with imagery and	 * detail - but not overboard.  Also, it's not a good idea to include TAKEABLE	 * items in the description because if a plpayer takes that item and moves it	 * to another room, your description will either be wrong or need to be updated.	 * You can, however, safely include Scenery items and NON-TAKEABLE items.	 */	public Room(String name, String description) {		this(name, description, false);	}	/**	 * Same as previous constructor but you can choose lockable or unlockable.	 */	public Room(String name, String description, boolean isLocked) {		this.name = name;		this.description = description;		this.isLocked = isLocked;		exits = new HashMap<String, Room>();		items = new HashMap<String, Item>();	}	/**	 * Define the exits of this room. Every direction either leads to another room	 * or is null (no exit there).	 */	public void setExits(Room north, Room east, Room south, Room west) {		if (north != null)			exits.put("north", north);		if (east != null)			exits.put("east", east);		if (south != null)			exits.put("south", south);		if (west != null)			exits.put("west", west);	}	/**	 * Updates the room description, which may be needed if the state of the room changes.	 */	public void setDescription(String newDescription) {		description = newDescription;	}//	/**//	 * Don't use this method because the Room name is used as a key in a HashMap and if you//	 * change it then you can mess up the HashMap links.//	 *///	public void setName(String newName) {//		name = newName;//	}		/**	 * Add an item to this room.	 */	@Override	public void addItem(Item item) {		items.put((String) item.getName(), item);	}	/**	 * Remove an item from this room, given only the item's name, and returns the Item removed	 */	@Override	public Item removeItem(String itemName) {		return items.remove(itemName);	}	/**	 * Remove an item from this room and returns the Item removed	 */	@Override	public Item removeItem(Item item) {		return items.remove(item.getName());	}    /** Returns an Item held by this Container, given the Item's name, and returns null if not found. */	@Override	public Item getItem(String itemName) {		// If the item is directly in this room, return it		if (items.get(itemName) != null)			return items.get(itemName);		// Otherwise check any open containers in this room for the item		else {			Set<String> keys = items.keySet();			Iterator<String> iter = keys.iterator();			while (iter.hasNext()) {				Item tempItem = items.get(iter.next());				if (tempItem instanceof CloseableContainer) {					if (((CloseableContainer) tempItem).isOpen() && ((Container)tempItem).hasItem(itemName)) {						return ((Container) tempItem).getItem(itemName);					}				}			}			return null;		}	}			/**	 * Returns whether this room has an item or not, given the Item's name	 */	@Override	public boolean hasItem(String itemName) {		boolean roomHasItem = items.containsKey(itemName);		// If the item is directly in this room return true		if (roomHasItem) {			return true;		}		// Otherwise check any open containers in this room for the item		else {			Set<String> keys = items.keySet();			Iterator<String> iter = keys.iterator();			while (iter.hasNext()) {				Item tempItem = items.get(iter.next());				if (tempItem instanceof Container) {					if (((Container)tempItem).hasItem(itemName)) {  // Casting is safe because we already checked that it's a Container						return true;					}				}			}			return false;					}	}	@Override	public boolean hasItem(Item item) {		return hasItem(item.getName());	}		/**	 * Returns whether this Room is locked or not	 */	@Override	public boolean isLocked() {		return isLocked;	}		/**	 * Locks this Room	 */	@Override	public void doLock() {		isLocked = true;	}		/**	 * Unlocks this Room	 */	@Override	public void doUnlock() {		isLocked = false;	}	/**	 * Return the description of the room (the one that was defined in the	 * constructor).	 */	public String shortDescription() {		return description;	}	public String getName() {		return name;	}	/**	 * Return a long description of this room, on the form: You are in the kitchen.	 * Exits: north west	 */	public String longDescription() {		String result = name + "\n" + description;		if (!getItemString().equals(""))			result += getItemString() + "\n";		if (exitString().equals("Exits: "))			result += exitString() + " n/a\n\n";		else			result += exitString() + "\n\n";		return result;	}		/**	 * Return a string describing the room's exits, for example "Exits: north west	 * ".	 */	private String exitString() {		String returnString = "Exits: ";		String tempString = "";		Set<String> keys = exits.keySet();		for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {			tempString = (String) iter.next();			returnString += " " + tempString;		}		return returnString;	}	/**	 * Return a string describing the room's non-scenery items	 */	@Override	public String getItemString() {		Collection<Item> mapValues = items.values();		Iterator<Item> roomItemIterator = mapValues.iterator();		// Make a list of non-scenery items		List<Item> roomItems = new ArrayList<Item>();		while (roomItemIterator.hasNext()) {			Item item = roomItemIterator.next();			if (!(item instanceof Scenery)) {				roomItems.add(item);			}		}		String items = "";		// Generates a String of non-scenery items in this room.		for (int i = 0; i < roomItems.size(); i++) {			// If more than 2 item, print "and" before last item			if (i != 0 && i == roomItems.size() - 1)				items += " and";			// If more than 2 items, separate items by a comma			else if (roomItems.size() > 1 && i > 0 && i < roomItems.size() - 1)				items += ",";			Item item = roomItems.get(i);			items += " " + item.getName();		}		if (!items.equals(""))			return "You can see a" + items + ".";		else			return "";	}	/**	 * Return the room that is reached if we go from this room in direction	 * "direction". If there is no room in that direction, return null.	 */	public Room nextRoom(String direction) {		return (Room) exits.get(direction);	}}