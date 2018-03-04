package org.koldar.commons;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PString {

	private static final Pattern BRACKETS = Pattern.compile("\\{(?<template>[^\\}]*)\\}");
	private static final Pattern EMPTY = Pattern.compile("^(:(?<arguments>.*))?$");
	private static final Pattern NUMBER = Pattern.compile("^(?<number>\\d+)(:(?<arguments>.*))?$");
	private static final Pattern KEY = Pattern.compile("^(?<key>[a-zA-Z_][a-zA-z0-9_]+)(:(?<arguments>.*))?$");

	public static String format(String template, Object ...objs) {
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
						
				sb = sb.replace(matcher.start(), matcher.end(), String.format(arguments, objs[number]));
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
						
				sb = sb.replace(matcher.start(), matcher.end(), String.format(arguments, objs[number]));
				continue;
			}
			
			//match key pattern
			Matcher keyMatcher = KEY.matcher(bracketBody);
			if (keyMatcher.find()) {
				String keyStr = keyMatcher.group("key");
				Optional<String> argumentsStr = Optional.ofNullable(keyMatcher.group("arguments"));
				
				int number = findObjectIdByKey(keyStr, nextObjectIndex, objs);  
				String arguments = argumentsStr.isPresent() ? argumentsStr.get() : "%s";
				
				sb = sb.replace(matcher.start(), matcher.end(), String.format(arguments, objs[number]));
				continue;
			}
			
		}
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
