package com.workoss.boot.storage.web.advice;

import com.workoss.boot.storage.exception.StorageException;
import com.yifengx.popeye.util.StringUtils;
import com.yifengx.popeye.util.model.ResultCode;
import com.yifengx.popeye.util.model.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 *
 * @author workoss
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

	private final MessageSource messageSource;

	public GlobalExceptionHandlerAdvice(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ MethodArgumentNotValidException.class })
	public ResultInfo validationErrorHandler(MethodArgumentNotValidException exception) {
		String errorInfo = exception.getBindingResult().getAllErrors().stream().map(error -> {
			log.warn("【VALID】参数校验异常 {}:{}", error.getObjectName(), error.getDefaultMessage());
			return error.getDefaultMessage();
		}).collect(Collectors.joining(";"));
		return ResultInfo.result(ResultCode.VALID_ERROR.getCode(), errorInfo);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ ConstraintViolationException.class })
	public ResultInfo validationErrorHandler(ConstraintViolationException exception) {
		String errorInfo = exception.getConstraintViolations().stream().map(error -> {
			log.warn("【VALID】参数校验异常 {}:{}", error.getPropertyPath(), error.getMessage());
			return error.getMessage();
		}).collect(Collectors.joining(";"));
		return ResultInfo.result(ResultCode.VALID_ERROR.getCode(), errorInfo);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ WebExchangeBindException.class })
	public ResultInfo validationErrorHandler(WebExchangeBindException exception) {
		String errorInfo = exception.getBindingResult().getAllErrors().stream().map(error -> {
			log.warn("【VALID】参数校验异常 {}:{}", error.getObjectName(), error.getDefaultMessage());
			return error.getDefaultMessage();
		}).collect(Collectors.joining(";"));
		return ResultInfo.result(ResultCode.VALID_ERROR.getCode(), errorInfo);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ IllegalArgumentException.class })
	public ResultInfo defaultException(IllegalArgumentException exception) {
		log.warn("【VALID】参数校验异常 {}", exception.getMessage());
		return ResultInfo.result(ResultCode.VALID_ERROR.getCode(), exception.getMessage());
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler({ StorageException.class })
	public ResultInfo serviceException(StorageException exception) {
		String errMsg = exception.getErrmsg();
		if (StringUtils.isBlank(errMsg)) {
			errMsg = messageSource.getMessage(exception.getErrcode(), null, LocaleContextHolder.getLocale());
		}
		log.warn("服务异常: errCode:{} errMsg:{}", exception.getErrcode(), errMsg);
		return ResultInfo.result(exception.getErrcode(), errMsg);
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler({ Throwable.class })
	public ResultInfo otherServiceException(Throwable throwable) {
		log.error("服务异常:", throwable);
		return ResultInfo.result(ResultCode.SERVER_ERROR.getCode(),
				throwable.getMessage() != null ? throwable.getMessage() : ResultCode.SERVER_ERROR.getMessage());
	}

}
