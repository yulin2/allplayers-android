package com.allplayers.android;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupData
{
/*  ["uuid",
	 "title",
	 "description",
	 "location",
	 "activity_level",
	 "list_in_directory",
	 "active",
	 "registration_fees_enabled",
	 "approved_for_payment",
	 "accept_amex",
	 "payee_uuid",
	 "approved_for_idverify",
	 "group_mates_enabled",
	 "comments",
	 "parent_approval_enabled",
	 "sort_order",
	 "override_default_group_permissions",
	 "primary_color",
	 "secondary_color",
	 "node_status",
	 "logo",
	 "uri",
	 "url",
	 "groups_above_uuid"]*/
	
	private String uuid;
	private String title;
	private String description;
	
	public GroupData(String jsonResult)
	{
		try
		{
			JSONObject groupObject = new JSONObject(jsonResult);
			uuid = groupObject.getString("uuid");
			title = groupObject.getString("title");
			description = groupObject.getString("description");
		}
		catch(JSONException ex)
		{
			System.out.println(ex);
		}
	}
	
	public String getUUID()
	{
		return uuid;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getDescription()
	{
		return description;
	}
}