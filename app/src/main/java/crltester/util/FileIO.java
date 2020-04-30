package crltester.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Benjamin Sanno
 * @author Marc Hoersken
 * @version $Revision: 1.2 2014-10-18$
 */
public class FileIO {
	/**
	 * Check if a file is accessible and can be read.
	 * 
	 * @param path	Path to a file.
	 * @return	Is file accessible and can be read.
	 */
	public static boolean testFileAccess(String path) {
		File file = new File(path);
		if (!file.exists()) {
			System.out.println("The file '" + path + "' doesn't exist.");
			return false;
		}
		if (!file.isFile()) {
			System.out.println("The file '" + path + "' isn't a file.");
			return false;
		}
		if (!file.canRead()) {
			System.out.println("The file '" + path + "' cannot be read.");
			return false;
		}
		System.out.println("The file '" + file.getAbsolutePath() + "' exists!");
		return true;
	}

	/**
	 * Check if a list of files are accessible and can be read.
	 * 
	 * @param paths	List of paths to a file.
	 * @return	Are files accessible and can be read.
	 */
	public static List<Boolean> testFileAccess(List<String> paths) {
		List<Boolean> results = new ArrayList<Boolean>(paths.size());
		for (String path : paths) {
			results.add(testFileAccess(path));
		}
		return results;
	}

	/**
	 * Open a file that is accessible and can be read.
	 * 
	 * @param filePath	Path to a file.
	 * @return	Input stream of the file.
	 * @throws IOException
	 */
	public static FileInputStream getFileInputStream(String filePath) throws IOException {
		FileInputStream inputStream = null;
		if (testFileAccess(filePath)) {
			try {
				inputStream = new FileInputStream(filePath);
				System.out.println("Successfully created input stream for file '" + filePath + "'.");
			} catch (FileNotFoundException e) {
				System.out.println("File is accessible, but couldn't create input stream from file '" + filePath + "'.");
				System.out.println(e.toString());
				throw new IOException("File is accessible, but couldn't create input stream from file '" + filePath + "'.", e);
			}
		} else {
			System.out.println("Couldn't create input stream for file '" + filePath + "', because it isn't accessible.");
			throw new IOException("Couldn't create input stream for file '" + filePath + "', because it isn't accessible.\n");
		}
		return inputStream;
	}

	/**
	 * Open a list of files that are accessible and can be read.
	 * 
	 * @param filePaths	List of paths to a file.
	 * @return	Input stream of the files.
	 * @throws IOException
	 */
	public static List<FileInputStream> getFileInputStreams(List<String> filePaths) throws IOException {
		List<FileInputStream> inputStreams = new ArrayList<FileInputStream>(filePaths.size());
		for (String filePath : filePaths) {
			inputStreams.add(getFileInputStream(filePath));
		}
		return inputStreams;
	}
}
