package mp

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Shows that tests for JavaScript-specific functionality will run at the same time as tests for common functionality.
 */
class ClassWithMultiPlatformFunctionalityTest {
    @Test
    fun stringRepresentationIsCorrect() {
        val instance = ClassWithMultiPlatformFunctionality();

        val stringRepresentation = instance.toString()

        assertEquals("A JavaScript class implementing common and providing JavaScript-specific functionality", stringRepresentation)
    }
}
