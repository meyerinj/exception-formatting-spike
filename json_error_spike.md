# How to standardize a JSON error responses in OPES microservices

## Overview

JSON error services are dependent on differing documented standards and are currently not internally consistent. Investigate standardized error responses and come up with a solution that meets our requirements

**Supports:**
[Link to JIRA story.](https://objectcomputing-projects.atlassian.net/browse/OPES2-366)

## Investigation

Initially recommended links were 

* Error response https://tools.ietf.org/html/rfc6749#section-5.2
* Vnd.Error  https://github.com/blongden/vnd.error
* Zalando Problem https://github.com/zalando/problem

With additional recommendations stemming from those
* HAL JSON https://tools.ietf.org/html/draft-kelly-json-hal-08
* Problem error JSON https://tools.ietf.org/html/rfc7807

## Potential Solutions

Two of the recommended links are not to error formats *per se* but to libraries to conveniently create standardized exceptions. These techniques will be compared to using the built-in tools for serialization in Micronaut, and ways to incorporate them will be explored.

Since OIDC must conform with the error types in RFC 6749 any attempt to standardize error format must include that. However, this error type is very rigid and specific to the Open ID framework, making it unhelpful as a general error format. I believe that a solution must start with the Open ID error format and add on from there.

#### 1. JSON Format options

##### Open ID

Level of Effort: 2-5 days

Teams Involved: Microservices, Mobile
```
{
    "error":ErrorResponse enum, 
    "error_description":string, 
    "error_uri":string
}
```
Pros: 
* Simplest. 
* Tools for this already exist in OIDC.

Cons: 
* Entirely inextensible. The Error field is intended to be an enum, limiting options outside of OIDC.

##### Open ID + Problem JSON

Level of Effort: 1-2 weeks

Teams Involved: Microservices, Mobile

```
{
    "error":string, 
    "error_description":string, 
    "error_uri":string, 
    "type":uri, 
    "title":string, 
    "detail":string, 
    "instance":uri
}
```
Pros: 
* Highly extensible, the Problem format explicitly allows for extension and adding fields. 
* Allows for references to datatypes indicated in Open API

Cons:
* Contains de facto duplicate fields. The required "error" field in Open ID serves the same role as the required "type" field in Problem. Ditto for "error_description" and "detail"
* The value of data type reference largely overruled by the use of a standardized error type

##### HAL with embedded error type

Level of Effort: 1-2 weeks

Teams Involved: Microservices, Mobile

```
{
    "_links":{
        "self":{
            "href":uri
        },
        "other-relevant-link":{
            "href":uri
        }
    },
    "_embedded":{
        {
            "error":string, 
            "error_description":string, 
            "error_uri":string
        }
    }
}
```
Pros:
* Highly extensible. Any datatype can be embedded and a self.type field can be added which would be a link to the Open API datatype
* HAL is not only an exception JSON format, but any JSON message, so this could be standardized to all responses.
* This format is already used in Micronaut.

Cons:
* Presents a level of extensibility that largely defeats the purpose of standardization.

##### Custom Open ID extension

Level of Effort: 2-5 weeks

Teams Involved: Microservices, Mobile
```
{
    "error":string, 
    "error_description":string, 
    "error_uri":string,
    "type":uri,
    "payload":{
        "some-field":any,
        "some-other-field":any,
        ...
    }
}
```
Pros:
* Bespoke solution intended to fit our needs specifically
* Contains no duplicate fields while still having high degrees of extensibility
Cons:
* Follows no standard completely

#### 2. JSON Generation technique

##### Problem

Contains handy tool to create error JSON in the Problem format. This could be implemented either in the Exceptions, overriding their toString methods with the Problem builder or in the exception handler.

##### vnd.error

The JsonError object built into Micronaut uses a somewhat restricted variant of this format and examples of its use can be seen in opesservice. It contains all the fields necessary for most errors, but does not support the "_embedded" which precludes including the Open ID required fields

##### Custom error POJO

As Micronaut will serialize any POJO we can create a bespoke one in the correlation repo for use across OPES. Having this POJO extend a standard (such as Micronaut's JsonError) will allow us to take advantage of those standards while adding our own needs.

## Recommended Solution

I am torn between two favorites. 
* One would be a subclass of JsonError that adds the Open ID required fields. This would allow the format to be used across all of OPES while still largely fitting a standard format.
* We could abandon the hope of using the same format in OIDC as in the rest of OPES. This allows OIDC to use the RFC required format while the rest of OPES can use a standard vnd.error format.
