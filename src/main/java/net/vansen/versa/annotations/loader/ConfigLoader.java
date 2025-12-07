package net.vansen.versa.annotations.loader;

import net.vansen.versa.Versa;
import net.vansen.versa.annotations.ConfigFile;
import net.vansen.versa.annotations.ConfigPath;
import net.vansen.versa.annotations.adapter.Adapters;
import net.vansen.versa.annotations.adapter.ConfigAdapter;
import net.vansen.versa.builder.NodeBuilder;
import net.vansen.versa.builder.ValueBuilder;
import net.vansen.versa.node.Node;
import net.vansen.versa.node.Value;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads and maps config values into Java static fields using {@code @ConfigFile} and {@code @ConfigPath}.
 * If the config file does not exist, it will be generated automatically using the default field values.
 * <p>
 * <p><b>Supports:</b></p>
 * <ul>
 *     <li>Primitive types & wrappers (String, int, long, boolean, double...)</li>
 *     <li>{@code List<T>} where T is either primitive-like or adapter-supported</li>
 *     <li>Custom objects using {@link ConfigAdapter}</li>
 * </ul>
 *
 * <p>Example:</p>
 * <pre>{@code
 * @ConfigFile("config.versa")
 * public class MyConfig {
 *     @ConfigPath("name") public static String name = "Server";
 *     @ConfigPath("port") public static int port = 25565;
 * }
 *
 * public static void main(String[] args) {
 *     ConfigLoader.load(MyConfig.class);
 *     System.out.println(MyConfig.name);
 * }
 * }</pre>
 *
 * <p><b>Note:</b> Versa Config Loader is still in active development.
 * Expect API changes and occasional bugs.</p>
 */
@SuppressWarnings("unchecked")
public final class ConfigLoader {

    private static final List<Class<?>> loaded = new ArrayList<>();
    private static final Map<Field, Object> defaults = new HashMap<>();

    public static void load(Class<?> cls) {
        ConfigFile fileAnn = cls.getAnnotation(ConfigFile.class);
        if (fileAnn == null) return;

        saveDefaults(cls);

        Path file = Path.of(fileAnn.value());
        if (!Files.exists(file)) {
            Node built = buildFromDefaults(cls);
            write(file, built.toString());
        }

        apply(cls);

        if (!loaded.contains(cls)) loaded.add(cls);
    }

    public static void reload() {
        for (Class<?> c : loaded) apply(c);
    }

    private static void saveDefaults(Class<?> c) {
        for (Field f : c.getDeclaredFields()) {
            ConfigPath cp = f.getAnnotation(ConfigPath.class);
            if (cp == null) continue;
            if (!Modifier.isStatic(f.getModifiers()))
                throw new RuntimeException("Config field '" + f.getName() + "' in " + c.getSimpleName() + " must be static");
            if (Modifier.isFinal(f.getModifiers()))
                throw new RuntimeException("Config field '" + f.getName() + "' in " + c.getSimpleName() + " cannot be final");
            try {
                f.setAccessible(true);
                defaults.putIfAbsent(f, f.get(null));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void apply(Class<?> c) {
        try {
            ConfigFile file = c.getAnnotation(ConfigFile.class);
            if (file == null) return;

            Node root = Versa.parse(file.value());

            for (Field f : c.getDeclaredFields()) {
                ConfigPath cp = f.getAnnotation(ConfigPath.class);
                if (cp == null) continue;
                if (!Modifier.isStatic(f.getModifiers()))
                    throw new RuntimeException("Config field '" + f.getName() + "' in " + c.getSimpleName() + " must be static");

                Object v = fetch(root, cp.value(), f);

                if (v == null) {
                    Object def = defaults.get(f);
                    set(f, def);
                } else {
                    set(f, v);
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> generic(Field f) {
        ParameterizedType p = (ParameterizedType) f.getGenericType();
        return (Class<?>) p.getActualTypeArguments()[0];
    }

    private static Object fetch(Node root, String path, Field f) {
        Class<?> t = f.getType();

        if (t == String.class) return root.getString(path);
        if (t == int.class || t == Integer.class) return root.getInteger(path);
        if (t == long.class || t == Long.class) return root.getLong(path);
        if (t == boolean.class || t == Boolean.class) return root.getBool(path);
        if (t == double.class || t == Double.class) return root.getDouble(path);
        if (t == float.class || t == Float.class) {
            Double d = root.getDouble(path);
            return d == null ? null : d.floatValue();
        }

        if (List.class.isAssignableFrom(t)) {
            Value v = root.getValue(path);
            if (v == null) return null;

            Class<?> comp = generic(f);
            ConfigAdapter<?> ad = Adapters.get(comp);

            if (ad != null && v.branchList != null) {
                List<Object> list = new ArrayList<>();
                for (Node nd : v.branchList)
                    list.add(((ConfigAdapter<Object>) ad).fromNode(nd));
                return list;
            }

            if (v.list != null) {
                List<Object> out = new ArrayList<>();
                for (Value x : v.list) out.add(x.raw());
                return out;
            }
            return null;
        }

        ConfigAdapter<?> ad = Adapters.get(t);
        if (ad != null) {
            Node nd = find(root, path);
            if (nd != null) return ((ConfigAdapter<Object>) ad).fromNode(nd);
            return null;
        }

        return null;
    }

    private static Node find(Node n, String path) {
        Node cur = n;
        for (String p : path.split("\\.")) {
            boolean ok = false;
            for (Node c : cur.children) {
                if (c.name.equals(p)) {
                    cur = c;
                    ok = true;
                    break;
                }
            }
            if (!ok) return null;
        }
        return cur;
    }

    private static void set(Field f, Object v) {
        try {
            VarHandle h = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup())
                    .findStaticVarHandle(f.getDeclaringClass(), f.getName(), f.getType());
            h.set(v);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static Node buildFromDefaults(Class<?> cls) {
        NodeBuilder root = new NodeBuilder();

        for (Field f : cls.getDeclaredFields()) {
            ConfigPath cp = f.getAnnotation(ConfigPath.class);
            if (cp == null) continue;

            Object def = defaults.get(f);
            if (def == null) continue;

            String name = cp.value();
            Class<?> type = f.getType();

            ConfigAdapter<?> ad = Adapters.get(type);
            if (ad != null) {
                NodeBuilder nb = new NodeBuilder().name(name);
                ((ConfigAdapter<Object>) ad).toNode(def, nb);
                root.child(nb.build());
                root.emptyLine();
                continue;
            }

            if (List.class.isAssignableFrom(type)) {
                List<?> list = (List<?>) def;
                Class<?> comp = generic(f);
                ConfigAdapter<?> cad = Adapters.get(comp);

                if (cad != null) {
                    Node[] arr = new Node[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        Object o = list.get(i);
                        NodeBuilder nb = new NodeBuilder().name(name + "_" + i);
                        ((ConfigAdapter<Object>) cad).toNode(o, nb);
                        Node built = nb.build();
                        built.name = null;
                        arr[i] = built;
                    }
                    ValueBuilder vb = new ValueBuilder().name(name).branches(arr);
                    root.add(vb);
                    root.emptyLine();
                    continue;
                }

                List<Value> lv = new ArrayList<>();
                for (Object o : list) lv.add(asValue(o));
                root.add(new ValueBuilder().name(name).list(lv.toArray(new Value[0])));
                root.emptyLine();
                continue;
            }

            ValueBuilder vb = new ValueBuilder().name(name);

            if (def instanceof String s) vb.string(s);
            else if (def instanceof Integer i) vb.intVal(i);
            else if (def instanceof Long l) vb.longVal(l);
            else if (def instanceof Boolean b) vb.bool(b);
            else if (def instanceof Double d) vb.doubleVal(d);
            else if (def instanceof Float fv) vb.floatVal(fv);
            else continue;

            root.add(vb);
            root.emptyLine();
        }

        return root.build();
    }

    private static Value asValue(Object o) {
        ValueBuilder v = new ValueBuilder();
        if (o instanceof String s) return v.string(s).build();
        if (o instanceof Integer i) return v.intVal(i).build();
        if (o instanceof Long l) return v.longVal(l).build();
        if (o instanceof Boolean b) return v.bool(b).build();
        if (o instanceof Double d) return v.doubleVal(d).build();
        if (o instanceof Float f) return v.floatVal(f.doubleValue()).build();
        return v.string(o.toString()).build();
    }

    private static void write(Path p, String s) {
        try {
            if (p.getParent() != null) Files.createDirectories(p.getParent());
            Files.writeString(p, s);
        } catch (Exception e) {
            throw new RuntimeException("Failed writing config " + p, e);
        }
    }
}