package com.kihyaa.Eiplanner.exception;

import com.kihyaa.Eiplanner.dto.response.ApiResponse;
import com.kihyaa.Eiplanner.exception.exceptions.ConflictException;
import com.kihyaa.Eiplanner.exception.exceptions.ForbiddenException;
import com.kihyaa.Eiplanner.exception.exceptions.InternalServerErrorException;
import com.kihyaa.Eiplanner.exception.exceptions.NotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.InputMismatchException;
import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler({InputMismatchException.class, ValidationException.class, MethodArgumentNotValidException.class})
  protected ResponseEntity<ApiResponse> handleBadRequestExceptions(Exception e) {
    log.info("Exception = {}", e.getMessage());

    if (e instanceof MethodArgumentNotValidException ex) {
      // 모든 예외 메시지를 리스트로 수집
      List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
              .map(FieldError::getDefaultMessage)
              .collect(Collectors.toList());

      // 리스트를 하나의 문자열로 합침
      String joinedErrorMessage = String.join(", ", errorMessages);

      return ApiResponse.createResponse(joinedErrorMessage, HttpStatus.BAD_REQUEST);
    }
    return ApiResponse.createResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  // NotFoundException 핸들링
  @ExceptionHandler(NotFoundException.class)
  protected ResponseEntity<ApiResponse> handleNotFoundException(NotFoundException e) {
    log.info("NotFoundException = {}", e.getMessage());
    return ApiResponse.createResponse(MessageCode.NOT_FOUND, HttpStatus.NOT_FOUND);
  }

  // ForbiddenException 핸들링
  @ExceptionHandler(ForbiddenException.class)
  protected ResponseEntity<ApiResponse> handleForbiddenException(ForbiddenException e) {
    log.info("ForbiddenException = {}", e.getMessage());
    return ApiResponse.createResponse(MessageCode.FORBIDDEN, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(ConflictException.class)
  protected ResponseEntity<ApiResponse> handleConflictException(ConflictException e) {
    log.info("ConflictException = {}", e.getMessage());
    return ApiResponse.createResponse(e.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(InternalServerErrorException.class)
  protected ResponseEntity<ApiResponse> handleInternalServerErrorException(InternalServerErrorException e) {
    log.info("InternalServerErrorException = {}", e.getMessage());
    return ApiResponse.createResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}