package com.lezo.iscript;

import lombok.Getter;
import lombok.Setter;

import com.lezo.iscript.parser.IParser;

@Getter
@Setter
public class IoGain {
	private IoSeed ioSeed;
	private IParser parser;
	private Object value;
}
