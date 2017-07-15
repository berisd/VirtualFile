/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class StringUtilsTest {

    @Test
    public void testSplitEmpty() {
        Assert.assertArrayEquals(new String[]{}, StringUtils.split(null, ','));
        Assert.assertArrayEquals(new String[]{}, StringUtils.split("", ','));
    }

    @Test
    public void testSplitSingle() {
        Assert.assertArrayEquals(new String[]{"abc"}, StringUtils.split("abc", ','));
        Assert.assertArrayEquals(new String[]{"abc,"}, StringUtils.split("abc,", '|'));
    }

    @Test
    public void testSplitDouble() {
        Assert.assertArrayEquals(new String[]{"abc", "def"}, StringUtils.split("abc,def", ','));
        Assert.assertArrayEquals(new String[]{"", "def"}, StringUtils.split(",def", ','));
        Assert.assertArrayEquals(new String[]{"def", ""}, StringUtils.split("def,", ','));
    }

    @Test
    public void testSplitMulti() {
        Assert.assertArrayEquals(new String[]{"abc", "def", " asdfghi aabc"}, StringUtils.split("abc,def, asdfghi aabc", ','));
    }

    @Test
    public void testIsBlank() {
        Assert.assertTrue(StringUtils.isBlank(null));
        Assert.assertTrue(StringUtils.isBlank(""));
        Assert.assertTrue(StringUtils.isBlank(" "));
    }

    @Test
    public void testIsNotBlank() {
        Assert.assertFalse(StringUtils.isBlank(" abc"));
        Assert.assertFalse(StringUtils.isBlank("abc"));
        Assert.assertFalse(StringUtils.isBlank("abc "));
    }

    @Test
    public void testJoinEmpty() {
        Assert.assertEquals(StringUtils.EMPTY_STRING, StringUtils.join(Collections.emptyList(), ','));
        Assert.assertEquals(StringUtils.EMPTY_STRING, StringUtils.join(null, ','));
    }

    @Test
    public void testJoinSingle() {
        Assert.assertEquals("abc", StringUtils.join(Collections.singletonList("abc"), ','));
    }

    @Test
    public void testJoinMulti() {
        Assert.assertEquals("abc,def", StringUtils.join(Arrays.asList("abc", "def"), ','));
        Assert.assertEquals("This is a teststring", StringUtils.join(Arrays.asList("This", "is", "a", "teststring"), ' '));
    }

    @Test
    public void testJoinBounds() {
        Assert.assertEquals("abc,def,ghi", StringUtils.join(new String[]{"abc", "def", "ghi"}, ',', 0, 3));
        Assert.assertEquals("abc,def", StringUtils.join(new String[]{"abc", "def", "ghi"}, ',', 0, 2));
        Assert.assertEquals("abc|def", StringUtils.join(new String[]{"abc", "def", "ghi"}, '|', 0, 2));
        Assert.assertEquals("abc", StringUtils.join(new String[]{"abc", "def", "ghi"}, ',', 0, 1));
        Assert.assertEquals("def", StringUtils.join(new String[]{"abc", "def", "ghi"}, ',', 1, 2));
        Assert.assertEquals(StringUtils.EMPTY_STRING, StringUtils.join(new String[]{"abc", "def", "ghi"}, ',', 0, 0));
    }

    @Test
    public void testRepeatEmpty() {
        Assert.assertEquals(StringUtils.EMPTY_STRING, StringUtils.repeat(null, 1));
        Assert.assertEquals(StringUtils.EMPTY_STRING, StringUtils.repeat("", 1));
        Assert.assertEquals(StringUtils.EMPTY_STRING, StringUtils.repeat(null, 0));
        Assert.assertEquals(StringUtils.EMPTY_STRING, StringUtils.repeat("", 0));
    }

    @Test
    public void testRepeat() {
        Assert.assertEquals("abc", StringUtils.repeat("abc", 1));
        Assert.assertEquals("abcabc", StringUtils.repeat("abc", 2));
        Assert.assertEquals("aa", StringUtils.repeat('a', 2));
    }

    @Test
    public void testTrim() {
        Assert.assertEquals(StringUtils.EMPTY_STRING, StringUtils.trim(null));
        Assert.assertEquals(StringUtils.EMPTY_STRING, StringUtils.trim(""));
        Assert.assertEquals("abc", StringUtils.trim(" abc "));
    }

    @Test
    public void testRightPad() {
        Assert.assertEquals(StringUtils.EMPTY_STRING, StringUtils.rightPad(null, 0, 'a'));
        Assert.assertEquals("aaa", StringUtils.rightPad("", 3, 'a'));
        Assert.assertEquals("abc", StringUtils.rightPad("abc", 3, 'z'));
        Assert.assertEquals("abczz", StringUtils.rightPad("abc", 5, 'z'));
        Assert.assertEquals("abc", StringUtils.rightPad("abc", 1, 'z'));
        Assert.assertEquals("abc", StringUtils.rightPad("abc", -1, 'z'));
    }

    @Test
    public void leftRightPad() {
        Assert.assertEquals(StringUtils.EMPTY_STRING, StringUtils.leftPad(null, 0, 'a'));
        Assert.assertEquals("aaa", StringUtils.leftPad("", 3, 'a'));
        Assert.assertEquals("abc", StringUtils.leftPad("abc", 3, ' '));
        Assert.assertEquals("  abc", StringUtils.leftPad("abc", 5, ' '));
        Assert.assertEquals("abc", StringUtils.leftPad("abc", 1, ' '));
        Assert.assertEquals("abc", StringUtils.leftPad("abc", -1, ' '));
    }

    @Test
    public void isEmpty() {
        Assert.assertTrue(StringUtils.isEmpty(null));
        Assert.assertTrue(StringUtils.isEmpty(""));
        Assert.assertFalse(StringUtils.isEmpty(" "));
        Assert.assertFalse(StringUtils.isEmpty("abc"));
        Assert.assertFalse(StringUtils.isEmpty(" abc "));
    }

    @Test
    public void getWordsFromCamelCaseString() {
        Assert.assertArrayEquals(new String[]{}, StringUtils.getWordsFromCamelCaseString(""));
        Assert.assertArrayEquals(new String[]{"Test"}, StringUtils.getWordsFromCamelCaseString("Test"));
        Assert.assertArrayEquals(new String[]{"test"}, StringUtils.getWordsFromCamelCaseString("test"));
        Assert.assertArrayEquals(new String[]{"Test", "Run"}, StringUtils.getWordsFromCamelCaseString("TestRun"));
        Assert.assertArrayEquals(new String[]{"test", "Run"}, StringUtils.getWordsFromCamelCaseString("testRun"));
        Assert.assertArrayEquals(new String[]{"A", "Test", "Run"}, StringUtils.getWordsFromCamelCaseString("ATestRun"));
    }
}