package com.ute.rental.validation;

import com.ute.rental.constant.MiniBookConstant;
import com.ute.rental.validation.impl.PhoneValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidation.class)
@Documented
public @interface Phone {
  boolean allowNull() default false;

  String pattern() default MiniBookConstant.PHONE_PATTERN;

  String message() default "The phone number should begin with 0 and have exactly 10 digits";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
