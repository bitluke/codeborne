package codeburne.luke;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ClassFinder {
    private InputStream inputStream;
    private List<String> qualifiedNames = new LinkedList<String>();
    private Logger LOG = Logger.getLogger(ClassFinder.class.getSimpleName());

    public ClassFinder(InputStream classNamesStream) {
        if (classNamesStream == null) {
            throw new IllegalStateException("Class finder not initialised");
        }
        inputStream = classNamesStream;
        initQualifiedNames(inputStream);
        processClassNames();
    }

    public Collection<String> findMatching(String pattern) {
        List<String> collect = new ArrayList<>();

        if (pattern == null) {
            return  Collections.emptyList();
        }
        if (pattern.isEmpty()) {
            return  Collections.emptyList();
        }

        StringBuilder newPattern = new StringBuilder();
        if (pattern.contains("*")) {
            if (pattern.length() == 1 ){return qualifiedNames;}
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
            if (pattern.charAt(i) != '*'){
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

    private void initQualifiedNames(InputStream qualifiedNameStream) {
        int intChar;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            while ((intChar = qualifiedNameStream.read()) != -1) {
                stringBuilder.append((char) intChar);
                if (intChar == '\n' || intChar == '\r') {
                    String name = stringBuilder.toString();
                    String substring = name.substring(0, name.length());
                    qualifiedNames.add(substring.trim());
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
    }

    private void processClassNames() {
        for (int index = 0; index < qualifiedNames.size(); index++) {
            String qualifiedName = qualifiedNames.get(index);
            int indexOfDot = qualifiedName.lastIndexOf(".");
            if (indexOfDot >= 1) {
                qualifiedNames.remove(index);
                qualifiedNames.add(index, qualifiedName.substring(indexOfDot + 1));
            } else if (indexOfDot < 0 && !qualifiedName.isEmpty()) {
                qualifiedNames.remove(index);
                qualifiedNames.add(index, qualifiedName.substring(indexOfDot + 1));
            }
        }

        Collections.sort(qualifiedNames);
    }


}



