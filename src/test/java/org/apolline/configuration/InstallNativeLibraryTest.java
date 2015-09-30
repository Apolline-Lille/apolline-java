package org.apolline.configuration;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({InstallNativeLibrary.class,Class.class})
public class InstallNativeLibraryTest {

    private InstallNativeLibrary install;

    @Before
    public void setUp(){
        install=new InstallNativeLibrary();
    }

    @Test
    public void testIsWindows(){
        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("windows");

        assertTrue(install.isWindows());
    }

    @Test
    public void testIsWindowsNotWork(){
        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("linux");

        assertFalse(install.isWindows());
    }

    @Test
    public void testIsMac(){
        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("mac");

        assertTrue(install.isMac());
    }

    @Test
    public void testIsMacNotWork(){
        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("linux");

        assertFalse(install.isMac());
    }

    @Test
    public void testIsSolaris(){
        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("sunos");

        assertTrue(install.isSolaris());
    }

    @Test
    public void testIsSolarisNotWork(){
        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("linux");

        assertFalse(install.isSolaris());
    }

    @Test
    public void testIsLinux(){
        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("linux");

        assertTrue(install.isUnix());
    }

    @Test
    public void testIsLinux2(){
        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("unix");

        assertTrue(install.isUnix());
    }

    @Test
    public void testIsLinux3(){
        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("aix");

        assertTrue(install.isUnix());
    }

    @Test
    public void testIsUnixNotWork(){
        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("windows");

        assertFalse(install.isUnix());
    }

    @Test
    public void testIsInstallOnLinux() throws Exception {
        File f=mock(File.class);

        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("linux");
        whenNew(File.class).withArguments("/fakeHome/.datalogger/librxtxSerial.so").thenReturn(f);
        when(f.exists()).thenReturn(true);
        System.setProperty("user.home", "/fakeHome");

        assertTrue(install.isInstall());

        verifyNew(File.class).withArguments("/fakeHome/.datalogger/librxtxSerial.so");
        verify(f).exists();
    }

    @Test
    public void testIsInstallNotWorkOnLinux() throws Exception {
        File f=mock(File.class);

        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("linux");
        whenNew(File.class).withArguments("/fakeHome/.datalogger/librxtxSerial.so").thenReturn(f);
        when(f.exists()).thenReturn(false);
        System.setProperty("user.home", "/fakeHome");

        assertFalse(install.isInstall());

        verifyNew(File.class).withArguments("/fakeHome/.datalogger/librxtxSerial.so");
        verify(f).exists();
    }

    @Test
    public void testIsInstallNotWork(){
        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("anOs");

        assertFalse(install.isInstall());
    }

    @Test
    public void testInstallNotWork() throws Exception {
        File f=mock(File.class);

        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("linux");
        whenNew(File.class).withArguments("/fakeHome/.datalogger/librxtxSerial.so").thenReturn(f);
        when(f.exists()).thenReturn(true);
        System.setProperty("user.home", "/fakeHome");

        install.install();

        verifyNew(File.class).withArguments(anyString());
    }

    @Test
    public void testInstallWithIsNotAvailable() throws Exception {
        File f=mock(File.class);
        File f2=mock(File.class);
        InputStream is=mock(InputStream.class);
        FileOutputStream out=mock(FileOutputStream.class);
        InstallNativeLibrary install=mock(InstallNativeLibrary.class);

        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("linux");
        whenNew(File.class).withArguments("/fakeHome/.datalogger/librxtxSerial.so").thenReturn(f);
        whenNew(File.class).withArguments("/fakeHome/.datalogger").thenReturn(f2);
        whenNew(FileOutputStream.class).withArguments(f).thenReturn(out);
        when(install.isInstall()).thenReturn(false);
        when(install.getLib()).thenReturn(is);
        when(f2.mkdir()).thenReturn(true);
        when(is.available()).thenReturn(0);
        doNothing().when(is).close();
        doNothing().when(out).close();
        when(f.setExecutable(true)).thenReturn(true);
        doCallRealMethod().when(install).install();
        System.setProperty("user.home", "/fakeHome");

        install.install();

        verifyNew(File.class).withArguments("/fakeHome/.datalogger/librxtxSerial.so");
        verifyNew(File.class).withArguments("/fakeHome/.datalogger");
        verifyNew(FileOutputStream.class).withArguments(f);
        verify(install).getLib();
        verify(f2).mkdir();
        verify(is).available();
        verify(is).close();
        verify(out).close();
        verify(f).setExecutable(true);
        verify(install).isInstall();
    }

    @Test
    public void testInstall() throws Exception {
        File f=mock(File.class);
        File f2=mock(File.class);
        InputStream is=mock(InputStream.class);
        FileOutputStream out=mock(FileOutputStream.class);
        InstallNativeLibrary install=mock(InstallNativeLibrary.class);

        mockStatic(InstallNativeLibrary.class);
        when(InstallNativeLibrary.OS()).thenReturn("linux");
        whenNew(File.class).withArguments("/fakeHome/.datalogger/librxtxSerial.so").thenReturn(f);
        whenNew(File.class).withArguments("/fakeHome/.datalogger").thenReturn(f2);
        whenNew(FileOutputStream.class).withArguments(f).thenReturn(out);
        when(install.isInstall()).thenReturn(false);
        when(install.getLib()).thenReturn(is);
        when(f2.mkdir()).thenReturn(true);
        when(is.available()).thenReturn(1,0);
        when(is.read()).thenReturn(10);
        doNothing().when(out).write(10);
        doNothing().when(is).close();
        doNothing().when(out).close();
        when(f.setExecutable(true)).thenReturn(true);
        doCallRealMethod().when(install).install();
        System.setProperty("user.home", "/fakeHome");

        install.install();

        verifyNew(File.class).withArguments("/fakeHome/.datalogger/librxtxSerial.so");
        verifyNew(File.class).withArguments("/fakeHome/.datalogger");
        verifyNew(FileOutputStream.class).withArguments(f);
        verify(install).getLib();
        verify(f2).mkdir();
        verify(is,times(2)).available();
        verify(is).close();
        verify(out).close();
        verify(f).setExecutable(true);
        verify(install).isInstall();
        verify(is).read();
        verify(out).write(10);
    }

    @Test
    public void testGetLib(){
        InstallNativeLibrary install=mock(InstallNativeLibrary.class);
        InputStream is=mock(InputStream.class);

        when(install.isUnix()).thenReturn(true);
        when(install.getLibLinux()).thenReturn(is);
        when(install.getLib()).thenCallRealMethod();

        assertEquals(is,install.getLib());

        verify(install).getLibLinux();
        verify(install).isUnix();
    }

    @Test
    public void testGetLibNotWork(){
        InstallNativeLibrary install=mock(InstallNativeLibrary.class);

        when(install.isUnix()).thenReturn(false);
        when(install.getLib()).thenCallRealMethod();

        assertNull(install.getLib());

        verify(install).isUnix();
    }
}
