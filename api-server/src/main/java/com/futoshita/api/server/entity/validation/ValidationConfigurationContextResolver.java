package com.futoshita.api.server.entity.validation;

import com.futoshita.api.server.service.LocaleThreadLocal;
import org.glassfish.jersey.server.validation.ValidationConfig;
import org.glassfish.jersey.server.validation.internal.InjectingConstraintValidatorFactory;

import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.Validation;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Custom configuration of validation. This configuration defines custom:
 * <ul>
 * <li>ConstraintValidationFactory - so that validators are able to inject
 * Jersey providers/resources.</li>
 * <li>ParameterNameProvider - if method input parameters are invalid, this
 * class returns actual parameter names instead of the default ones
 * ({@code arg0, arg1, ..})</li>
 * </ul>
 */
public class ValidationConfigurationContextResolver implements ContextResolver<ValidationConfig> {

    @Context
    private ResourceContext resourceContext;

    @Override
    public ValidationConfig getContext(final Class<?> type) {
        return new ValidationConfig()
                .constraintValidatorFactory(resourceContext.getResource(InjectingConstraintValidatorFactory.class))
                .parameterNameProvider(new CustomParameterNameProvider())
                .messageInterpolator(new LocaleSpecificMessageInterpolator(Validation.byDefaultProvider().configure().getDefaultMessageInterpolator()));
    }

    private class CustomParameterNameProvider implements ParameterNameProvider {

        private final ParameterNameProvider nameProvider;

        public CustomParameterNameProvider() {
            nameProvider = Validation.byDefaultProvider().configure().getDefaultParameterNameProvider();
        }

        @Override
        public List<String> getParameterNames(final Constructor<?> constructor) {
            return nameProvider.getParameterNames(constructor);
        }

        @Override
        public List<String> getParameterNames(final Method method) {
            if ("addUser".equals(method.getName())) {
                return Arrays.asList("user");
            }
            return nameProvider.getParameterNames(method);
        }
    }

    private class LocaleSpecificMessageInterpolator implements MessageInterpolator {

        private final MessageInterpolator defaultInterpolator;

        public LocaleSpecificMessageInterpolator(MessageInterpolator interpolator) {
            this.defaultInterpolator = interpolator;
        }

        @Override
        public String interpolate(String message, Context context) {
            return defaultInterpolator.interpolate(message, context, LocaleThreadLocal.get());
        }

        @Override
        public String interpolate(String message, Context context, Locale locale) {
            return defaultInterpolator.interpolate(message, context, locale);
        }
    }
}