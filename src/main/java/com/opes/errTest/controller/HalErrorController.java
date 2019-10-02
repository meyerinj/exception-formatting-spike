package com.opes.errTest.controller;

import com.opes.errTest.exception.HalParentException;
import com.opes.errTest.exception.PhoneNotFoundException;
import com.opes.errTest.exception.PhoneNotFoundExceptionHAL;
import com.opes.errTest.exception.PhoneVerificationCodeDoesNotMatchException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.hateos.JsonError;
import io.micronaut.http.hateos.Link;

@Controller("/halError")
public class HalErrorController {

    @Get
    public HttpResponse doHALError(@QueryValue("request") String request, HttpRequest http) {
        if (request.equalsIgnoreCase("pnfe")) {
            throw new PhoneNotFoundException(request);
        }
        else if (request.equalsIgnoreCase("pvcdnme")) {
            throw new PhoneVerificationCodeDoesNotMatchException(request, "123456");
        }
        else if (request.equalsIgnoreCase("hal")) {
            throw new PhoneNotFoundExceptionHAL(request, http);
        }
        return HttpResponse.ok().body(request);
    }

    @Error(HalParentException.class)
    public HttpResponse doGenericHAL(HalParentException hpe) {
        return HttpResponse.status(hpe.getStatus()).body(hpe.buildError());
    }

    @Error(PhoneNotFoundException.class)
    public HttpResponse doPNFError(HttpRequest request, PhoneNotFoundException pnfe) {
        JsonError je = new JsonError(pnfe.getMessage());
        je.link(Link.SELF, Link.build(
                request.getUri())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .templated(true)
                .build());
        return HttpResponse.badRequest().body(je);
    }

    @Error(PhoneVerificationCodeDoesNotMatchException.class)
    public HttpResponse doPCDNMError(HttpRequest request, PhoneVerificationCodeDoesNotMatchException pvcdnme) {
        JsonError je = new JsonError(pvcdnme.getMessage());
        je.link(Link.SELF, Link.of(request.getUri()));
        return HttpResponse.unprocessableEntity().body(je);
    }
}
