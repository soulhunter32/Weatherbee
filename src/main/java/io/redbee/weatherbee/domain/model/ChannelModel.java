package io.redbee.weatherbee.domain.model;

public class ChannelModel {

	private LocationModel location;
	private ItemModel item;

	public LocationModel getLocation() {
		return location;
	}

	public void setLocation(LocationModel location) {
		this.location = location;
	}

	public ItemModel getItem() {
		return item;
	}

	public void setItem(ItemModel item) {
		this.item = item;
	}

	@Override
	public String toString() {
		return "ChannelModel [item=" + item + "]";
	}
}
