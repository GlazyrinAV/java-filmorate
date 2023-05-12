package ru.yandex.practicum.filmorate.customConstraints.ratingIdConstraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {RatingIdConstraintValidator.class})
public @interface RatingIdConstraint {
    String message() default "Указан несуществующий рейтинг.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
