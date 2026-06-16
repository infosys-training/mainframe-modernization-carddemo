import apiClient from './apiClient';
import type { Transaction } from '@/types';

interface TransactionListParams {
  tranId?: string;
  page?: number;
  size?: number;
}

interface PaginatedResponse<T> {
  data: T[];
  totalPages: number;
  currentPage: number;
  totalItems: number;
}

export const transactionApi = {
  list: (params: TransactionListParams = {}) =>
    apiClient
      .get<PaginatedResponse<Transaction>>('/transactions', {
        params: { ...params, size: params.size ?? 10 },
      })
      .then((r) => r.data),

  getById: (id: string) =>
    apiClient.get<Transaction>(`/transactions/${id}`).then((r) => r.data),

  create: (data: Omit<Transaction, 'transactionId'>) =>
    apiClient.post<Transaction>('/transactions', data).then((r) => r.data),
};
