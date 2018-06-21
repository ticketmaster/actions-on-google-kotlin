package actions.expected;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;


public class ServletUtils {
    static Logger logger = Logger.getAnonymousLogger();


    //Copies inputstream so it can be logged and reused.  Super inefficient and should not be production
    public static String getBody(InputStream inputStream, String charsetName) throws IOException {
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            if (inputStream != null) {
                if (charsetName == null) {
                    charsetName = "UTF-8";
                }
                bufferedReader = new BufferedReader(new BufferedReader(new InputStreamReader(inputStream, charsetName)));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
                logger.warning("bytes read: " + bytesRead);
            } else {
                logger.warning("input stream is null");
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    //creates 2 copies of the InputStream.
    public static ByteArrayInputStream[] copyBuffer(InputStream input) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
        } catch (IOException e) {
            logger.warning("Exception copying inputstream: " + e.getMessage());
        }

        return new ByteArrayInputStream[]{new ByteArrayInputStream(baos.toByteArray()),
                new ByteArrayInputStream(baos.toByteArray())};
    }

    public static byte[] toByteArray(InputStream is) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        try {
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
        } catch (IOException e) {
            return null;
        }


        return buffer.toByteArray();
    }


}
