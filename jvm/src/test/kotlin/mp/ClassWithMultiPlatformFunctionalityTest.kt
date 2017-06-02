package mp

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Shows that tests for Java-specific functionality will run at the same time as tests for common functionality.
 */
class ClassWithMultiPlatformFunctionalityTest {
    @Test
    fun stringRepresentationIsCorrect() {
        val instance = ClassWithMultiPlatformFunctionality();

        val stringRepresentation = instance.toString()

        assertEquals("A Java class implementing common and providing Java-specific functionality", stringRepresentation)
    }
}
