package es.uvigo.esei.dgss.exercises.domain.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidAddressValidator.class)
@Documented
public @interface ValidAddress {
	String message() default "{es.uvigo.esei.dgss.exercises.domain.constraints.ValidAddress.message}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
