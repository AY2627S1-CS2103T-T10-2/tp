package seedu.address.model.classcode;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Objects;

/**
 * Represents a class that a student is enrolled in, identified by its module and class group, e.g. module CS2103T and
 * group T10 give the class code "CS2103T-T10". Guarantees: immutable; is valid as declared in
 * {@link #isValidClassCode(String)}
 */
public class ClassCode {

    public static final String SEPARATOR = "-";

    public static final String MESSAGE_CONSTRAINTS = "Class codes should be in the format MODULE" + SEPARATOR
            + "GROUP, e.g. CS2103T-T10.\n" + ModuleCode.MESSAGE_CONSTRAINTS + "\n" + ClassGroup.MESSAGE_CONSTRAINTS;

    private final ModuleCode moduleCode;
    private final ClassGroup classGroup;

    /**
     * Constructs a {@code ClassCode} from its two parts.
     */
    public ClassCode(ModuleCode moduleCode, ClassGroup classGroup) {
        requireNonNull(moduleCode);
        requireNonNull(classGroup);
        this.moduleCode = moduleCode;
        this.classGroup = classGroup;
    }

    /**
     * Constructs a {@code ClassCode} from a string in the format MODULE-GROUP.
     *
     * @param classCode A valid class code.
     */
    public ClassCode(String classCode) {
        requireNonNull(classCode);
        checkArgument(isValidClassCode(classCode), MESSAGE_CONSTRAINTS);
        String[] parts = classCode.split(SEPARATOR, -1);
        this.moduleCode = new ModuleCode(parts[0]);
        this.classGroup = new ClassGroup(parts[1]);
    }

    /**
     * Returns true if a given string is a valid class code.
     */
    public static boolean isValidClassCode(String test) {
        requireNonNull(test);
        String[] parts = test.split(SEPARATOR, -1);
        return parts.length == 2
                && ModuleCode.isValidModuleCode(parts[0])
                && ClassGroup.isValidClassGroup(parts[1]);
    }

    public ModuleCode getModuleCode() {
        return moduleCode;
    }

    public ClassGroup getClassGroup() {
        return classGroup;
    }

    @Override
    public String toString() {
        return moduleCode + SEPARATOR + classGroup;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ClassCode)) {
            return false;
        }
        ClassCode otherClassCode = (ClassCode) other;
        return moduleCode.equals(otherClassCode.moduleCode) && classGroup.equals(otherClassCode.classGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleCode, classGroup);
    }
}
