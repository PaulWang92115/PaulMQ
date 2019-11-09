package com.paul.mq.consumer;

public class ClustersRelation {
	
	private String exchangeName;
	
	private ConsumerClusters clusters;
	
    ClustersRelation() {

    }

	public ClustersRelation(String exchangeName, ConsumerClusters clusters) {
		super();
		this.exchangeName = exchangeName;
		this.clusters = clusters;
	}

	

	public String getExchangeName() {
		return exchangeName;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	public ConsumerClusters getClusters() {
		return clusters;
	}

	public void setClusters(ConsumerClusters clusters) {
		this.clusters = clusters;
	}
	
	

}
