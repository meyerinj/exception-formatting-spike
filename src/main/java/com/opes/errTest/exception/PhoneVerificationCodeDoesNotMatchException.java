package com.opes.errTest.exception;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotBlank;

/**
 * Exception thrown when phone is found but verification code does not match.
 */
public class PhoneVerificationCodeDoesNotMatchException extends RuntimeException {
    /**
     * Phone which was not found.
     */
    @Nonnull
    private String phone;

    @Nonnull
    @NotBlank
    private String verificationCode;

    /**
     *
     * @param phone Phone which was not found in E164.
     * @param verificationCode Verification code
     */
    public PhoneVerificationCodeDoesNotMatchException(@Nonnull String phone,
                                                      @Nonnull @NotBlank String verificationCode) {
        this.phone = phone;
        this.verificationCode = verificationCode;
    }

    @Nonnull
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nonnull String phone) {
        this.phone = phone;
    }


    @Nonnull
    @NotBlank
    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(@Nonnull @NotBlank String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
