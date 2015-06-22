package com.lezo.iscript.resulter.ident;

public interface SimilarResolver {
	TokenSimilar doResolve(EntityToken token, EntityToken other);
}
