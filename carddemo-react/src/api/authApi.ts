import apiClient from './apiClient';
import type { LoginRequest, LoginResponse } from '@/types';

export const authApi = {
  login: (data: LoginRequest) =>
    apiClient.post<LoginResponse>('/auth/login', data).then((r) => r.data),
};
