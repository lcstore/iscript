package com.lezo.iscript.resulter.cluster;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.lezo.iscript.resulter.ident.EntitySimilar;
import com.lezo.iscript.resulter.ident.EntityToken;

@Getter
@Setter
public class CenterToken {
	private EntityToken center;
	private List<EntityToken> members;
	private List<EntitySimilar> similars;

	public CenterToken(EntityToken center) {
		this(center, new ArrayList<EntityToken>());
	}

	public CenterToken(EntityToken center, List<EntityToken> members) {
		super();
		this.center = center;
		this.members = members;
	}

	public void addMember(EntityToken token) {
		if (center == token) {
			return;
		}
		this.members.add(token);
	}

	public void addSimilar(EntitySimilar similar) {
		if (similars == null) {
			similars = new ArrayList<EntitySimilar>();
		}
		this.similars.add(similar);
	}

	public void clear() {
		this.members.clear();
	}

}
