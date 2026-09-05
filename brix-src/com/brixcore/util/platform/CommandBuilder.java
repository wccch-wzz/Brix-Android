package com.brixcore.util.platform;

import com.brixcore.util.Logging;
import com.brixcore.util.io.NetworkUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import kotlin.text.Typography;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;

/* JADX INFO: loaded from: classes7.dex */
public final class CommandBuilder {
    private final List<Item> raw = new ArrayList();
    private static final Pattern UNSTABLE_OPTION_PATTERN = Pattern.compile("-XX:(?<key>[a-zA-Z0-9]+)=(?<value>.*)");
    private static final Pattern UNSTABLE_BOOLEAN_OPTION_PATTERN = Pattern.compile("-XX:(?<value>[+\\-])(?<key>[a-zA-Z0-9]+)");

    /* JADX INFO: renamed from: $r8$lambda$wBZ5N9SAmxJLRX-vx8hIUeo_fgc, reason: not valid java name */
    public static /* synthetic */ ArrayList m349$r8$lambda$wBZ5N9SAmxJLRXvx8hIUeo_fgc() {
        return new ArrayList();
    }

    private String parse(String s) {
        return toShellStringLiteral(s);
    }

    public CommandBuilder add(String... args) {
        for (String s : args) {
            this.raw.add(new Item(s, true));
        }
        return this;
    }

    public CommandBuilder addAll(Collection<String> args) {
        for (String s : args) {
            this.raw.add(new Item(s, true));
        }
        return this;
    }

    public CommandBuilder addWithoutParsing(String... args) {
        for (String s : args) {
            this.raw.add(new Item(s, false));
        }
        return this;
    }

    public CommandBuilder addAllWithoutParsing(Collection<String> args) {
        for (String s : args) {
            this.raw.add(new Item(s, false));
        }
        return this;
    }

    public void addAllDefault(Collection<String> args) {
        addAllDefault(args, true);
    }

    public void addAllDefaultWithoutParsing(Collection<String> args) {
        addAllDefault(args, false);
    }

    private void addAllDefault(Collection<String> args, boolean parse) {
        Item item;
        for (String arg : args) {
            if (arg.startsWith("-D")) {
                int idx = arg.indexOf(61);
                if (idx >= 0) {
                    addDefault(arg.substring(0, idx + 1), arg.substring(idx + 1), parse);
                } else {
                    String opt = arg + NetworkUtils.NAME_VALUE_SEPARATOR;
                    Iterator<Item> it = this.raw.iterator();
                    do {
                        if (it.hasNext()) {
                            item = it.next();
                            if (item.arg.startsWith(opt)) {
                                Logging.LOG.info("Default option '" + arg + "' is suppressed by '" + item.arg + "'");
                                break;
                            }
                        } else {
                            this.raw.add(new Item(arg, parse));
                            break;
                        }
                    } while (!item.arg.equals(arg));
                }
            } else {
                if (arg.startsWith("-XX:")) {
                    Matcher matcher = UNSTABLE_OPTION_PATTERN.matcher(arg);
                    if (matcher.matches()) {
                        addUnstableDefault(matcher.group("key"), matcher.group("value"), parse);
                    } else {
                        Matcher matcher2 = UNSTABLE_BOOLEAN_OPTION_PATTERN.matcher(arg);
                        if (matcher2.matches()) {
                            addUnstableDefault(matcher2.group("key"), Marker.ANY_NON_NULL_MARKER.equals(matcher2.group("value")), parse);
                        }
                    }
                }
                if (arg.startsWith("-X")) {
                    String opt2 = null;
                    String value = null;
                    String[] strArr = {"-Xmx", "-Xms", "-Xmn", "-Xss"};
                    for (int i = 0; i < 4; i++) {
                        String prefix = strArr[i];
                        if (arg.startsWith(prefix)) {
                            opt2 = prefix;
                            value = arg.substring(prefix.length());
                            break;
                        }
                    }
                    if (opt2 != null) {
                        addDefault(opt2, value, parse);
                    }
                }
                Iterator<Item> it2 = this.raw.iterator();
                do {
                    if (!it2.hasNext()) {
                        this.raw.add(new Item(arg, parse));
                        break;
                    }
                } while (!it2.next().arg.equals(arg));
            }
        }
    }

    public String addDefault(String opt, String value) {
        return addDefault(opt, value, true);
    }

    private String addDefault(String opt, String value, boolean parse) {
        for (Item item : this.raw) {
            if (item.arg.startsWith(opt)) {
                Logging.LOG.info("Default option '" + opt + value + "' is suppressed by '" + item.arg + "'");
                return item.arg;
            }
        }
        this.raw.add(new Item(opt + value, parse));
        return null;
    }

    public String addUnstableDefault(String opt, boolean value) {
        return addUnstableDefault(opt, value, true);
    }

    private String addUnstableDefault(String opt, boolean value, boolean parse) {
        for (Item item : this.raw) {
            Matcher matcher = UNSTABLE_BOOLEAN_OPTION_PATTERN.matcher(item.arg);
            if (matcher.matches() && matcher.group("key").equals(opt)) {
                return item.arg;
            }
        }
        if (value) {
            this.raw.add(new Item("-XX:+" + opt, parse));
            return null;
        }
        this.raw.add(new Item("-XX:-" + opt, parse));
        return null;
    }

    public String addUnstableDefault(String opt, String value) {
        return addUnstableDefault(opt, value, true);
    }

    private String addUnstableDefault(String opt, String value, boolean parse) {
        for (Item item : this.raw) {
            Matcher matcher = UNSTABLE_OPTION_PATTERN.matcher(item.arg);
            if (matcher.matches() && matcher.group("key").equals(opt)) {
                return item.arg;
            }
        }
        this.raw.add(new Item("-XX:" + opt + NetworkUtils.NAME_VALUE_SEPARATOR + value, parse));
        return null;
    }

    public boolean removeIf(final Predicate<String> pred) {
        return this.raw.removeIf(new Predicate() { // from class: com.brixcore.util.platform.CommandBuilder$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return pred.test(((CommandBuilder.Item) obj).arg);
            }
        });
    }

    public boolean noneMatch(final Predicate<String> predicate) {
        return this.raw.stream().noneMatch(new Predicate() { // from class: com.brixcore.util.platform.CommandBuilder$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return predicate.test(((CommandBuilder.Item) obj).arg);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$toString$2(Item i) {
        return i.parse ? parse(i.arg) : i.arg;
    }

    public String toString() {
        return (String) this.raw.stream().map(new Function() { // from class: com.brixcore.util.platform.CommandBuilder$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$toString$2((CommandBuilder.Item) obj);
            }
        }).collect(Collectors.joining(StringUtils.SPACE));
    }

    public List<String> asList() {
        return (List) this.raw.stream().map(new Function() { // from class: com.brixcore.util.platform.CommandBuilder$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((CommandBuilder.Item) obj).arg;
            }
        }).collect(Collectors.toList());
    }

    public List<String> asMutableList() {
        return (List) this.raw.stream().map(new Function() { // from class: com.brixcore.util.platform.CommandBuilder$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((CommandBuilder.Item) obj).arg;
            }
        }).collect(Collectors.toCollection(new Supplier() { // from class: com.brixcore.util.platform.CommandBuilder$$ExternalSyntheticLambda2
            @Override // java.util.function.Supplier
            public final Object get() {
                return CommandBuilder.m349$r8$lambda$wBZ5N9SAmxJLRXvx8hIUeo_fgc();
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class Item {
        final String arg;
        final boolean parse;

        Item(String arg, boolean parse) {
            this.arg = arg;
            this.parse = parse;
        }

        public String toString() {
            return this.parse ? CommandBuilder.toShellStringLiteral(this.arg) : this.arg;
        }
    }

    public static String pwshString(String str) {
        return "'" + str.replace("'", "''") + "'";
    }

    public static boolean hasExecutionPolicy() {
        try {
            boolean z = true;
            Process process = Runtime.getRuntime().exec(new String[]{"powershell", "-Command", "Get-ExecutionPolicy"});
            if (!process.waitFor(5L, TimeUnit.SECONDS)) {
                process.destroy();
                return false;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), OperatingSystem.NATIVE_CHARSET));
            try {
                String policy = reader.readLine();
                if (!"Unrestricted".equalsIgnoreCase(policy) && !"RemoteSigned".equalsIgnoreCase(policy)) {
                    z = false;
                }
                reader.close();
                return z;
            } catch (Throwable th) {
                try {
                    reader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (Throwable th3) {
            return false;
        }
    }

    public static boolean setExecutionPolicy() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"powershell", "-Command", "Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser"});
            if (!process.waitFor(5L, TimeUnit.SECONDS)) {
                process.destroy();
                return false;
            }
        } catch (Throwable th) {
        }
        return true;
    }

    private static boolean containsEscape(String str, String escapeChars) {
        for (int i = 0; i < escapeChars.length(); i++) {
            if (str.indexOf(escapeChars.charAt(i)) >= 0) {
                return true;
            }
        }
        return false;
    }

    private static String escape(String str, char... escapeChars) {
        for (char ch : escapeChars) {
            str = str.replace("" + ch, "\\" + ch);
        }
        return str;
    }

    public static String toBatchStringLiteral(String s) {
        return containsEscape(s, " \t\"^&<>|") ? Typography.quote + escape(s, IOUtils.DIR_SEPARATOR_WINDOWS, Typography.quote) + Typography.quote : s;
    }

    public static String toShellStringLiteral(String s) {
        return containsEscape(s, " \t\"!#$&'()*,;<=>?[\\]^`{|}~") ? Typography.quote + escape(s, Typography.quote, '$', Typography.amp, '`') + Typography.quote : s;
    }
}
