package com.fw.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;



public class OrgTreeNode {
	private String text;
	private List<OrgTreeNode> nodes;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<OrgTreeNode> getNodes() {
		return nodes;
	}
	public void setNodes(List<OrgTreeNode> nodes) {
		this.nodes = nodes;
	}
	
}
