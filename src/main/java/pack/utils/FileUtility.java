package pack.utils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public class FileUtility {
	public static String uploadFileImage(MultipartFile file, String folderName) {
		try {
			String folderUpload = System.getProperty("user.dir") + "/" + folderName;
			String fileName = System.currentTimeMillis() + file.getOriginalFilename();
			String strPath = String.format("%s/%s", folderUpload, fileName);
			byte[] data = file.getBytes();
			try (FileOutputStream fout = new FileOutputStream(strPath);
					BufferedOutputStream buf = new BufferedOutputStream(fout)) {
				buf.write(data);
				buf.flush();
				buf.close();
			}
			return fileName;
		} catch (Exception e) {
			e.getMessage();
		}
		return "";
	}

	public static String deleteFile(String folderName, String fileName) {
		try {
			Path pathFile = Path.of(System.getProperty("user.dir") + "/" + folderName + "/" + fileName);
			Files.delete(pathFile);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
}
