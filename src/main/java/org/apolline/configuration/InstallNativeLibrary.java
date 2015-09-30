package org.apolline.configuration;

import java.io.*;
import java.lang.reflect.Field;

/**
 * This class is used for install automatically the java native library rxtx
 */
public class InstallNativeLibrary {

    /**
     * This parameter find the name of the OS
     */
    protected static String OS(){return  System.getProperty("os.name").toLowerCase();}

    /**
     * This method verify if the library is installed
     * @return Return true, if the library is installed, else false
     */
    public boolean isInstall(){
        //If is a linux
        if(isUnix()){
            //Get path to the user directory
            String home=System.getProperty("user.home");

            //verify if the file .datalogger/librxtxSerial.so exist in the user directory
            return (new File(home+"/.datalogger/librxtxSerial.so")).exists();
        }

        //Else return false
        return false;
    }

    /**
     * This method install the java native library rxtx to the user directory
     * @throws IOException
     */
    public void install() throws IOException {

        //If the library is not installed
        if(!isInstall()){

            //Get path to the user directory
            String home=System.getProperty("user.home");

            //Create the datalogger directory
            (new File(home+"/.datalogger")).mkdir();

            //Find the library into the jar
            InputStream is=getLib();

            //Create the file in the user directory
            File f = new File(home+"/.datalogger/librxtxSerial.so");
            FileOutputStream fos = new java.io.FileOutputStream(f);

            //Copy the file to the user directory
            while (is.available() > 0) {
                fos.write(is.read());
            }

            //Close files
            fos.close();
            is.close();

            //Set the file executable
            f.setExecutable(true);
        }
    }

    /**
     * This method get the library into the jar
     * @return An inputStream for read the rxtx library
     */
    protected InputStream getLib(){
        //If is a linux
        if(isUnix()){

            //get the rxtx linux library
            return getLibLinux();
        }

        //else return null
        return null;
    }

    /**
     * This method get rxtx linux library
     * @return An inputStream for read the rxtx library
     */
    protected InputStream getLibLinux(){
        //If is an architecture 64 bits
        if(System.getProperty("sun.arch.data.model").equals("64")) {

            //Find the library 64 bits
            return getClass().getResourceAsStream("/native_library/Linux/x86_64-unknown-linux-gnu/librxtxSerial.so");
        }

        //Else find the library 32 bits
        return getClass().getResourceAsStream("/native_library/Linux/i686-unknown-linux-gnu/librxtxSerial.so");
    }

    /**
     * This method load the library
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public void loadLibrary() throws IllegalAccessException, NoSuchFieldException {
        //if the libray is install
        if(isInstall()){

            //Get path to the user directory
            String home=System.getProperty("user.home");

            //The the path to the folder contains the rxtx library
            System.setProperty("java.library.path", home + "/.datalogger" + File.pathSeparator + System.getProperty("java.library.path"));
            System.setProperty("jna.library.path", home + "/.datalogger" + File.pathSeparator + System.getProperty("java.library.path"));
            System.setProperty("jni.library.path", home + "/.datalogger"+File.pathSeparator+System.getProperty("java.library.path"));

            //Disable path to sys_paths
            final Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);

            //Load the library
            System.loadLibrary("rxtxSerial");
        }
    }

    /**
     * This method verify is a Windows
     * @return Return true if is a Windows, else false
     */
    protected boolean isWindows() {
        return (OS().indexOf("win") >= 0);
    }

    /**
     * This method verify is a Mac
     * @return Return true if is a Mac, else false
     */
    protected boolean isMac() {
        return (OS().indexOf("mac") >= 0);
    }

    /**
     * This method verify is a Linux
     * @return Return true if is a Linux, else false
     */
    protected boolean isUnix() {
        return (OS().indexOf("nix") >= 0 || OS().indexOf("nux") >= 0 || OS().indexOf("aix") >= 0 );
    }

    /**
     * This method verify is a Solaris
     * @return Return true if is a Solaris, else false
     */
    protected boolean isSolaris() {
        return (OS().indexOf("sunos") >= 0);
    }
}