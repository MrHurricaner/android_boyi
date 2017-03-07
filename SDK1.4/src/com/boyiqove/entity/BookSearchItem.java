package com.boyiqove.entity;

public class BookSearchItem {

	public String bookCoverLogo;
	public String bookCategory;//书籍类型
	public String bookName;
	public String bookAuthor;
	public String bookId;
	public String bookUpdateTime;
	public String bookBrief;
	public String bookStatus;
	public int bookChapterTotalSize;
	public BookSearchItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	public BookSearchItem(String bookCoverLogo, String bookCategory,
			String bookName, String bookAuthor, String bookId,
			String bookUpdateTime, String bookBrief, String bookStatus,
			int bookChapterTotalSize) {
		super();
		this.bookCoverLogo = bookCoverLogo;
		this.bookCategory = bookCategory;
		this.bookName = bookName;
		this.bookAuthor = bookAuthor;
		this.bookId = bookId;
		this.bookUpdateTime = bookUpdateTime;
		this.bookBrief = bookBrief;
		this.bookStatus = bookStatus;
		this.bookChapterTotalSize = bookChapterTotalSize;
	}
	
}
