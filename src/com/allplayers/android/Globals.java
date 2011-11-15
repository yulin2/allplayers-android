package com.allplayers.android;

import java.util.ArrayList;

/* This class is used for storing global variables across activities.
 * Also, update timestamps should be kept to avoid making too many unnecessary calls, such as the 
 * getUserGroups() call for several activities.
 */
public class Globals
{
	//public static ArrayList<GroupData> groupList = new ArrayList<GroupData>();
	public static GroupData currentGroup = new GroupData();
}