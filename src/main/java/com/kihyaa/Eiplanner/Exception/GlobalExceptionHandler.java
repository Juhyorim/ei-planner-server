package com.kihyaa.Eiplanner.Exception;

import com.kihyaa.Eiplanner.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.InputMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(InputMismatchException.class)
  protected ResponseEntity handlerInputMismatchException(InputMismatchException e) {
    log.info("InputMismatchException = {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("입력값이 잘못되었습니다", e.getMessage()));
  }

  @ExceptionHandler(NotFoundException.class)
  protected ResponseEntity handlerNotFoundException(NotFoundException e) {
    log.info("NotFoundException = {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("데이터를 찾을 수 없습니다", e.getMessage()));
  }


}