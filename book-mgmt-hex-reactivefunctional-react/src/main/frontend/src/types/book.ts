import type { Author } from './author';

export interface Book {
  bookId: string;
  isbn: string;
  title: string;
  publisherId: string;
  publisherName: string;
  genreId: string;
  genreName: string;
  authorsId?: string[];
  authors: Author[];
}
