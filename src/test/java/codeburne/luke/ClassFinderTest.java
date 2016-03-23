package codeburne.luke;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class ClassFinderTest {
    private final static String clazzNames =
            "org.omg.DynamicAny.DynAnyFactoryHelper\n "
                    + "org.omg.DynamicAny.DynAnyHelper\n"
                    + "org.omg.DynamicAny.DynAnySeqHelper\n " +
                    "java.awt.event.ActionEvent\n" +
                    "java.awt.event.AdjustmentEvent\n" +
                    "javax.swing.event.AncestorEvent\n" +
                    "java.awt.event.ComponentEvent\r" +
                    "java.awt.event.ContainerEvent\n" +
                    "java.awt.event.FocusEvent\n" +
                    "java.awt.event.InputEvent\n" +
                    "java.awt.event.KeyEvent\n" +
                    "javax.imageio.stream.FileCacheImageInputStream\n" +
                    "javax.imageio.stream.FileImageInputStream\r" +
                    "a.b.FooBarBaz\n" +
                    "a.d.FooBarBaz\n" +
                    "c.d.FooBar\n" +
                    "FioBarBaz\n" +
                    "com.LooBarBaz\n" +
                    "org.omg.DynamicAny.DynArrayHelper";


    private final static String clazzNamesWithoutSameSimpleNAme =
            "org.omg.DynamicAny.DynAnyFactoryHelper\n "
                    + "org.omg.DynamicAny.DynAnyHelper\n"
                    + "org.omg.DynamicAny.DynAnySeqHelper\n " +
                    "java.awt.event.ActionEvent\n" +
                    "java.awt.event.AdjustmentEvent\n" +
                    "javax.swing.event.AncestorEvent\n" +
                    "java.awt.event.ComponentEvent\r" +
                    "java.awt.event.ContainerEvent\n" +
                    "java.awt.event.FocusEvent\n" +
                    "java.awt.event.InputEvent\n" +
                    "java.awt.event.KeyEvent\n" +
                    "javax.imageio.stream.FileCacheImageInputStream\n" +
                    "javax.imageio.stream.FileImageInputStream\r" +
                    "a.d.FooBarBaz\n" +
                    "c.d.FooBar\n" +
                    "FioBarBaz\n" +
                    "com.LooBarBaz\n" +
                    "org.omg.DynamicAny.DynArrayHelper";

    private ClassFinder classFinder;

    @Test(expected = NullPointerException.class)
    public void classFinderShouldThrowExceptionWhenInputStreamIsNull() {
        classFinder = new ClassFinder(null);
    }

    @Test
    public void classFinderShouldBeProperlyInitialised() {
        InputStream inputStream = new ByteArrayInputStream(new byte[1]);
        classFinder = new ClassFinder(inputStream);
        assertNotNull(classFinder);
    }

    @Test
    public void classFinderShouldContainThreeQualifiedNamesWithNoLastReturn() {
        String qualifiedNames = "a.b.C \n a.b.N \n a.NMS \r x.z.y.Name";
        InputStream inputStream = new ByteArrayInputStream(qualifiedNames.getBytes());
        classFinder = new ClassFinder(inputStream);
        assertThat(classFinder.findMatching(" "), not(hasItem("Name")));

    }

    @Test
    public void findMatchingShouldReturnThreeQualifiedNamesWithLastAsterisks() {
        String qualifiedNames = "a.b.C \n a.b.N \n a.NMS \r x.z.y.Name \n";
        InputStream inputStream = new ByteArrayInputStream(qualifiedNames.getBytes());
        classFinder = new ClassFinder(inputStream);
        assertThat(classFinder.findMatching("N*"), hasItems("N", "NMS", "Name"));
    }

    @Test
    public void classFinderShouldContainNoQualifiedNames() {
        String qualifiedNames = "a.b.C";
        InputStream inputStream = new ByteArrayInputStream(qualifiedNames.getBytes());
        classFinder = new ClassFinder(inputStream);
        assertTrue(classFinder.findMatching(" ").isEmpty());
    }

    @Test
    public void classFinderInitWithEmptyNameShouldContainNoQualifiedNames() {
        String qualifiedNames = "";
        InputStream inputStream = new ByteArrayInputStream(qualifiedNames.getBytes());
        classFinder = new ClassFinder(inputStream);
        assertTrue(classFinder.findMatching(" ").isEmpty());
    }

    @Test
    public void classFinderInitWithNullNameShouldContainNoQualifiedNames() {
        byte[] byteString = {};
        InputStream inputStream = new ByteArrayInputStream(byteString);
        classFinder = new ClassFinder(inputStream);
        assertTrue(classFinder.findMatching(" ").isEmpty());
    }

    @Test
    public void findMatchingWithNullStringShouldReturnEmptyList() {
        String qualifiedNames = "";
        InputStream inputStream = new ByteArrayInputStream(qualifiedNames.getBytes());
        classFinder = new ClassFinder(inputStream);

        Collection<String> matchingStrings = classFinder.findMatching(null);

        assertTrue(matchingStrings.isEmpty());

    }


    @Test
    public void findMatchingWithEmptyStringShouldReturnEmptyList() {
        String qualifiedNames = "";
        InputStream inputStream = new ByteArrayInputStream(qualifiedNames.getBytes());
        classFinder = new ClassFinder(inputStream);

        Collection<String> matchingStrings = classFinder.findMatching("");

        assertTrue(matchingStrings.isEmpty());

    }

    @Test
    public void findMatchingWithSpaceStringShouldReturnEmptyList() {
        String qualifiedNames = "";
        InputStream inputStream = new ByteArrayInputStream(qualifiedNames.getBytes());
        classFinder = new ClassFinder(inputStream);

        Collection<String> matchingStrings = classFinder.findMatching(" ");

        assertTrue(matchingStrings.isEmpty());

    }

    @Test
    public void findMatchingWithEndingLineBreakStringShouldAnEntry() {
        String qualifiedNames = "a.b.C \n a.b.N \n a.NMS \r x.z.y.Name \n";
        InputStream inputStream = new ByteArrayInputStream(qualifiedNames.getBytes());
        classFinder = new ClassFinder(inputStream);

        Collection<String> matchingStrings = classFinder.findMatching("NMS ");

        assertThat(matchingStrings, hasItems("NMS"));

    }

    @Test
    public void findMatchingWithSpaceClassNameShouldNotContainSpaceAnEntry() {
        String qualifiedNames = "a.b.C \n  \n a.NMS \r x.z.y.Name \n";
        InputStream inputStream = new ByteArrayInputStream(qualifiedNames.getBytes());
        classFinder = new ClassFinder(inputStream);

        Collection<String> matchingStrings = classFinder.findMatching("NMS ");

        assertThat(matchingStrings, not(hasItem(" ")));

    }


    @Test
    public void findMatchingShouldReturnThreeMatchingClassesWithAsterisksBetween() {
        String qualifiedNames = clazzNamesWithoutSameSimpleNAme;
        InputStream inputStream = new ByteArrayInputStream(qualifiedNames.getBytes());
        classFinder = new ClassFinder(inputStream);

        Collection<String> matchingStrings = classFinder.findMatching("F*Bar");

        assertTrue(matchingStrings.size() == 3);
        assertThat(matchingStrings, hasItems("FioBarBaz", "FooBar", "FooBarBaz"));

    }


    @Test
    public void findMatchingShouldReturnThreeMatchingClassesWithUpperAndLowerCase() {
        String qualifiedNames = clazzNamesWithoutSameSimpleNAme;
        InputStream inputStream = new ByteArrayInputStream(qualifiedNames.getBytes());
        classFinder = new ClassFinder(inputStream);

        Collection<String> matchingStrings = classFinder.findMatching("FBar");

        assertTrue(matchingStrings.size() == 3);
        assertThat(matchingStrings, hasItems("FioBarBaz", "FooBar", "FooBarBaz"));

    }

    @Test
    public void ensureMatchingEntriesAreSorted() {
        String qualifiedNames = "a.A \n a.C \r a.B \n";
        InputStream inputStream = new ByteArrayInputStream(qualifiedNames.getBytes());
        classFinder = new ClassFinder(inputStream);

        List<String> strings = classFinder.findMatching("*").stream().collect(Collectors.toList());

        assertTrue(strings.get(0).equals("A"));
        assertTrue(strings.get(1).equals("B"));
        assertTrue(strings.get(2).equals("C"));
    }


    @Test
    public void findMatchingShouldReturnFourEntriesWHenThereAreMultipleSimpleNames() {
        String qualifiedNames = clazzNames;
        InputStream inputStream = new ByteArrayInputStream(qualifiedNames.getBytes());
        classFinder = new ClassFinder(inputStream);

        Collection<String> matchingStrings = classFinder.findMatching("FBar");

        assertTrue(matchingStrings.size() == 4);
        assertThat(matchingStrings, hasItems("FioBarBaz", "FooBar", "FooBarBaz", "FooBarBaz"));

    }


    @Test
    public void findMatchingShouldReturnEntriesWithBars() {
        String qualifiedNames = clazzNames;
        InputStream inputStream = new ByteArrayInputStream(qualifiedNames.getBytes());
        classFinder = new ClassFinder(inputStream);

        Collection<String> matchingStrings = classFinder.findMatching("*Bar");

        assertThat(matchingStrings, hasItems("FioBarBaz", "FooBar", "FooBarBaz"));

    }

}