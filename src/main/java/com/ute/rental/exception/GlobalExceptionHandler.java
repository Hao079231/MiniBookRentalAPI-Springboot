package com.ute.rental.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ute.rental.dto.ApiMessageDto;
import com.ute.rental.dto.ErrorCode;
import com.ute.rental.form.ErrorForm;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  private static final String BAD_REQUEST = "BAD REQUEST";
  final ObjectMapper mapper = new ObjectMapper();
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiMessageDto<String>> globalExceptionHandler(NotFoundException ex){
    log.error("=====> " + ex.getMessage(), ex);
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    apiMessageDto.setCode(ex.getCode());
    apiMessageDto.setResult(false);
    apiMessageDto.setMessage(ex.getMessage());
    return new ResponseEntity<>(apiMessageDto, HttpStatus.NOT_FOUND);
  }

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    apiMessageDto.setCode("Error handleNoHandlerFoundException");
    apiMessageDto.setResult(false);
    apiMessageDto.setMessage("[Ex3]: 404");
    return new ResponseEntity<>(apiMessageDto, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiMessageDto<String>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    apiMessageDto.setCode("ERROR forbidden");
    apiMessageDto.setResult(false);
    apiMessageDto.setMessage(ex.getMessage());
    return new ResponseEntity<>(apiMessageDto, HttpStatus.FORBIDDEN);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ApiMessageDto<List<ErrorForm>> exceptionHandler(Exception ex) {
    log.error("===> " +ex.getMessage(), ex);
    ApiMessageDto<List<ErrorForm>> apiMessageDto = new ApiMessageDto<>();
    apiMessageDto.setCode("ERROR");
    apiMessageDto.setResult(false);
    if(ex instanceof MyBindingException){
      try {
        List<ErrorForm> errorForms = Arrays.asList(mapper.readValue(ex.getMessage(), ErrorForm[].class));
        apiMessageDto.setData(errorForms);
        apiMessageDto.setMessage("Invalid form");
      }catch (Exception e){
        log.error(e.getMessage());
      }
    }else{
      apiMessageDto.setMessage("[Ex2]: "+ex.getMessage());
    }
    return apiMessageDto;
  }

  @ExceptionHandler({UnauthorizationException.class})
  public ResponseEntity<ApiMessageDto<String>> notAllow(UnauthorizationException ex) {
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    apiMessageDto.setResult(false);
    apiMessageDto.setMessage(ex.getMessage());
    return new ResponseEntity<>(apiMessageDto, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler({BadRequestException.class})
  public ResponseEntity<ApiMessageDto<String>> badRequest(BadRequestException ex) {
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    apiMessageDto.setResult(false);
    apiMessageDto.setCode(ex.getCode());
    apiMessageDto.setMessage(ex.getMessage());
    return new ResponseEntity<>(apiMessageDto, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({ DataAccessException.class})
  public ResponseEntity<ApiMessageDto<String>> databaseError(DataAccessException ex) {
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    apiMessageDto.setResult(false);
    apiMessageDto.setMessage("Database Error");
    apiMessageDto.setCode(ErrorCode.ERROR_DB_QUERY);
    return new ResponseEntity<>(apiMessageDto, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(CustomizeOverallException.class)
  public ResponseEntity<ApiMessageDto<String>> handleInternalExceptionAll(CustomizeOverallException ex, WebRequest request) {
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    apiMessageDto.setCode("internalServerError");
    apiMessageDto.setResult(false);
    apiMessageDto.setMessage(ex.getMessage());
    return new ResponseEntity<>(apiMessageDto, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
