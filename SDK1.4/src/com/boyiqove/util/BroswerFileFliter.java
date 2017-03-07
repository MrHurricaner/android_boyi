package com.boyiqove.util;

import java.io.File;
import java.io.FileFilter;

public class BroswerFileFliter implements FileFilter {

	private String[] extension = { ".txt" };

	@Override
	public boolean accept(File arg0) {

		if (arg0.isDirectory() && !arg0.isHidden()) {
			return true;
		} else {

			for (String ext : extension) {
				if (arg0.getName().toLowerCase().endsWith(ext)) {
					return true;
				}
			}
		}
		return false;
	}

}
