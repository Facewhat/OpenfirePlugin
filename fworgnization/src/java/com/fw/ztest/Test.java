package com.fw.ztest;

import net.sf.json.JSONArray;

public class Test {
	public static void main(String[] args) {
		String groupId = "2";
		System.out.println(JSONArray.fromObject(groupId).toString());
	}
}
