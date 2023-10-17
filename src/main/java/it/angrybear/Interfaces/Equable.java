package it.angrybear.Interfaces;

import it.fulminazzo.reflectionutils.Objects.ReflObject;

import java.util.Objects;

public interface Equable {
    default boolean equals(Equable tWorld) {
        ReflObject<Equable> thisReflObject = new ReflObject<>(this);
        thisReflObject.setShowErrors(false);
        ReflObject<Equable> tWorldReflObject = new ReflObject<>(tWorld);
        tWorldReflObject.setShowErrors(false);
        return thisReflObject.getFields().stream()
                .map(f -> "get" + f.getName())
                .allMatch(n -> Objects.equals(thisReflObject.getMethodObject(n), tWorldReflObject.getMethodObject(n)));
    }
}
