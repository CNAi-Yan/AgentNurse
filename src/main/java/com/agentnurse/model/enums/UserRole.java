package com.agentnurse.model.enums;

/**
 * 用户角色枚举
 */
public enum UserRole {
    /**
     * 患者
     */
    PATIENT("患者"),
    
    /**
     * 家属
     */
    FAMILY("家属"),
    
    /**
     * 护理员
     */
    CAREGIVER("护理员"),
    
    /**
     * 管理员
     */
    ADMIN("管理员");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
