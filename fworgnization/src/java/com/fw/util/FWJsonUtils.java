package com.fw.util;

import java.util.ArrayList;
import java.util.List;


import com.fw.orgnization.entity.FWGroup;
import com.fw.orgnization.entity.FWGroupUser;

public class FWJsonUtils {
	public static OrgTreeNode FWGroupToOrgTreeNode(List<FWGroup> fwGroups){
		OrgTreeNode root = new OrgTreeNode();
		List<OrgTreeNode> nodes = new ArrayList<OrgTreeNode>();
		
		for (FWGroup fwGroup : fwGroups) 
		{
			if(fwGroup.getGroupfathername().equals("0"))
			{
				root.setText(fwGroup.getDisplayname());
				continue;
			}
			OrgTreeNode child = new OrgTreeNode();
			
			child.setText(fwGroup.getDisplayname());
			nodes.add(child);
		}
		root.setNodes(nodes);
		return root;
	}
	
	public static List<OrgTreeNode> FWUserToOrgTreeNodes(List<FWGroupUser> fwGroupUsers){
		List<OrgTreeNode> userNodes = new ArrayList<OrgTreeNode>();
		for (FWGroupUser fwGroupUser : fwGroupUsers) 
		{
			OrgTreeNode userNode = new OrgTreeNode();
			userNode.setText(fwGroupUser.getUsernickname());
			userNode.setNodes(null);
			userNodes.add(userNode);
		}
		return userNodes;
	}
}
