package com.blps.lab1.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PermissionDeniedException extends RuntimeException {
    private String message;
}
