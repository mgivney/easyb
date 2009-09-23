package org.easyb.ui.editor;

/**
 * Cosntants for easyb keywords
 * @author whiteda
 *
 */
public  enum KeywordEnum {
	SCENARIO(){
		@Override public String toString(){
			return "scenario";
		}
	},
	GIVEN(){
		@Override public String toString(){
			return "given";
		}
	},
	WHEN(){
		@Override public String toString(){
			return "when";
		}
	},
	THEN(){
		@Override public String toString(){
			return "then";
		}
	},
	AND(){
		@Override public String toString(){
			return "and";
		}
	},
	NARRATIVE(){
		@Override public String toString(){
			return "narrative";
		}
	},
	IT(){
		@Override public String toString(){
			return "it";
		}
	},
	BEFORE(){
		@Override public String toString(){
			return "before";
		}
	},
	BEFORE_EACH(){
		@Override public String toString(){
			return "before_each";
		}
	},
	AFTER(){
		@Override public String toString(){
			return "after";
		}
	},
	AFTER_EACH(){
		@Override public String toString(){
			return "after_each";
		}
	},
	ENSURE_THROWS(){
		@Override public String toString(){
			return "ensureThrows";
		}
	},
	DESCRIPTION(){
		@Override public String toString(){
			return "description";
		}
	},
	ENSURE(){
		@Override public String toString(){
			return "ensure";
		}
	},
	ENSURE_FAILS(){
		@Override public String toString(){
			return "ensureFails"; 
		}
	},
	CONTAINS(){
		@Override public String toString(){
			return "contains"; 
		}
	},
	HAS(){
		@Override public String toString(){
			return "has"; 
		}
	};
	
	public static String[] toStringArray(){
		return new String[]{
				SCENARIO.toString(),
				GIVEN.toString(),
				WHEN.toString(),
				THEN.toString(),
				AND.toString(),
				NARRATIVE.toString(),
				IT.toString(),
				BEFORE.toString(),
				BEFORE_EACH.toString(),
				AFTER.toString(),
				AFTER_EACH.toString(),
				ENSURE_THROWS.toString(),
				DESCRIPTION.toString(),
				ENSURE.toString(),
				ENSURE_FAILS.toString()
		};
	}
}
