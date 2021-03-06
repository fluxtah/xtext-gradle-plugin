package org.xtext.builder.standalone;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.xtext.builder.standalone.LanguageAccess;
import org.eclipse.xtext.builder.standalone.LanguageAccessFactory;
import org.eclipse.xtext.builder.standalone.StandaloneBuilder;
import org.eclipse.xtext.builder.standalone.StandaloneBuilderModule;
import org.eclipse.xtext.parser.IEncodingProvider;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {

	public static boolean generate(String[] args) throws Exception {
		Injector injector = Guice.createInjector(new StandaloneBuilderModule());
		StandaloneBuilder builder = injector.getInstance(StandaloneBuilder.class);
		builder.setClassPathEntries(new ArrayList<String>());
		builder.setEncoding("UTF-8");

		List<String> sourcePath = Lists.newArrayList();
		File workingDirectory = new File("");
		LanguageConfigurationParser languageParser = new LanguageConfigurationParser();

		Iterator<String> arguments = Arrays.asList(args).iterator();
		while (arguments.hasNext()) {
			String argument = arguments.next();
			if ("-classpath".equals(argument.trim()) || "-cp".equals(argument.trim())) {
				builder.setClassPathEntries(Splitter.on(File.pathSeparator).split(arguments.next().trim()));
			} else if ("-tempdir".equals(argument.trim()) || "-td".equals(argument.trim())) {
				builder.setTempDir(arguments.next().trim());
			} else if ("-encoding".equals(argument.trim())) {
				builder.setEncoding(arguments.next().trim());
			} else if ("-cwd".equals(argument.trim())) {
				workingDirectory = new File(arguments.next().trim());
			} else if (argument.trim().startsWith("-L")) {
				languageParser.addArgument(argument);
			} else {
				sourcePath.add(argument);
			}
		}
		Map<String, LanguageAccess> languages = new LanguageAccessFactory().createLanguageAccess(
				languageParser.getLanguages(), Main.class.getClassLoader(), workingDirectory);
		fixEncoding(languages, builder);
		builder.setLanguages(languages);
		builder.setSourceDirs(sourcePath);
		try {
			Method javaSourceDirsSetter = StandaloneBuilder.class.getMethod("setJavaSourceDirs", Iterable.class);
			javaSourceDirsSetter.invoke(builder, sourcePath);
		} catch (Exception ignored) {}
		return builder.launch();
	}

	private static void fixEncoding(Map<String, LanguageAccess> languages, StandaloneBuilder builder) {
		for (LanguageAccess language : languages.values()) {
			IEncodingProvider encodingProvider = language.getEncodingProvider();
			if (encodingProvider instanceof IEncodingProvider.Runtime) {
				((IEncodingProvider.Runtime) encodingProvider).setDefaultEncoding(builder.getEncoding());
			}
		}
	}
}
