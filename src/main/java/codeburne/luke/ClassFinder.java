package codeburne.luke;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ClassFinder {
    public static final List<String> EMPTY_LIST = Collections.emptyList();
    private InputStream inputStream;
    private Collection<String> qualifiedNames = new ArrayList<String>();
    private Collection<String> finalNames = new ArrayList<String>();
    private Logger LOG = Logger.getLogger(ClassFinder.class.getSimpleName());

    public ClassFinder(InputStream classNamesStream) {
        if (classNamesStream == null) {
            throw new NullPointerException("Input Stream can't be null");
        }
        inputStream = classNamesStream;
        qualifiedNames = initQualifiedNames();
        finalNames = processClassNames();
    }

    public Collection<String> findMatching(String pattern) {
        List<String> collect = new ArrayList<>();

        if (pattern == null || pattern.isEmpty()) {
            return EMPTY_LIST;
        }

        StringBuilder newPattern = new StringBuilder();
        if (pattern.contains("*")) {
            if (pattern.length() == 1) {
                return qualifiedNames;
            }
            return processAsterisks(pattern, collect, newPattern);
        }

        if (pattern.endsWith(" ")) {
            return processEndingWithSpace(pattern, collect);
        }

        collect = processUpperCase(pattern, collect);
        processLowerCase(pattern, collect);
        return collect;

    }

    private Collection<String> processAsterisks(String pattern, List<String> collect, StringBuilder newPattern) {
        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) != '*') {
                newPattern.append(pattern.charAt(i));
            }
        }
        collect = processUpperCase(newPattern.toString(), collect);
        processLowerCase(pattern, collect);
        return collect;
    }

    private Collection<String> processEndingWithSpace(String pattern, List<String> collect) {
        collect = processUpperCase(pattern, collect);

        for (int i = pattern.toCharArray().length - 1; i >= 0; i--) {
            Character ch = pattern.toCharArray()[i];
            if (Character.isUpperCase(ch)) {
                collect = qualifiedNames
                        .stream()
                        .filter(s -> s.endsWith(pattern.substring(pattern.lastIndexOf(ch)).trim()))
                        .collect(Collectors.toList());
                break;
            }

        }

        return collect;
    }

    private List<String> processUpperCase(String pattern, List<String> collect) {
        for (Character c : pattern.toCharArray()) {
            if (Character.isUpperCase(c)) {
                collect = qualifiedNames
                        .stream()
                        .filter(s -> s.contains(c.toString()))
                        .collect(Collectors.toList());
            }
            qualifiedNames = collect;
        }
        return collect;
    }

    private void processLowerCase(String pattern, List<String> collect) {
        for (Character c : pattern.toCharArray()) {
            if (Character.isLowerCase(c)) {
                collect = qualifiedNames
                        .stream()
                        .filter(s -> s.contains(c.toString()))
                        .collect(Collectors.toList());
            }
            qualifiedNames = collect;
        }
    }

    private Collection<String> initQualifiedNames() {
        int intChar;
        Collection<String> processQualifiedNames = new ArrayList();
        StringBuilder stringBuilder = new StringBuilder();

        try {
            while ((intChar = inputStream.read()) != -1) {
                stringBuilder.append((char) intChar);
                if (intChar == '\n' || intChar == '\r') {
                    String name = stringBuilder.toString();
                    String substring = name.substring(0, name.length());
                    processQualifiedNames.add(substring.trim());
                    stringBuilder.delete(0, stringBuilder.length());
                }
            }
        } catch (IOException e) {
            LOG.severe("Can't read anymore");
            try {
                inputStream.close();
            } catch (IOException e1) {
                LOG.severe("Can't close stream");
            }
        }
        return processQualifiedNames;
    }

    private Collection<String> processClassNames() {
        return qualifiedNames.stream()
                .map(this::getClassName)
                .collect(Collectors.toList());
    }

    private String getClassName(String qualifiedName) {
        int dotIndex = qualifiedName.lastIndexOf(".");
        return qualifiedName.substring(dotIndex == -1 ? 0 : dotIndex + 1, qualifiedName.length());
    }


}



