package com.plantiss.mongoadapter.objects;

import java.util.ArrayList;

public class CheckinOptions {
	private boolean allowPartial;
	private ArrayList<String> ignoreFields= new ArrayList<String>(); 

	public boolean isAllowPartial() {
		return allowPartial;
	}

	public void setAllowPartial(boolean allowPartial) {
		this.allowPartial = allowPartial;
	}

	public ArrayList<String> getIgnoreFields() {
		return ignoreFields;
	}

	public void setIgnoreFields(ArrayList<String> ignoreFields) {
		this.ignoreFields = ignoreFields;
	}
	
	
	
}
