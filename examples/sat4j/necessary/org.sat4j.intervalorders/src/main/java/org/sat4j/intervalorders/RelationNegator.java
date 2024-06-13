package org.sat4j.intervalorders;

import org.sat4j.pb.tools.INegator;

public class RelationNegator implements INegator {

	public boolean isNegated(Object thing) {
		return thing instanceof NegRelation;
	}

	public IRelation unNegate(Object thing) {
		return ((NegRelation) thing).relation;
	}

}
