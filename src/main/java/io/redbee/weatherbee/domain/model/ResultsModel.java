package io.redbee.weatherbee.domain.model;

public class ResultsModel {

	private ChannelModel channel;

	public ChannelModel getChannel() {
		return channel;
	}

	public void setChannel(ChannelModel channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "ResultsModel [channel=" + channel + "]";
	}
}

