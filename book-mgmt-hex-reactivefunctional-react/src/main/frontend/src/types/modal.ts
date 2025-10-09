
export interface SingleSelectModalProps<T> {
  selected: T | null;
  onSelect: (selected: T) => void;
  onClose: () => void;
}

export interface MultiSelectModalProps<T> {
  selected: T[];
  onSelect: (selected: T[]) => void;
  onClose: () => void;
}
