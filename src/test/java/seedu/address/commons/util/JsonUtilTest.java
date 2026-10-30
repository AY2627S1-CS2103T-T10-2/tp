package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.SerializableTestClass;
import seedu.address.testutil.TestUtil;

/**
 * Tests JSON Read and Write
 */
public class JsonUtilTest {

    private static final Path SERIALIZATION_FILE = TestUtil.getFilePathInSandboxFolder("serialize.json");

    @Test
    public void serializeObjectToJsonFile_noExceptionThrown() throws IOException {
        SerializableTestClass serializableTestClass = new SerializableTestClass();
        serializableTestClass.setTestValues();

        JsonUtil.serializeObjectToJsonFile(SERIALIZATION_FILE, serializableTestClass);

        assertEquals(FileUtil.readFromFile(SERIALIZATION_FILE), SerializableTestClass.JSON_STRING_REPRESENTATION);
    }

    @Test
    public void deserializeObjectFromJsonFile_noExceptionThrown() throws IOException {
        FileUtil.writeToFile(SERIALIZATION_FILE, SerializableTestClass.JSON_STRING_REPRESENTATION);

        SerializableTestClass serializableTestClass = JsonUtil
                .deserializeObjectFromJsonFile(SERIALIZATION_FILE, SerializableTestClass.class);

        assertEquals(serializableTestClass.getName(), SerializableTestClass.getNameTestValue());
        assertEquals(serializableTestClass.getListOfLocalDateTimes(), SerializableTestClass.getListTestValues());
        assertEquals(serializableTestClass.getMapOfIntegerToString(), SerializableTestClass.getHashMapTestValues());
    }

    @Test
    public void fromJsonString_consecutiveObjects_throwsIoException() {
        String json = SerializableTestClass.JSON_STRING_REPRESENTATION;
        assertThrows(IOException.class, () -> JsonUtil.fromJsonString(json + json, SerializableTestClass.class));
        assertThrows(IOException.class, () -> JsonUtil.fromJsonString(
                json + " \t\r\n" + json, SerializableTestClass.class));
    }

    @Test
    public void fromJsonString_trailingJsonValue_throwsIoException() {
        String json = SerializableTestClass.JSON_STRING_REPRESENTATION;
        for (String trailingValue : new String[]{"[]", "\"another value\"", "42", "true", "false", "null"}) {
            assertThrows(IOException.class, () -> JsonUtil.fromJsonString(
                    json + " " + trailingValue, SerializableTestClass.class));
        }
    }

    @Test
    public void fromJsonString_invalidTrailingContent_throwsIoException() {
        String json = SerializableTestClass.JSON_STRING_REPRESENTATION;
        for (String trailingContent : new String[]{"garbage", "}", ",", "/* comment */"}) {
            assertThrows(IOException.class, () -> JsonUtil.fromJsonString(
                    json + " " + trailingContent, SerializableTestClass.class));
        }
    }

    @Test
    public void fromJsonString_trailingWhitespace_success() throws IOException {
        SerializableTestClass actual = JsonUtil.fromJsonString(
                SerializableTestClass.JSON_STRING_REPRESENTATION + " \t\r\n", SerializableTestClass.class);

        assertEquals(SerializableTestClass.JSON_STRING_REPRESENTATION, JsonUtil.toJsonString(actual));
    }

    @Test
    public void fromJsonString_unknownField_ignored() throws IOException {
        String json = "{\"name\":\"Test\",\"unknown\":{\"nested\":[1,2]}}";

        SerializableTestClass actual = JsonUtil.fromJsonString(json, SerializableTestClass.class);

        assertEquals("Test", actual.getName());
    }
}
