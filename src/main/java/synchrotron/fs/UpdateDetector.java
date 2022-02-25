package synchrotron.fs;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class UpdateDetector {

	public static final int BUFFER_SIZE = 1024;

	private Map<File, String> map;
	public final MessageDigest messageDigest;

	public UpdateDetector(MessageDigest messageDigest) {
		this.map = new HashMap<>();
		this.messageDigest = messageDigest;
	}

	public String getHash(File file) {
		try {
			InputStream inputStream = new FileInputStream(file);
			DigestInputStream digestInputStream = new DigestInputStream(inputStream, this.messageDigest);

			int reads;
			byte[] buffer = new byte[UpdateDetector.BUFFER_SIZE];
			do {
				reads = digestInputStream.read(buffer);
				if (reads > 0) {
					this.messageDigest.update(buffer);
				}
			} while (reads != -1);

			digestInputStream.close();
			inputStream.close();

			return HexBin.encode(this.messageDigest.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "N/A";
	}
}
