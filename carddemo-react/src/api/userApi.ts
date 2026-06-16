import apiClient from './apiClient';
import type { SecUser } from '@/types';

interface PaginatedResponse<T> {
  data: T[];
  totalPages: number;
  currentPage: number;
  totalItems: number;
}

export const userApi = {
  list: (params: { page?: number; size?: number } = {}) =>
    apiClient
      .get<PaginatedResponse<SecUser>>('/users', { params })
      .then((r) => r.data),

  create: (data: SecUser) =>
    apiClient.post<SecUser>('/users', data).then((r) => r.data),

  update: (id: string, data: Partial<SecUser>) =>
    apiClient.put<SecUser>(`/users/${id}`, data).then((r) => r.data),

  delete: (id: string) =>
    apiClient.delete(`/users/${id}`).then((r) => r.data),
};
