package seedu.address.model;

import java.util.ArrayList;
import java.util.List;

/**
 * An address book that saves past states to support undo and redo.
 */
public class VersionedAddressBook extends AddressBook {
    private final List<AddressBook> addressBookStateList;
    private int currentStatePointer;

    /**
     * Creates an address book with the given data and saves its initial state.
     */
    public VersionedAddressBook(ReadOnlyAddressBook initialState) {
        super(initialState);

        addressBookStateList = new ArrayList<>();
        addressBookStateList.add(new AddressBook(this));
        currentStatePointer = 0;
    }

    /**
     * Saves changed data as a new state and clears any redo history.
     * Does nothing if the data matches the current saved state.
     */
    public void commit() {
        if (addressBookStateList.get(currentStatePointer).equals(this)) {
            return;
        }

        AddressBook snapshot = new AddressBook(this);

        addressBookStateList.subList(
                currentStatePointer + 1, addressBookStateList.size()).clear();

        addressBookStateList.add(snapshot);
        currentStatePointer++;
    }

    /**
     * Restores the previous saved state.
     *
     * @throws IllegalStateException if undo is unavailable.
     */
    public void undo() {
        if (!canUndo()) {
            throw new IllegalStateException("No states to undo.");
        }

        int previousPointer = currentStatePointer - 1;
        resetData(addressBookStateList.get(previousPointer));
        currentStatePointer = previousPointer;
    }

    /**
     * Restores the next saved state.
     *
     * @throws IllegalStateException if redo is unavailable.
     */
    public void redo() {
        if (!canRedo()) {
            throw new IllegalStateException("No states to redo.");
        }

        int nextPointer = currentStatePointer + 1;
        resetData(addressBookStateList.get(nextPointer));
        currentStatePointer = nextPointer;
    }

    /**
     * Returns true if there is a previously saved state to restore.
     */
    public boolean canUndo() {
        return currentStatePointer > 0;
    }

    /**
     * Returns true if there is a next saved state to restore.
     */
    public boolean canRedo() {
        return currentStatePointer < (addressBookStateList.size() - 1);
    }
}
