package org.koldar.commons;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Module implementing python-like format string in java.
 * 
 * It's not as powerful as as beartiful as python, but it's pretty close.
 * 
 * 
 * @author koldar
 *
 */
public class PString {

	private static final Pattern BRACKETS = Pattern.compile("\\{(?<template>[^\\}]*)\\}");
	private static final Pattern EMPTY = Pattern.compile("^(:(?<arguments>.*))?$");
	private static final Pattern NUMBER = Pattern.compile("^(?<number>\\d+)(:(?<arguments>.*))?$");
	private static final Pattern KEY = Pattern.compile("^(?<key>[a-zA-Z_][a-zA-z0-9_]+)(:(?<arguments>.*))?$");
	
	/**
	 * Format a string <tt>template</tt> by using the arguments in <tt>objs</tt>
	 * 
	 * The following rules applies when formatting the string. First of all <tt>{</tt> is used to open a format parameter and <tt>}</tt> to close a format parameter.
	 * The general syntax is the following one:
	 * 
	 *  <pre>
	 *  format-parameter :: { ([\d]+ | [a-zA-Z_][a-zA-Z0-9_]+)? (:.*)? } 
	 *  </pre>
	 *  
	 *  The string before ":" is the key used to find which object you're requesting while the string after the optional ":" is, if specifcied, the formatting
	 *  used to format the object requested. The formatting used after ":" follows the syntactical rules of {@link String#format(Local, String, Object...)}.
	 *  
	 *  The keys can be 3 elements. It can be a non negative number. If this is the case the objects are access like a list. In other words:
	 *  
	 * 	<pre>
	 *  PString.format("Hello {0}! I'm {1}", "world", "Pizza"); /Hello world! I'm Pizza
	 *  PString.format("Hello {1}! I'm {1}", "world", "Pizza"); /Hello Pizza! I'm Pizza
	 *  </pre>
	 *  
	 *  The keys can be totally empty. In this case the function will implicitly assign the number sequentally:
	 *  <pre>
	 *  PString.format("Hello {}! I'm {}", "world", "Pizza"); /Hello world! I'm Pizza
	 *  //as if PString.format("Hello {0}! I'm {1}", "world", "Pizza");
	 *  </pre>
	 *  
	 *  Finally the keys can be assigned map-like. This has been implemented to emulate the python format capability:
	 *  
	 *  <pre>
	 * 	"Hello {stuff}! I'm {name}".format(stuff="world", name="Pizza")
	 *  </pre>
	 * 
	 * In java we can't use such syntax. The close we can do is doingin somthing like this:
	 *  <pre>
	 *  PString.format("Hello {stuff}! I'm {name}", 
	 *  	"stuff", "world", //first key-value 
	 *  	"name", "Pizza" //second key-value
	 *  );
	 *  </pre>
	 * 
	 * Mixing these 3 styles in a single format leads to <b>UB</b>.
	 * You can specifies the formatting as in this example:
	 * 
	 * <pre>
	 * PString.format("Hello {}! Your number is {:%04d}", "Pizza", 5); /Hello Pizza! Your number is 0004
	 * </pre>
	 * 
	 * @param locale the locale to use when formatting the string
	 * @param template the string to format
	 * @param objs the objects used to format
	 * @return the formatted string
	 */
	public static String format(Locale locale, String template, Object ...objs) {
		int nextObjectIndex = 0;
		StringBuilder sb = new StringBuilder(template);
		
		while(true) {
			Matcher matcher = BRACKETS.matcher(sb.toString());
			if (!matcher.find()) {
				return sb.toString();
			}
			String bracketBody = matcher.group("template");
			
			//match empty pattern
			Matcher emptyMatcher = EMPTY.matcher(bracketBody);
			if (emptyMatcher.find()) {
				Optional<String> argumentsStr = Optional.ofNullable(emptyMatcher.group("arguments"));
				
				int number = nextObjectIndex;
				String arguments = argumentsStr.isPresent() ? argumentsStr.get() : "%s";  
						
				sb = sb.replace(matcher.start(), matcher.end(), String.format(locale, arguments, objs[number]));
				nextObjectIndex += 1;
				continue;
			}
			
			//match number pattern
			Matcher numberMatcher = NUMBER.matcher(bracketBody);
			if (numberMatcher.find()) {
				String numberStr = numberMatcher.group("number");
				Optional<String> argumentsStr = Optional.ofNullable(numberMatcher.group("arguments"));
				
				int number = Integer.parseInt(numberStr);
				String arguments = argumentsStr.isPresent() ? argumentsStr.get() : "%s";  
						
				sb = sb.replace(matcher.start(), matcher.end(), String.format(locale, arguments, objs[number]));
				continue;
			}
			
			//match key pattern
			Matcher keyMatcher = KEY.matcher(bracketBody);
			if (keyMatcher.find()) {
				String keyStr = keyMatcher.group("key");
				Optional<String> argumentsStr = Optional.ofNullable(keyMatcher.group("arguments"));
				
				int number = findObjectIdByKey(keyStr, nextObjectIndex, objs);  
				String arguments = argumentsStr.isPresent() ? argumentsStr.get() : "%s";
				
				sb = sb.replace(matcher.start(), matcher.end(), String.format(locale, arguments, objs[number]));
				continue;
			}
			
		}
	}
	
	/**
	 * like {@link PString#format(Locale, String, Object...)} but with the null {@link Locale} 
	 * 
	 * @param template the stirng to use as baseline
	 * @param objs the objects to format the string with
	 * @return the formatted string
	 */
	public static String format(String template, Object ...objs) {
		return PString.format(null, template, objs);
	}
	
	private static int findObjectIdByKey(String key, int nextObjectIndex, Object... objects) {
		for (int i=0; i<objects.length; i+=2) {
			if (objects[i].toString().equals(key)) {
				return i+1;
			}
		}
		return nextObjectIndex;
	}

}
