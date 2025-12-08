export interface Review {
  id: number;
  movie: number;
  author?: string;
  content: string;
  rating?: number;
  createdAt: string;
}

export interface ReviewCreate {
  author?: string;
  content: string;
  rating: number;
}