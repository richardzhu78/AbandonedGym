package textadventure;import items.Container;/** * A Player can contain objects and knows the Room it's currently in. */public class Player extends Container {	/** Room this Player is in at the moment */	private Room currentRoom;		/** Creates a player that starts in the given Room with no items */	public Player(Room startRoom, World world) {		super(world, "Player", "You are looking good today!");		currentRoom = startRoom;	}	/* Getters and setters */	public Room getCurrentRoom() {		return currentRoom;	}	public void setCurrentRoom(Room newRoom) {		currentRoom = newRoom;	}}