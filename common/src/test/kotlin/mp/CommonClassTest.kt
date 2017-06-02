package mp

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Shows that tests for common functionality will be run in the multi-platform modules.
 */
class CommonClassTest {
    @Test
    fun executeInvokesDoItOfGivenArgument() {
        val instance = CommonClass();
        
        var hasBeenInvoked = false
        instance.execute(object : CommonInterface {
            override fun doIt() {
                hasBeenInvoked = true
            }
        })
        
        assertTrue(hasBeenInvoked)
    }
    
    @Test
    fun stringRepresentationIsCorrect() {
        val instance = CommonClass();

        val stringRepresentation = instance.toString()
        
        assertEquals("An instance of the common class", stringRepresentation)
    }
}
