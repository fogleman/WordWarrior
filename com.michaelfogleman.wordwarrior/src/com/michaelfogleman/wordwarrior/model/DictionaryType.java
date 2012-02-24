package com.michaelfogleman.wordwarrior.model;

public enum DictionaryType {
	
	TWL06(0, "TWL06", "TWL06.txt"),
	SOWPODS(1, "SOWPODS", "sowpods.txt"),
	ODS(2, "ODS", "ods4.txt"),
	LOC2000(3, "LOC2000", null),
	MULTI(4, "MULTI", null),
	PARO(5, "PARO", "zingarelli2005.txt"),
	SWL(6, "SWL", null),
	TWL06_CLABBERS(7, "TWL06", "TWL06.txt", true);
	
	private int id;
	private String name;
	private String file;
	private boolean clabbers;
	
	DictionaryType(int id, String name, String file) {
		this(id, name, file, false);
	}
	
	DictionaryType(int id, String name, String file, boolean clabbers) {
		this.id = id;
		this.name = name;
		this.file = file;
		this.clabbers = clabbers;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFile() {
		return file;
	}
	
	public boolean isClabbers() {
		return clabbers;
	}
	
	public static DictionaryType getInstance(String name) {
		for (DictionaryType instance : DictionaryType.values()) {
			if (instance.name.equalsIgnoreCase(name)) {
				return instance;
			}
		}
		return null;
	}
	
	public static DictionaryType getInstance(int id) {
		for (DictionaryType instance : DictionaryType.values()) {
			if (instance.id == id) {
				return instance;
			}
		}
		return null;
	}

}
