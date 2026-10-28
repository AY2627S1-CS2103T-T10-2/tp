package seedu.address.model.gradebook;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

import seedu.address.model.classcode.ModuleCode;

/**
 * Identifies an assignment within a module, such as CS2103T:A1.
 */
public final class AssignmentId {
    public static final String MESSAGE_CONSTRAINTS = "Assignment IDs should be MODULE:CODE, where CODE starts with "
            + "a letter and contains only letters and digits (e.g. CS2103T:A1).";

    private static final String SEPARATOR = ":";
    private static final String CODE_REGEX = "[A-Za-z][A-Za-z0-9]*";

    private final ModuleCode moduleCode;
    private final String code;

    /**
     * Creates an assignment ID from its module and assignment code.
     */
    public AssignmentId(ModuleCode moduleCode, String code) {
        this.moduleCode = requireNonNull(moduleCode);
        requireNonNull(code);
        checkArgument(code.matches(CODE_REGEX), MESSAGE_CONSTRAINTS);
        this.code = code.toUpperCase(Locale.ROOT);
    }

    /**
     * Creates an assignment ID from MODULE:CODE.
     */
    public AssignmentId(String value) {
        requireNonNull(value);
        checkArgument(isValidAssignmentId(value), MESSAGE_CONSTRAINTS);
        String[] parts = value.split(SEPARATOR, -1);
        moduleCode = new ModuleCode(parts[0]);
        code = parts[1].toUpperCase(Locale.ROOT);
    }

    /**
     * Returns whether the input is a valid assignment ID.
     */
    public static boolean isValidAssignmentId(String value) {
        requireNonNull(value);
        String[] parts = value.split(SEPARATOR, -1);
        return parts.length == 2 && ModuleCode.isValidModuleCode(parts[0]) && parts[1].matches(CODE_REGEX);
    }

    public ModuleCode getModuleCode() {
        return moduleCode;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return moduleCode + SEPARATOR + code;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AssignmentId otherId
                && moduleCode.equals(otherId.moduleCode) && code.equals(otherId.code);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(moduleCode, code);
    }
}
