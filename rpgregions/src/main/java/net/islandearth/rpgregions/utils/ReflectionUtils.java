package net.islandearth.rpgregions.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReflectionUtils {

    /**
     * Gets all fields from all super classes (up to Object.class) with the specified annotation.
     * @param target origin class
     * @param annotation annotation to find
     * @return {@link CompletableFuture} with a list of the annotated fields
     */
    public static CompletableFuture<List<Field>> getSuperFieldsFromAnnotationAsync(Class<?> target, Class<? extends Annotation> annotation) {
        return CompletableFuture.supplyAsync(() -> {
            List<Field> fields = new ArrayList<>();
            Class<?> current = target;
            while (!current.equals(Object.class)) {
                for (Field declaredField : current.getDeclaredFields()) {
                    if (declaredField.isAnnotationPresent(annotation)) {
                        fields.add(declaredField);
                    }
                }
                current = current.getSuperclass();
            }
            return fields;
        });
    }
}
