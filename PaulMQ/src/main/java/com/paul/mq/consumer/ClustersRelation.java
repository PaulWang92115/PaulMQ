package com.paul.mq.consumer;

public class ClustersRelation {
	
	private String clusterId;
	
	private ConsumerClusters clusters;
	
    ClustersRelation() {

    }

	public ClustersRelation(String clusterId, ConsumerClusters clusters) {
		super();
		this.clusterId = clusterId;
		this.clusters = clusters;
	}

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

	public ConsumerClusters getClusters() {
		return clusters;
	}

	public void setClusters(ConsumerClusters clusters) {
		this.clusters = clusters;
	}
	
	

}
