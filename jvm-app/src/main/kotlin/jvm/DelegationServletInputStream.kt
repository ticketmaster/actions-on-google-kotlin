package jvm

import java.io.InputStream
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream


class DelegatingServletInputStream(val inputStream: InputStream) : ServletInputStream() {

    override fun isFinished(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setReadListener(readListener: ReadListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isReady(): Boolean = true

    override fun read(): Int {
        return inputStream.read()
    }

    override fun read(p0: ByteArray?): Int {
        return inputStream.read(p0)
    }

    override fun read(p0: ByteArray?, p1: Int, p2: Int): Int {
        return inputStream.read(p0, p1, p2)
    }

    override fun skip(p0: Long): Long {
        return inputStream.skip(p0)
    }

    override fun readLine(b: ByteArray?, off: Int, len: Int): Int {
        TODO("NOT IMPLEMENTED")
    }

    override fun available(): Int {
        return inputStream.available()
    }

    override fun reset() {
        inputStream.reset()
    }

    override fun mark(p0: Int) {
        inputStream.mark(p0)
    }

    override fun markSupported(): Boolean {
        return inputStream.markSupported()
    }

    override fun close() {
        super.close()
        inputStream.close()
    }
}
