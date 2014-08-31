package com.lezo.iscript.yeam.result;

import java.util.List;

import com.lezo.iscript.yeam.writable.ResultWritable;

public interface Convertable<E> {
	public E doConvert(List<ResultWritable> rsList);
}
