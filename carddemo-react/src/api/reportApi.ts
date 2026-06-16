import apiClient from './apiClient';

interface ReportRequest {
  type: 'monthly' | 'yearly' | 'custom';
  startDate?: string;
  endDate?: string;
}

interface ReportResponse {
  message: string;
  reportId?: string;
}

export const reportApi = {
  generate: (data: ReportRequest) =>
    apiClient.post<ReportResponse>('/reports', data).then((r) => r.data),
};
