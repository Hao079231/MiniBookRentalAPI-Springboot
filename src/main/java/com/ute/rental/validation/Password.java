package com.ute.rental.validation;

import com.ute.rental.constant.MiniBookConstant;
import com.ute.rental.validation.impl.PasswordValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidation.class)
@Documented
public @interface Password {
  boolean allowNull() default false;

  String pattern() default MiniBookConstant.PASSWORD_PATTERN;

  String message() default "The password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
