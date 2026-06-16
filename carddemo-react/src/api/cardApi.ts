import apiClient from './apiClient';
import type { Card } from '@/types';

interface CardListParams {
  accountId?: number;
  cardNumber?: string;
  page?: number;
  size?: number;
}

interface PaginatedResponse<T> {
  data: T[];
  totalPages: number;
  currentPage: number;
  totalItems: number;
}

export const cardApi = {
  list: (params: CardListParams = {}) =>
    apiClient
      .get<PaginatedResponse<Card>>('/cards', {
        params: { ...params, size: params.size ?? 7 },
      })
      .then((r) => r.data),

  getByNumber: (cardNumber: string) =>
    apiClient.get<Card>(`/cards/${cardNumber}`).then((r) => r.data),

  update: (cardNumber: string, data: Partial<Card>) =>
    apiClient.put<Card>(`/cards/${cardNumber}`, data).then((r) => r.data),
};
