/*
 * Copyright (C) 2010 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of Enigma Plugin.
 * Enigma Plugin is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.enigma.backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.enigma.EnigmaRunner;
import org.enigma.TargetHandler;
import org.enigma.TargetHandler.TargetSelection;
import org.enigma.backend.EnigmaDriver.SyntaxError;

public class EnigmaSettings
	{
	//Compatibility / Progress options
	public int cppStrings = 0; // Defines what language strings are inherited from.               0 = GML,               1 = C
	public int cppOperators = 0; // Defines what language operators ++ and -- are inherited from. 0 = GML,               1 = C
	public int cppEquals = 0; // Defines whether = should be exclusively treated as a setter.     0 = GML (= or ==)      1 = C (= only)
	public int literalHandling = 0; // Determines how literals are treated.                       0 = enigma::variant,   1 = C-scalar
	public int structHandling = 0; // Defines behavior of the closing brace of struct {}.         0 = Implied semicolon, 1 = ISO C

	//Advanced options
	public int instanceTypes = 0; // Defines how to represent instances.           0 = Integer, 1 = Pointer
	public int storageClass = 0; // Determines how instances are stored in memory. 0 = Map,     1 = List,    2 = Array

	public String definitions = "", globalLocals = "";
	public String initialization = "", cleanup = "";

	public TargetSelection targetPlatform, targetGraphics, targetAudio, targetCollision;

	public EnigmaSettings()
		{
		this(true);
		}

	private EnigmaSettings(boolean load)
		{
		if (!load) return;

		loadDefinitions();

		targetPlatform = TargetHandler.defaultPlatform;
		targetGraphics = TargetHandler.defaultGraphics;
		targetAudio = TargetHandler.defaultAudio;
		targetCollision = TargetHandler.defaultCollision;
		}

	void loadDefinitions()
		{
		definitions = fileToString(new File(EnigmaRunner.WORKDIR,"definitions.h"));
		}

	public void saveDefinitions()
		{
		writeString(new File(EnigmaRunner.WORKDIR,"definitions.h"),definitions);
		}

	String fileToString(File f)
		{
		StringBuffer sb = new StringBuffer(1024);
		try
			{
			FileReader in = new FileReader(f);
			char[] cbuf = new char[1024];
			int size = 0;
			while ((size = in.read(cbuf)) > -1)
				sb.append(cbuf,0,size);
			in.close();
			}
		catch (IOException e)
			{
			}
		return sb.toString();
		}

	void writeString(File f, String s)
		{
		try
			{
			PrintStream ps = new PrintStream(f);
			ps.print(s);
			ps.close();
			}
		catch (FileNotFoundException e)
			{
			}
		}

	private String toTargetYaml()
		{
		return "%e-yaml\n---\n"//
				+ "target-windowing: " + (targetPlatform == null ? "" : targetPlatform.id) + "\n"//
				+ "target-graphics: " + (targetGraphics == null ? "" : targetGraphics.id) + "\n"//
				+ "target-audio: " + (targetAudio == null ? "" : targetAudio.id) + "\n"//
				+ "target-collision: " + (targetCollision == null ? "" : targetCollision.id) + "\n"//
				+ "target-widget: " + "None" + "\n"//
				+ "target-networking: " + "None" + "\n";//
		}

	public SyntaxError commitToDriver()
		{
		return EnigmaRunner.DRIVER.definitionsModified(definitions,toTargetYaml());
		}

	public EnigmaSettings copy()
		{
		EnigmaSettings es = new EnigmaSettings(false);
		es.cppStrings = cppStrings;
		es.cppOperators = cppOperators;
		es.literalHandling = literalHandling;
		es.structHandling = structHandling;

		es.instanceTypes = instanceTypes;
		es.storageClass = storageClass;

		es.definitions = definitions;
		es.globalLocals = globalLocals;
		es.initialization = initialization;
		es.cleanup = cleanup;

		es.targetPlatform = targetPlatform;
		es.targetGraphics = targetGraphics;
		es.targetAudio = targetAudio;
		es.targetCollision = targetCollision;
		return es;
		}
	}
