package com.example.eventure.dto;

public class PasswordChangeDTO {
    private String oldPassword;
    private String newPasswordFirst;
    private String newPasswordSecond;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPasswordFirst() {
        return newPasswordFirst;
    }

    public void setNewPasswordFirst(String newPasswordFirst) {
        this.newPasswordFirst = newPasswordFirst;
    }

    public String getNewPasswordSecond() {
        return newPasswordSecond;
    }

    public void setNewPasswordSecond(String newPasswordSecond) {
        this.newPasswordSecond = newPasswordSecond;
    }

    public PasswordChangeDTO(String oldPassword, String newPasswordFirst, String newPasswordSecond) {
        this.oldPassword = oldPassword;
        this.newPasswordFirst = newPasswordFirst;
        this.newPasswordSecond = newPasswordSecond;
    }

    public PasswordChangeDTO() {
    }
}
