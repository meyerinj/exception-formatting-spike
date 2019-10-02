package com.opes.errTest.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import java.net.URI;

public class PhoneVerificationCodeDoesNotMatchExceptionProblem extends AbstractThrowableProblem {
    static final URI TYPE = URI.create("http://opes.pe/opesservice/exception/PhoneVerificationCodeDoesNotMatchException");
    public PhoneVerificationCodeDoesNotMatchExceptionProblem(String message) {
        super(TYPE, "Titular issue", Status.EXPECTATION_FAILED, message);
    }
}
