package org.apolline.configuration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DataloggerParser.class)
public class DataloggerParserTest{

    protected DataloggerParser parser;

    @Before
    public void setUp(){
        parser=new DataloggerParser();
    }

    @Test
    public void testCreateOption() throws Exception {
        Options opt=mock(Options.class);
        whenNew(Options.class).withNoArguments().thenReturn(opt);
        when(opt.addOption("p","properties",true,"Properties file")).thenReturn(opt);
        when(opt.addOption(eq("v"),eq("version"),eq(false),eq("version"))).thenReturn(opt);

        assertEquals(parser.createOptions(), opt);

        verifyNew(Options.class).withNoArguments();
        verify(opt).addOption("p", "properties", true, "Properties file");
        verify(opt).addOption(eq("v"),eq("version"),eq(false),eq("version"));
    }

    @Test
    public void testParseArguments() throws Exception {
        DefaultParser defaultParser=mock(DefaultParser.class);
        CommandLine cmd=mock(CommandLine.class);
        String args[]={"-f","file.properties"};
        Options opt=mock(Options.class);

        whenNew(DefaultParser.class).withNoArguments().thenReturn(defaultParser);
        whenNew(Options.class).withNoArguments().thenReturn(opt);
        when(opt.addOption("p", "properties", true, "Properties file")).thenReturn(opt);
        when(opt.addOption(eq("v"), eq("version"), eq(false), eq("version"))).thenReturn(opt);
        when(defaultParser.parse(eq(opt), eq(args))).thenReturn(cmd);

        DataloggerParser parser=new DataloggerParser();
        parser.parse(args);

        verifyNew(DefaultParser.class).withNoArguments();
        verifyNew(Options.class).withNoArguments();
        verify(opt).addOption("p", "properties", true, "Properties file");
        verify(opt).addOption(eq("v"), eq("version"), eq(false), eq("version"));
        verify(defaultParser).parse(eq(opt), eq(args));
    }

    @Test
    public void testGetValueReturnNullIfValueNotFound(){
        String args[]={"-p","file.properties"};
        parser.parse(args);

        assertNull(parser.getValueOf("f"));
    }

    @Test
    public void testGetValue(){
        String args[]={"-p","file.properties"};
        parser.parse(args);

        assertEquals("file.properties", parser.getValueOf("p"));
    }
}