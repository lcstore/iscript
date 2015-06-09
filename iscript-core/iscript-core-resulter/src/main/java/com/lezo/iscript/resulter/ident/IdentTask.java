package com.lezo.iscript.resulter.ident;

import java.util.List;
import java.util.concurrent.Callable;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IdentTask implements Callable<IdentTarget> {
	private List<Identifier> identifiers;

	@Override
	public IdentTarget call() throws Exception {
		IdentTarget target = new IdentTarget();
		for (Identifier identifier : identifiers) {
			identifier.identify(target);
		}
		return target;
	}
}
