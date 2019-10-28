package com.paul.mq.consumer;
/**
 * 
 * 集群状态管理类
 *
 */
public class ClustersState {
	
    public static final int ERROR = 1;
    public static final int SUCCESS = 0;
    public static final int NETWORKERR = -1;

	//集群 id
	private String clusters;
	//状态
	private int state;
	
	
	public ClustersState(String clusters, int state) {
		this.clusters = clusters;
		this.state = state;
	}
	
	public String getClusters() {
		return clusters;
	}
	public void setClusters(String clusters) {
		this.clusters = clusters;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	
}
