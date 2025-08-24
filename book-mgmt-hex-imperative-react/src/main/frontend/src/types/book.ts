import type { Author } from './author';

export interface Book {
  bookId: number;
  isbn: string;
  title: string;
  publisherId: number;
  publisherName: string;
  genreId: number;
  genreName: string;
  authorsId?: number[];
  authors: Author[];
}
