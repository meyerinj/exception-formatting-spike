package com.opes.errTest.exception;


import javax.annotation.Nonnull;

/**
 * Exception thrown when phone is not found.
 */
public class PhoneNotFoundException extends RuntimeException {

    /**
     * Phone which was not found.
     */
    @Nonnull
    private String phone;

    /**
     *
     * @param phone Phone which was not found in E164.
     */
    public PhoneNotFoundException(@Nonnull String phone) {
        super("phone " + phone + " not found");
        this.phone = phone;
    }

    @Nonnull
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nonnull String phone) {
        this.phone = phone;
    }
}
