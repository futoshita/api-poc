package com.futoshita.jersey.server.entity.constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import com.futoshita.jersey.server.entity.User;

/**
 * Checks whether a given
 * {@link org.glassfish.jersey.examples.beanvalidation.webapp.domain.User}
 * entity has ID. Only return values are supposed to be annotated with this
 * annotation.
 *
 * @author Michal Gajdos
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { HasId.Validator.class, HasId.ListValidator.class })
public @interface HasId {
  
  String message() default "{com.futoshita.jersey.server.entity.constraint.HasId.message}";
  
  Class<?>[] groups() default {};
  
  Class<? extends Payload>[] payload() default {};
  
  public class Validator implements ConstraintValidator<HasId, User> {
    
    @Override
    public void initialize(final HasId hasId) {
    }
    
    @Override
    public boolean isValid(final User user, final ConstraintValidatorContext constraintValidatorContext) {
      return user == null || user.getId() != null;
    }
  }
  
  public class ListValidator implements ConstraintValidator<HasId, List<User>> {
    
    private Validator validator = new Validator();
    
    @Override
    public void initialize(final HasId hasId) {
    }
    
    @Override
    public boolean isValid(final List<User> users, final ConstraintValidatorContext constraintValidatorContext) {
      boolean isValid = true;
      for (final User user : users) {
        isValid &= validator.isValid(user, constraintValidatorContext);
      }
      return isValid;
    }
  }
}
