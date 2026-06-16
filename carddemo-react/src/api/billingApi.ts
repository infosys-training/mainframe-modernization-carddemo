import apiClient from './apiClient';

interface PaymentRequest {
  accountId: number;
}

interface PaymentResponse {
  message: string;
  confirmationNumber?: string;
}

export const billingApi = {
  pay: (data: PaymentRequest) =>
    apiClient.post<PaymentResponse>('/billing/pay', data).then((r) => r.data),
};
