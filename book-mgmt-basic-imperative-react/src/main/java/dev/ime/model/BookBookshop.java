package dev.ime.model;

import java.util.Objects;

public class BookBookshop {

	private Book book;	
	private Bookshop bookshop;	
	private Double price;	
	private Integer units;
	
	public BookBookshop() {
		super();
	}
	
	public BookBookshop(Book book, Bookshop bookshop, Double price, Integer units) {
		super();
		this.book = book;
		this.bookshop = bookshop;
		this.price = price;
		this.units = units;
	}
	
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public Bookshop getBookshop() {
		return bookshop;
	}
	public void setBookshop(Bookshop bookshop) {
		this.bookshop = bookshop;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Integer getUnits() {
		return units;
	}
	public void setUnits(Integer units) {
		this.units = units;
	}
	@Override
	public int hashCode() {
		return Objects.hash(book, bookshop, price, units);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookBookshop other = (BookBookshop) obj;
		return Objects.equals(book, other.book) && Objects.equals(bookshop, other.bookshop)
				&& Objects.equals(price, other.price) && Objects.equals(units, other.units);
	}
	@Override
	public String toString() {
		return "BookBookshop [book=" + book + ", bookshop=" + bookshop + ", price=" + price + ", units=" + units + "]";
	}
	
}
