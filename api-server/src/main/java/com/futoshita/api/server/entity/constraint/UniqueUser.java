package com.futoshita.api.server.entity.constraint;

import com.futoshita.api.server.entity.User;
import com.futoshita.api.server.service.StorageService;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UniqueUser.Validator.class})
public @interface UniqueUser {

    Logger LOGGER = LoggerFactory.getLogger(UniqueUser.class);

    String message() default "{user.non.unique}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    public class Validator implements ConstraintValidator<UniqueUser, User> {

        @Override
        public void initialize(final UniqueUser unique) {
        }

        @Override
        public boolean isValid(final User user, final ConstraintValidatorContext constraintValidatorContext) {
            try {
                if (user == null || user.getIdent() == null || user.getEmail() == null) {
                    return true;
                }

                boolean isValid = true;
                String messageTemplate = null;
                String attribute = null;
                String value = null;

                List<User> existingUsers = StorageService.findByEmail(user.getEmail());
                for (User existingUser : existingUsers) {
                    if (!user.equals(existingUser)) {
                        isValid = false;
                        messageTemplate = "{user.non.unique.email}";
                        attribute = "email";
                        value = user.getEmail();

                        break;
                    }
                }

                existingUsers = StorageService.findByIdent(user.getIdent());
                for (User existingUser : existingUsers) {
                    if (!user.equals(existingUser)) {
                        isValid = false;
                        messageTemplate = "{user.non.unique.ident}";
                        attribute = "ident";
                        value = user.getIdent();

                        break;
                    }
                }

                if (!isValid) {
                    HibernateConstraintValidatorContext hibernateConstraintValidatorContext = constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class);

                    hibernateConstraintValidatorContext.disableDefaultConstraintViolation();
                    hibernateConstraintValidatorContext
                            .addExpressionVariable(attribute, value)
                            .buildConstraintViolationWithTemplate(messageTemplate)
                            .addPropertyNode(attribute)
                            .addConstraintViolation();
                }

                return isValid;
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }

            return false;
        }
    }
}
