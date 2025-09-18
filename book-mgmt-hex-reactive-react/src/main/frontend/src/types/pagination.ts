
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
  onEdit: (id: string) => void;
  onDelete: (id: string) => void;
  onPageChange: (newPage: number) => void;
}

export interface PaginatedListMultiKeyProps<T> {
  paginatedResult: PaginatedResult<T>;
  onEdit: (id1: string, id2: string) => void;
  onDelete: (id1: string, id2: string) => void;
  onPageChange: (newPage: number) => void;
}

