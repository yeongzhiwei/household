package com.yeongzhiwei.assessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Could not find household")
public class HouseholdNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -1864648520695576229L;
    
}
