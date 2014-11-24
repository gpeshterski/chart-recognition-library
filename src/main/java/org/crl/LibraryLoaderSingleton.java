package org.crl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
/**
 * Loads the libraries required for OCR recognition 
 * Leptonica and Tesseract
 * <br>
 * Requires a 32 bit java version
 * Windows or Linux OS
 * 
 * */
public class LibraryLoaderSingleton {
	private static LibraryLoaderSingleton instance;

	private static void loadJarDll(String name) throws IOException {
		InputStream in = ClassLoader.getSystemClassLoader()
				.getResourceAsStream(name);
		byte[] buffer = new byte[1024];
		int read = -1;
		FileOutputStream fos = null;
		try {
			File temp = File.createTempFile(name, "");
			temp = new File(temp.getParent() + File.separator + name);
			fos = new FileOutputStream(temp);
			while ((read = in.read(buffer)) != -1) {
				fos.write(buffer, 0, read);
			}
			fos.flush();
			fos.close();
			in.close();
			System.load(temp.getAbsolutePath());
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load.\n" + e);
			System.exit(1);
		} catch (Exception loadFailed) {
			throw new IllegalStateException("Error loading library "+name ,loadFailed);
		} finally {
			fos.close();
			in.close();
		}
	}
/**
 * Returns the process id of the process that loads a library
 * */
	private static void printPid() {
		try {
			java.lang.management.RuntimeMXBean runtime = java.lang.management.ManagementFactory
					.getRuntimeMXBean();
			java.lang.reflect.Field jvm;
			jvm = runtime.getClass().getDeclaredField("jvm");

			jvm.setAccessible(true);
			sun.management.VMManagement mgmt = (sun.management.VMManagement) jvm
					.get(runtime);
			java.lang.reflect.Method pid_method = mgmt.getClass()
					.getDeclaredMethod("getProcessId");
			pid_method.setAccessible(true);

			int pid = (Integer) pid_method.invoke(mgmt);
			System.out.println("Process ID:"+pid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
/**
 * Validates whether the library dll was loaded into memory
 */
	private static boolean isDllLoaded(String name) {
		Field LIBRARIES;
		try {
			LIBRARIES = ClassLoader.class
					.getDeclaredField("loadedLibraryNames");
			LIBRARIES.setAccessible(true);
			final java.util.Vector<String> libraries = (java.util.Vector<String>) LIBRARIES
					.get(ClassLoader.getSystemClassLoader());
			System.out.println(libraries.toString());
			for (String lib : libraries) {
				if (lib.contains(name)) {
					//System.out.println("Library "+name+" is loaded.");
					return true;
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException(
					"Unable to get loaded DLL libraries.", e);
		}
		return false;
	}
/**
 * For support of Windows and Linux platform 
 * file extensions
 * */
	private static String getFileExtension() {
		String fileExtension = null;
		if (System.getProperty("os.name").contains("Windows")) {
			fileExtension = ".dll";
		} else {
			fileExtension = ".so";
		}
		if (System.getProperty("os.name").contains("Mac")) {
			throw new IllegalStateException(
					"Your operating system is not supported");
		}
		return fileExtension;
	}
/**
 * Loads the libraries required for Tesseract
 * */
	private static boolean loadTesseract() {
		String fileExtension = getFileExtension();
		try {
			if (Integer.parseInt(System.getProperty("sun.arch.data.model")) != 32) {
				throw new IllegalStateException(
						"A 32 bit version of Java is required to run the Tesseract Library.");
			}
			loadJarDll("liblept168" + fileExtension);
			loadJarDll("libtesseract302" + fileExtension);
			return true;

		} catch (IOException e) {
			throw new IllegalStateException("Unable to load Tesseract library.");
		}
	}

	public static LibraryLoaderSingleton getInstance() {
		if (instance != null) {
			return instance;
		}
		instance = new LibraryLoaderSingleton();
		loadTesseract();
		return instance;
	}
}
