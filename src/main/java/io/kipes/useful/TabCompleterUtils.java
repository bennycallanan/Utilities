package io.kipes.useful;

import net.minecraft.util.com.google.common.base.Preconditions;

import java.util.List;
import java.util.stream.Collectors;

public class TabCompleterUtils {

    public static List<String> setCompletions(String[] arguments, List<String> input) {
        Preconditions.checkNotNull(arguments);
        Preconditions.checkArgument(arguments.length != 0);

        String argument = arguments[arguments.length - 1];

        return input.stream().filter(string -> string.regionMatches(true, 0, argument, 0, argument.length())).limit(80).collect(Collectors.toList());
    }

}