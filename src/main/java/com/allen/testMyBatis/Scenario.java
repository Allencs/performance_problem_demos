package com.allen.testMyBatis;

import java.util.Date;

public class Scenario {

    Integer id;
    String scenarioSkillCode;
    Integer scenarioId;
    Integer skillState;
    String skillAuthor;
    Date createTime;
    String updateBy;
    Date updateTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getScenarioSkillCode() {
        return scenarioSkillCode;
    }

    public void setScenarioSkillCode(String scenarioSkillCode) {
        this.scenarioSkillCode = scenarioSkillCode;
    }

    public Integer getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Integer scenarioId) {
        this.scenarioId = scenarioId;
    }

    public Integer getSkillState() {
        return skillState;
    }

    public void setSkillState(Integer skillState) {
        this.skillState = skillState;
    }

    public String getSkillAuthor() {
        return skillAuthor;
    }

    public void setSkillAuthor(String skillAuthor) {
        this.skillAuthor = skillAuthor;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "id=" + id +
                ", scenarioSkillCode='" + scenarioSkillCode + '\'' +
                ", scenarioId=" + scenarioId +
                ", skillState=" + skillState +
                ", skillAuthor='" + skillAuthor + '\'' +
                ", createTime=" + createTime +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
