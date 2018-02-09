package com.eportal.models.request;

import com.eportal.models.Skillsets;

public class SkillsetsRequest {
	public String name;
	public long experience;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getExperience() {
		return experience;
	}
	public void setExperience(long experience) {
		this.experience = experience;
	}
	
	public Skillsets toSkillsets(){
		Skillsets skillsets = new Skillsets();
		skillsets.setSkillset(this.getName());
		skillsets.setExperience(this.getExperience());
		return skillsets;
	}
	
	public SkillsetsRequest fromSkillsets(Skillsets skillset){
		SkillsetsRequest skillsetRequest = new SkillsetsRequest();
		skillsetRequest.setName(skillset.getSkillset());
		skillsetRequest.setExperience(skillset.getExperience());
		return this;
	}
}
