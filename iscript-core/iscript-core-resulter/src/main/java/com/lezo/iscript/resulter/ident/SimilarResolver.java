package com.lezo.iscript.resulter.ident;

public interface SimilarResolver {
	EntitySimilar doResolve(EntityToken token, EntityToken other);
}
