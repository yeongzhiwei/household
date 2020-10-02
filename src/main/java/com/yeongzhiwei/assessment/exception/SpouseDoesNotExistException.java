package com.yeongzhiwei.assessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Spouse does not exist.")
public class SpouseDoesNotExistException extends RuntimeException {

    private static final long serialVersionUID = -6963012293045542828L;
    
}
