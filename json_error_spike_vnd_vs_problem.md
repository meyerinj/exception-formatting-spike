# How to standardize a JSON error responses in OPES microservices

## Overview

Explore ways to standardize error generation.

**Supports:**
[Link to JIRA story.](https://objectcomputing-projects.atlassian.net/browse/OPES2-366)

## Investigation

Initially recommended links were 

* Vnd.Error  https://github.com/blongden/vnd.error
* Zalando Problem https://github.com/zalando/problem

## Potential Solutions

As in the links the solutions are VND.error and the Zalando problem library

#### 1. VND.error

Level of Effort: 2-5 days

Teams Involved: Microservices

As the Micronaut JsonError class implements this and examples of its use already exist in opesservice I will here show only a variant of its use which as an abstract class which exception can extend

After creating a fairly simple HALParentException class I was able to create this
```
public class PhoneNotFoundExceptionHAL extends HalParentException {
    public PhoneNotFoundExceptionHAL(String message, HttpRequest request) {
        super(message, HttpStatus.CONFLICT);
        setRequestUri(request.getUri());
        setSelfTemplate(true);
    }
}
```

Which in my test controller will put out this json format:
```
{
    "_links": {
        "self": {
            "href": "/halError/?request=hal",
            "templated": true
        }
    },
    "message": "hal"
}
```

This way a single error handler can be implemented in micronaut-correlation that will handle outbound message formatting, as the info to format them will be contained in the exception class itself.
Pros: 
* Uses micronaut built-in tools. 
* Allows standardized handling across all projects.

Cons: 
* Involves some inelegant tweaking of micronaut JsonError to apply

#### 2. Problem builder in error handler

Level of Effort: 1-2 weeks

Teams Involved: Microservices, Mobile

With an error handler method of the form...
```
@Error(PhoneVerificationCodeDoesNotMatchException.class)
public HttpResponse doPCDNMError(PhoneVerificationCodeDoesNotMatchException pvcdnme) {
   return HttpResponse.status(HttpStatus.CONNECTION_TIMED_OUT).body(Problem.builder()
           .withDetail(pvcdnme.getMessage())
           .withStatus(Status.valueOf(HttpStatus.CONNECTION_TIMED_OUT.getCode()))
           .withTitle(PhoneVerificationCodeDoesNotMatchException.class.getSimpleName())
           .build()
   );
}
```
generates this json
```
{
    "type": "http://opes.pe/opesservice/exception/PhoneNotFoundException",
    "title": "Phone not found",
    "status": 502,
    "detail": "phone pnfe not found"
}
```

Pros: 
* Very straightforward and customizable builders usable in each handler
* Follows a clear standard

Cons:
* Requires a handler for each exception type

#### 3. Problem Throwable implementation

Level of Effort: 2 weeks

Teams Involved: Microservices, Mobile

Problem contains a ThrowableProblem type that exceptions can extend allowing code such as this

```
public class PhoneVerificationCodeDoesNotMatchExceptionProblem extends AbstractThrowableProblem {
    static final URI TYPE = URI.create("http://opes.pe/opesservice/exception/PhoneVerificationCodeDoesNotMatchException");
    public PhoneVerificationCodeDoesNotMatchExceptionProblem(String message) {
        super(TYPE, "Titular issue", Status.EXPECTATION_FAILED, message);
    }
}
```
Which generates the same Problem format as above but puts all formatting in the exception class meaning only one handler need ever be written.
Pros:
* Easy to use and generalizable
* Follows a known standard
* Surprisingly little extra code per exception

Cons:
* Some annoyances involved in translating the Problem status enum to the micronaut status enum

## Recommended Solution

Throwable problem is super convenient, and if there are cases where it cannot be used along with the builder/handler model.