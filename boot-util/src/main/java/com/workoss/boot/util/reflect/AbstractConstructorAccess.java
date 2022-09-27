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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.springframework.asm.ClassWriter;
import org.springframework.asm.MethodVisitor;

import static org.springframework.asm.Opcodes.*;

/**
 * @author admin
 */
@SuppressWarnings("ALL")
public abstract class AbstractConstructorAccess<T> {

	boolean isNonStaticMemberClass;

	public boolean isNonStaticMemberClass() {
		return isNonStaticMemberClass;
	}

	/**
	 * Constructor for top-level classes and static nested classes.
	 * <p>
	 * If the underlying class is a inner (non-static nested) class, a new instance will
	 * be created using <code>null</code> as the this$0 synthetic reference. The
	 * instantiated object will work as long as it actually don't use any member variable
	 * or method fron the enclosing instance.
	 * @return 实例
	 */
	abstract public T newInstance();

	/**
	 * Constructor for inner classes (non-static nested classes).
	 * @param enclosingInstance The instance of the enclosing type to which this inner
	 * instance is related to (assigned to its synthetic this$0 field).
	 * @return 实例
	 */
	abstract public T newInstance(Object enclosingInstance);

	static public <T> AbstractConstructorAccess<T> get(Class<T> type) {
		Class enclosingType = type.getEnclosingClass();
		boolean isNonStaticMemberClass = enclosingType != null && type.isMemberClass()
				&& !Modifier.isStatic(type.getModifiers());

		String className = type.getName();
		String accessClassName = className + "AbstractConstructorAccess";
		if (accessClassName.startsWith(AccessClassLoader.JAVA_PREFIX)) {
			accessClassName = "reflectasm." + accessClassName;
		}
		Class accessClass;

		AccessClassLoader loader = AccessClassLoader.get(type);
		try {
			accessClass = loader.loadClass(accessClassName);
		}
		catch (ClassNotFoundException ignored) {
			synchronized (loader) {
				try {
					accessClass = loader.loadClass(accessClassName);
				}
				catch (ClassNotFoundException ignored2) {
					String accessClassNameInternal = accessClassName.replace('.', '/');
					String classNameInternal = className.replace('.', '/');
					String enclosingClassNameInternal;
					Constructor<T> constructor = null;
					int modifiers = 0;
					if (!isNonStaticMemberClass) {
						enclosingClassNameInternal = null;
						try {
							constructor = type.getDeclaredConstructor((Class[]) null);
							modifiers = constructor.getModifiers();
						}
						catch (Exception ex) {
							throw new RuntimeException(
									"Class cannot be created (missing no-arg constructor): " + type.getName(), ex);
						}
						if (Modifier.isPrivate(modifiers)) {
							throw new RuntimeException(
									"Class cannot be created (the no-arg constructor is private): " + type.getName());
						}
					}
					else {
						enclosingClassNameInternal = enclosingType.getName().replace('.', '/');
						try {
							// Inner classes should have this.
							constructor = type.getDeclaredConstructor(enclosingType);
							modifiers = constructor.getModifiers();
						}
						catch (Exception ex) {
							throw new RuntimeException(
									"Non-static member class cannot be created (missing enclosing class constructor): "
											+ type.getName(),
									ex);
						}
						if (Modifier.isPrivate(modifiers)) {
							throw new RuntimeException(
									"Non-static member class cannot be created (the enclosing class constructor is private): "
											+ type.getName());
						}
					}
					String superclassNameInternal = Modifier.isPublic(modifiers)
							? "com/workoss/boot/util/reflect/AbstractPublicConstructorAccess"
							: "com/workoss/boot/util/reflect/AbstractConstructorAccess";

					ClassWriter cw = new ClassWriter(0);
					cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null, superclassNameInternal, null);

					insertConstructor(cw, superclassNameInternal);
					insertNewInstance(cw, classNameInternal);
					insertNewInstanceInner(cw, classNameInternal, enclosingClassNameInternal);

					cw.visitEnd();
					accessClass = loader.defineClass(accessClassName, cw.toByteArray());
				}
			}
		}
		AbstractConstructorAccess<T> access;
		try {
			access = (AbstractConstructorAccess<T>) accessClass.newInstance();
		}
		catch (Throwable t) {
			throw new RuntimeException("Exception constructing constructor access class: " + accessClassName, t);
		}
		if (!(access instanceof AbstractPublicConstructorAccess)
				&& !AccessClassLoader.areInSameRuntimeClassLoader(type, accessClass)) {
			// Must test this after the try-catch block, whether the class has been loaded
			// as if has been defined.
			// Throw a Runtime exception here instead of an IllegalAccessError when
			// invoking newInstance()
			throw new RuntimeException((!isNonStaticMemberClass
					? "Class cannot be created (the no-arg constructor is protected or package-protected, and its ConstructorAccess could not be defined in the same class loader): "
					: "Non-static member class cannot be created (the enclosing class constructor is protected or package-protected, and its ConstructorAccess could not be defined in the same class loader): ")
					+ type.getName());
		}
		access.isNonStaticMemberClass = isNonStaticMemberClass;
		return access;
	}

	static private void insertConstructor(ClassWriter cw, String superclassNameInternal) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, superclassNameInternal, "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	static void insertNewInstance(ClassWriter cw, String classNameInternal) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "()Ljava/lang/Object;", null, null);
		mv.visitCode();
		mv.visitTypeInsn(NEW, classNameInternal);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", "()V", false);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
	}

	static void insertNewInstanceInner(ClassWriter cw, String classNameInternal, String enclosingClassNameInternal) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(Ljava/lang/Object;)Ljava/lang/Object;", null,
				null);
		mv.visitCode();
		if (enclosingClassNameInternal != null) {
			mv.visitTypeInsn(NEW, classNameInternal);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, enclosingClassNameInternal);
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", "(L" + enclosingClassNameInternal + ";)V",
					false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(4, 2);
		}
		else {
			mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
			mv.visitInsn(DUP);
			mv.visitLdcInsn("Not an inner class.");
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>",
					"(Ljava/lang/String;)V", false);
			mv.visitInsn(ATHROW);
			mv.visitMaxs(3, 2);
		}
		mv.visitEnd();
	}

}
