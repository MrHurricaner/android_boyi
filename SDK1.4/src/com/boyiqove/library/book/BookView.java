package com.boyiqove.library.book;

public class BookView {
	public enum PageIndex {
		previous, current, next;

		public PageIndex getNext() {
			switch (this) {
			case previous:
				return current;
			case current:
				return next;
			default:
				return null;
			}
		}

		public PageIndex getPrevious() {
			switch (this) {
			case next:
				return current;
			case current:
				return previous;
			default:
				return null;
			}
		}
	}
}
