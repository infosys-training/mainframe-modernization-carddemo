import apiClient from './apiClient';
import type { Account } from '@/types';

export const accountApi = {
  getById: (id: number) =>
    apiClient.get<Account>(`/accounts/${id}`).then((r) => r.data),

  update: (id: number, data: Partial<Account>) =>
    apiClient.put<Account>(`/accounts/${id}`, data).then((r) => r.data),
};
