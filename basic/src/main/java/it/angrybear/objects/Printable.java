package it.angrybear.objects;

import it.fulminazzo.reflectionutils.objects.ReflObject;

import java.util.stream.Collectors;

/**
 * Represents an object that on toString() calls
 * prints itself and the fields it contains.
 */
public abstract class Printable {

    /**
     * Prints the object class and fields in a nice format.
     *
     * @param object    the object
     * @param headStart the start string
     * @return the string containing the information
     */
    private String printObject(Object object, String headStart) {
        if (object == null) return null;
        else {
            ReflObject<?> reflObject = new ReflObject<>(object);
            return String.format("%s%s {\n", headStart, object.getClass()) + reflObject.getFields().stream()
                    .map(f -> {
                        Object o = reflObject.getFieldObject(f.getName());
                        String str = o instanceof Printable ? printObject(o, headStart + "  ") : o == null ? "null" : o.toString();
                        return String.format("%s%s: %s", headStart + "  ", f.getName(), str);
                    })
                    .collect(Collectors.joining("\n")) + String.format("\n%s}\n", headStart);
        }
    }

    @Override
    public String toString() {
        return printObject(this, "");
    }
}
