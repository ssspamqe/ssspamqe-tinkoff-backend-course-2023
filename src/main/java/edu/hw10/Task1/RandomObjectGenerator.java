package edu.hw10.Task1;

import edu.hw10.Task1.annotations.Max;
import edu.hw10.Task1.annotations.Min;
import edu.hw10.Task1.annotations.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class RandomObjectGenerator {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final RandomObjectGenerator ROG = new RandomObjectGenerator();

    private static final Method ANNOTATION_MIN_VALUE;
    private static final Method ANNOTATION_MAX_VALUE;

    static {
        try {
            ANNOTATION_MIN_VALUE = Min.class.getMethod("value");
            ANNOTATION_MAX_VALUE = Max.class.getMethod("value");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Object nextObject(Class<?> objectClass) {
        var constructor = getConstructorWithBiggestParameterAmount(objectClass);
        var parameters = constructor.getParameters();
        var arguments = generateAllArguments(parameters);

        try {
            return constructor.newInstance(arguments.toArray(new Object[0]));
        } catch (Exception ex) {
            LOGGER.warn(ex);
            return null;
        }

    }

    public Object nextObject(Class<?> objectClass, String fabricMethodName) {
        var fabricMethod = getMethodWithBiggestParametersAmountWithName(fabricMethodName, objectClass);
        var parameters = fabricMethod.getParameters();
        var arguments = generateAllArguments(parameters);

        try {
            return fabricMethod.invoke(null, arguments.toArray(new Object[0]));
        } catch (Exception ex) {
            LOGGER.warn(ex);
            return null;
        }
    }

    private Method getMethodWithBiggestParametersAmountWithName(String name, Class<?> objectClass) {
        var methods = objectClass.getMethods();
        return Arrays.stream(methods)
            .filter(it -> it.getName().equals(name))
            .reduce((x, y) -> {
                if (x.getParameterCount() > y.getParameterCount()) {
                    return x;
                } else {
                    return y;
                }
            })
            .orElseThrow(() -> new IllegalArgumentException(
                objectClass + " do not have static fabric method with such name"));
    }

    private Constructor<?> getConstructorWithBiggestParameterAmount(Class<?> objectClass) {
        var constructors = objectClass.getConstructors();
        var biggestConstructor = constructors[0];

        for (var c : constructors) {
            if (c.getParameterCount() > biggestConstructor.getParameterCount()) {
                biggestConstructor = c;
            }
        }

        return biggestConstructor;
    }

    private List<Object> generateAllArguments(Parameter[] argumentTypes) {
        return Arrays.stream(argumentTypes).map(this::generateArgument).toList();
    }

    private Object generateArgument(Parameter parameter) {
        var annotations = parameter.getAnnotations();
        ParameterConstraints constraints;

        try {
            constraints = getParameterConstraints(annotations);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return generateInstance(parameter.getClass(), constraints);
    }

    private ParameterConstraints getParameterConstraints(Annotation[] annotations)
        throws InvocationTargetException, IllegalAccessException {
        boolean notNull = false;
        long min = Long.MIN_VALUE;
        long max = Long.MAX_VALUE;

        for (var annotation : annotations) {
            if (annotation.annotationType() == NotNull.class) {
                notNull = true;
            } else if (annotation.annotationType() == Min.class) {
                min = (long) ANNOTATION_MIN_VALUE.invoke(annotation);
            } else if (annotation.annotationType() == Max.class) {
                max = (long) ANNOTATION_MAX_VALUE.invoke(annotation);
            }
        }

        return new ParameterConstraints(notNull, min, max);
    }

    private Object generateInstance(Class<?> objectClass, ParameterConstraints constraints) {
        if (!constraints.notNull()
            && !objectClass.isPrimitive()
            && RANDOM.nextBoolean()) {
            return null;
        }

        if (objectClass == char.class || objectClass == Character.class) {
            return (char) RANDOM.nextLong(
                (long) max(0, constraints.min()),
                (long) min(Math.pow(2, 16), constraints.max())
            );
        } else if (objectClass == boolean.class || objectClass == Boolean.class) {
            return RANDOM.nextBoolean();
        } else if (Number.class.isAssignableFrom(objectClass)) {
            return generateNumberWithConstraints(constraints, objectClass);
        }
        return ROG.nextObject(objectClass);
    }

    private Object generateNumberWithConstraints(ParameterConstraints constraints, Class<?> numberClass) {
        double minValue = Double.MIN_VALUE;
        double maxValue = Double.MAX_VALUE;

        if (numberClass == byte.class || numberClass == Byte.class) {
            minValue = max(Byte.MIN_VALUE, constraints.min());
            maxValue = min(Byte.MAX_VALUE, constraints.max());
        } else if (numberClass == short.class || numberClass == Short.class) {
            minValue = max(Short.MIN_VALUE, constraints.min());
            maxValue = min(Short.MAX_VALUE, constraints.max());
        } else if (numberClass == int.class || numberClass == Integer.class) {
            minValue = max(Short.MIN_VALUE, constraints.min());
            maxValue = min(Short.MAX_VALUE, constraints.max());
        } else if (numberClass == long.class || numberClass == Long.class) {
            minValue = max(Long.MIN_VALUE, constraints.min());
            maxValue = min(Long.MAX_VALUE, constraints.max());
        } else if (numberClass == float.class || numberClass == Float.class) {
            minValue = max(Float.MIN_VALUE, constraints.min());
            maxValue = min(Float.MAX_VALUE, constraints.max());
        } else {
            minValue = max(minValue, constraints.min());
            maxValue = max(maxValue, constraints.max());
        }

        return RANDOM.nextDouble(minValue, maxValue);
    }
}
