package com.opes.errTest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opes.errTest.exception.PhoneNotFoundException;
import com.opes.errTest.exception.PhoneVerificationCodeDoesNotMatchException;
import com.opes.errTest.exception.PhoneVerificationCodeDoesNotMatchExceptionProblem;
import io.micronaut.context.annotation.Bean;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import java.net.URI;

@Controller("problemError")
public class ProblemErrorController {
    @Bean
    public ObjectMapper objectMapper(ObjectMapper om) {
        return om.registerModule(new ProblemModule());
    }

    @Get
    public HttpResponse doProblemError(@QueryValue("request") String request) {
        if (request.equalsIgnoreCase("pnfe")) {
            throw new PhoneNotFoundException(request);
        }
        else if (request.equalsIgnoreCase("pvcdnme")) {
            throw new PhoneVerificationCodeDoesNotMatchException(request, "123456");
        }
        else if (request.equalsIgnoreCase("problem")) {
            throw new PhoneVerificationCodeDoesNotMatchExceptionProblem(request);
        }
        return HttpResponse.ok().body(request);
    }

    @Error(ThrowableProblem.class)
    public HttpResponse doGenericProblem(ThrowableProblem tp) {
        return HttpResponse.status(HttpStatus.valueOf(tp.getStatus().getStatusCode())).body(tp);
    }

    @Error(PhoneNotFoundException.class)
    public HttpResponse doPNFError(PhoneNotFoundException pnfe) {
        Problem p = Problem.builder()
                .withTitle("Phone not found")
                .withDetail(pnfe.getMessage())
                .withStatus(Status.BAD_GATEWAY)
                .withType(URI.create("http://opes.pe/opesservice/exception/PhoneNotFoundException"))
                .build();
        return HttpResponse.status(HttpStatus.valueOf(p.getStatus().getStatusCode())).body(p);
    }

    @Error(PhoneVerificationCodeDoesNotMatchException.class)
    public HttpResponse doPCDNMError(PhoneVerificationCodeDoesNotMatchException pvcdnme) {
        return HttpResponse.status(HttpStatus.CONNECTION_TIMED_OUT).body(Problem.builder()
                .withDetail(pvcdnme.getMessage())
                .withStatus(Status.valueOf(HttpStatus.CONNECTION_TIMED_OUT.getCode()))
                .withTitle(PhoneVerificationCodeDoesNotMatchException.class.getSimpleName())
                .build()
        );
    }
}
