package feup.sdis.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utilities related to files
 */
public class FileUtils {

    /**
     * Copy a stream to another location
     *
     * @param is input stream
     * @param os output stream
     * @return true if successful, false otherwise
     */
    public static boolean copyStream(final InputStream is, final OutputStream os) {
        try {
            final byte[] buf = new byte[1024];

            int len;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            is.close();
            os.close();
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
