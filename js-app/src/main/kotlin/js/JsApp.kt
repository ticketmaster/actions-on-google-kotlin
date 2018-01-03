package js

import mp.ClassWithMultiPlatformFunctionality
import mp.CommonClass
import mp.CommonClassDelegatingToInternalClassHeader
import mp.commonFunction
import mp.CommonInterface
import mp.CommonObject
import mp.MpCloseable
import mp.mpUse
import mp.multiPlatformFunction
import mp.printThis
import mp.SubClassHeaderDelegatedTo

fun main(arguments: Array<String>) {
    header("Common function")
    println(commonFunction())

    header("Multi-platform function")
    println(multiPlatformFunction())

    val canBeClosed = CanBeClosed()

    header("Common extension function on expected interface")
    canBeClosed.printThis()

    header("Common generic extension function on expected interface")
    canBeClosed.mpUse {
        println("Using it through 'mpUse': $it")
    }

    // Instantiation of a common class
    val commonClass = CommonClass()

    header("Common extension function on an instance of a common class")
    commonClass.printThis()

    header("Common generic extension function on an instance of a common class")
    commonClass.mpUse { cc ->
        // Instantiation of a common interface, and passing it to a function
        cc.execute(object : CommonInterface {
            override fun doIt() {
                println("Doing it in JavaScript through a common class instance")
                // Common extension function on an instance of a common interface
                printThis()
            }

            override fun toString() = "Common interface instance passed to an instance of the common class"
        })
    }

    header("Use of a common object, and common extension function on a common object")
    CommonObject.printThis()

    header("Common generic extension function on a common object")
    CommonObject.mpUse { co ->
        co.execute(object : CommonInterface {
            override fun doIt() {
                println("Doing it in JavaScript through the common object")
                // Common generic extension function on an instance of a common interface
                mpUse { ci ->
                    println("Using common inteface instance: $ci")
                }
            }

            override fun toString() = "Common interface instance passed to the common object"
        })
    }

    val classWithMultiPlatformFunctionality = ClassWithMultiPlatformFunctionality()

    header("Invocation of a function defined by the expected class")
    classWithMultiPlatformFunctionality.commonFunctionality()

    header("Invocation of a function defined by the JavaScript class implementation")
    classWithMultiPlatformFunctionality.javascriptFunctionality()

    header("Invocation of a function on an instance of a common class that delegates to a multi-platform internal class")
    CommonClassDelegatingToInternalClassHeader().execute()

    // Instantiation of a multi-platform sub class, that has a base class containing common code
    val subClassDelegatedTo = SubClassHeaderDelegatedTo()

    header("Invocation of a base class function that delegates to a multi-platform member function")
    subClassDelegatedTo.execute()
}

class CanBeClosed : MpCloseable {
    override fun close() {
        println("Closed: $this")
    }

    override fun toString() = "I can be closed"
}

private fun header(text: String) {
    println()
    println("##### $text #####")
}

