package br.unb.unbiquitous.listView;

import br.unb.unbiquitous.ubiquitos.uos.driverManager.DriverData;

/**
 * 
 * @author ricardoandrade
 *
 */
public class Item  implements Comparable<Item> {
	
	/************************************************************
	 * VARIABLES
	 ************************************************************/
	
	private long id;
	private String caption;
	private boolean driverInUse;
	private DriverData driverData;

	
	/************************************************************
	 * CONSTRUCTOR
	 ************************************************************/
	
	public Item(long id, String caption, DriverData driverData) {
		super();
		this.id = id;
		this.caption = caption;
		this.driverData = driverData;
	}

	/************************************************************
	 * GETTERS AND SETTERS
	 ************************************************************/
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public int compareTo(Item other) {
		return (int) (id - other.getId());
	}

	public DriverData getDriverData() {
		return driverData;
	}

	public void setDriverData(DriverData driverData) {
		this.driverData = driverData;
	}

	public boolean isDriverInUse() {
		return driverInUse;
	}

	public void setDriverInUse(boolean driverInUse) {
		this.driverInUse = driverInUse;
	}

}
