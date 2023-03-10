package com.i5e2.likeawesomevegetable.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AwesomeVegeAppException extends RuntimeException {
    private AppErrorCode errorCode;
    private String message;
}
