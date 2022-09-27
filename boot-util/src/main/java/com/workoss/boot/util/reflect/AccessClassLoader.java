/*
 * Copyright 2019-2022 workoss (https://www.workoss.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.workoss.boot.util.reflect;

import java.lang.invoke.MethodHandles;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.WeakHashMap;

;

/**
 * @author admin
 */
class AccessClassLoader extends ClassLoader {

	public static final String JAVA_PREFIX = "java.";

	/**
	 * Weak-references to class loaders, to avoid perm gen memory leaks, for example in
	 * app servers/web containters if the reflectasm library (including this class) is
	 * loaded outside the deployed applications (WAR/EAR) using ReflectASM/Kryo (exts,
	 * user classpath, etc). The key is the parent class loader and the value is the
	 * AccessClassLoader, both are weak-referenced in the hash table.
	 */
	static private final WeakHashMap<ClassLoader, WeakReference<AccessClassLoader>> ACCESS_CLASS_LOADERS = new WeakHashMap();

	/**
	 * Fast-path for classes loaded in the same ClassLoader as this class.
	 */
	static private final ClassLoader SELF_CONTEXT_PARENT_CLASS_LOADER = getParentClassLoader(AccessClassLoader.class);
	static private volatile AccessClassLoader selfContextAccessClassLoader = new AccessClassLoader(
			SELF_CONTEXT_PARENT_CLASS_LOADER);

	static private volatile Method defineClassMethod;

	static AccessClassLoader get(Class type) {
		ClassLoader parent = getParentClassLoader(type);
		// 1. fast-path:
		if (SELF_CONTEXT_PARENT_CLASS_LOADER.equals(parent)) {
			if (selfContextAccessClassLoader == null) {
				// DCL with volatile semantics
				synchronized (ACCESS_CLASS_LOADERS) {
					if (selfContextAccessClassLoader == null) {
						selfContextAccessClassLoader = new AccessClassLoader(SELF_CONTEXT_PARENT_CLASS_LOADER);
					}
				}
			}
			return selfContextAccessClassLoader;
		}
		// 2. normal search:
		synchronized (ACCESS_CLASS_LOADERS) {
			WeakReference<AccessClassLoader> ref = ACCESS_CLASS_LOADERS.get(parent);
			if (ref != null) {
				AccessClassLoader accessClassLoader = ref.get();
				if (accessClassLoader != null) {
					return accessClassLoader;
				}
				else {
					// the value has been GC-reclaimed, but still not the key (defensive
					// sanity)
					ACCESS_CLASS_LOADERS.remove(parent);
				}
			}
			AccessClassLoader accessClassLoader = new AccessClassLoader(parent);
			ACCESS_CLASS_LOADERS.put(parent, new WeakReference<AccessClassLoader>(accessClassLoader));
			return accessClassLoader;
		}
	}

	public static void remove(ClassLoader parent) {
		// 1. fast-path:
		if (SELF_CONTEXT_PARENT_CLASS_LOADER.equals(parent)) {
			selfContextAccessClassLoader = null;
		}
		else {
			// 2. normal search:
			synchronized (ACCESS_CLASS_LOADERS) {
				ACCESS_CLASS_LOADERS.remove(parent);
			}
		}
	}

	public static int activeAccessClassLoaders() {
		int sz = ACCESS_CLASS_LOADERS.size();
		if (selfContextAccessClassLoader != null) {
			sz++;
		}
		return sz;
	}

	private AccessClassLoader(ClassLoader parent) {
		super(parent);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		// These classes come from the classloader that loaded AccessClassLoader.
		if (name.equals(AbstractFieldAccess.class.getName())) {
			return AbstractFieldAccess.class;
		}
		if (name.equals(AbstractMethodAccess.class.getName())) {
			return AbstractMethodAccess.class;
		}
		if (name.equals(AbstractConstructorAccess.class.getName())) {
			return AbstractConstructorAccess.class;
		}
		if (name.equals(AbstractPublicConstructorAccess.class.getName())) {
			return AbstractPublicConstructorAccess.class;
		}
		// All other classes come from the classloader that loaded the type we are
		// accessing.
		return super.loadClass(name, resolve);
	}

	Class<?> defineClass(String name, byte[] bytes) throws ClassFormatError {
		try {
			// Attempt to load the access class in the same loader, which makes protected
			// and default access members accessible.
			return (Class<?>) getDefineClassMethod().invoke(getParent(), new Object[] { name, bytes, Integer.valueOf(0),
					Integer.valueOf(bytes.length), getClass().getProtectionDomain() });
		}
		catch (Exception ignored) {
			// continue with the definition in the current loader (won't have access to
			// protected and package-protected members)
		}
		return defineClass(name, bytes, 0, bytes.length, getClass().getProtectionDomain());
	}

	/**
	 * As per JLS, section 5.3, "The runtime package of a class or interface is determined
	 * by the package name and defining class loader of the class or interface."
	 * @param type1
	 * @param type2
	 * @return
	 */
	static boolean areInSameRuntimeClassLoader(Class type1, Class type2) {

		if (type1.getPackage() != type2.getPackage()) {
			return false;
		}
		ClassLoader loader1 = type1.getClassLoader();
		ClassLoader loader2 = type2.getClassLoader();
		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		if (loader1 == null) {
			return (loader2 == null || loader2 == systemClassLoader);
		}
		if (loader2 == null) {
			return loader1 == systemClassLoader;
		}
		return loader1 == loader2;
	}

	private static ClassLoader getParentClassLoader(Class type) {
		ClassLoader parent = type.getClassLoader();
		if (parent == null) {
			parent = ClassLoader.getSystemClassLoader();
		}
		return parent;
	}

	private static Method getDefineClassMethod() throws Exception {
		// DCL on volatile
		if (defineClassMethod == null) {
			synchronized (ACCESS_CLASS_LOADERS) {
				defineClassMethod = MethodHandles.Lookup.class.getDeclaredMethod("defineClass",
						new Class[] { String.class, byte[].class, int.class, int.class, ProtectionDomain.class });
				// defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass",
				// new Class[]{String.class, byte[].class, int.class,
				// int.class, ProtectionDomain.class});
				try {
					defineClassMethod.setAccessible(true);
				}
				catch (Exception ignored) {
				}
			}
		}
		return defineClassMethod;
	}

}
