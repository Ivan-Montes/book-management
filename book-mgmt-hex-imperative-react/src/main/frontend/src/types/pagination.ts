
export interface PaginatedResult<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface PaginatedListProps<T> {
  paginatedResult: PaginatedResult<T>;
  onEdit: (id: number) => void;
  onDelete: (id: number) => void;
  onPageChange: (newPage: number) => void;
}

export interface PaginatedListMultiKeyProps<T> {
  paginatedResult: PaginatedResult<T>;
  onEdit: (id1: number, id2: number) => void;
  onDelete: (id1: number, id2: number) => void;
  onPageChange: (newPage: number) => void;
}

